/**
 * 微信小程序上传脚本
 *
 * 使用示例：
 *   pnpm upload:mp-weixin --dry-run
 *   pnpm upload:mp-weixin --version=1.0.1 --desc="修复登录问题" --robot=2
 *   WECHAT_UPLOAD_PRIVATE_KEY_PATH=/path/private.key pnpm upload:mp-weixin --dry-run
 */

import { execSync } from 'node:child_process'
import fs from 'node:fs'
import path from 'node:path'
import process from 'node:process'
import { fileURLToPath } from 'node:url'
import ci from 'miniprogram-ci'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const ROOT_DIR = path.resolve(__dirname, '..')
const DEFAULT_BUILD_COMMAND = 'pnpm build:mp:prod'
const DEFAULT_PROJECT_PATH = path.resolve(ROOT_DIR, 'dist', 'build', 'mp-weixin')

function readJson(filePath) {
  return JSON.parse(fs.readFileSync(filePath, 'utf-8'))
}

function parseEnvContent(content) {
  const result = {}
  content.split('\n').forEach((line) => {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith('#')) {
      return
    }
    const [key, ...valueParts] = trimmed.split('=')
    if (!key) {
      return
    }
    result[key.trim()] = valueParts.join('=').trim().replace(/^['"]|['"]$/g, '')
  })
  return result
}

function loadEnvFile(mode = 'production') {
  const env = {}
  const envFiles = [
    path.resolve(ROOT_DIR, 'env', '.env'),
    path.resolve(ROOT_DIR, 'env', `.env.${mode}`),
  ]

  envFiles.forEach((filePath) => {
    if (fs.existsSync(filePath)) {
      Object.assign(env, parseEnvContent(fs.readFileSync(filePath, 'utf-8')))
    }
  })

  return {
    ...env,
    ...process.env,
  }
}

function readPackageVersion() {
  try {
    const pkg = readJson(path.resolve(ROOT_DIR, 'package.json'))
    return pkg['yudao-version'] || pkg.version || '1.0.0'
  } catch {
    return '1.0.0'
  }
}

function readGitCommitMessage() {
  try {
    return execSync('git log -1 --pretty="%an: %s"', {
      cwd: ROOT_DIR,
      encoding: 'utf-8',
    }).trim()
  } catch {
    return ''
  }
}

function parseArgs() {
  const args = process.argv.slice(2)
  const params = {
    desc: '',
    dryRun: false,
    privateKeyPath: '',
    robot: undefined,
    skipBuild: false,
    version: '',
  }

  args.forEach((arg) => {
    if (arg === '--dry-run') {
      params.dryRun = true
    } else if (arg === '--skip-build') {
      params.skipBuild = true
    } else if (arg.startsWith('--version=')) {
      params.version = arg.slice('--version='.length)
    } else if (arg.startsWith('--desc=')) {
      params.desc = arg.slice('--desc='.length)
    } else if (arg.startsWith('--robot=')) {
      params.robot = Number.parseInt(arg.slice('--robot='.length), 10)
    } else if (arg.startsWith('--private-key-path=')) {
      params.privateKeyPath = arg.slice('--private-key-path='.length)
    }
  })

  return params
}

function resolvePrivateKeyPath(appid, params, env) {
  const candidates = [
    params.privateKeyPath,
    env.WECHAT_UPLOAD_PRIVATE_KEY_PATH,
    path.resolve(ROOT_DIR, `private.${appid}.key`),
    path.resolve(ROOT_DIR, 'private.key'),
  ].filter(Boolean).map(candidate => path.isAbsolute(candidate) ? candidate : path.resolve(ROOT_DIR, candidate))

  const keyPath = candidates.find(candidate => fs.existsSync(candidate))
  if (keyPath) {
    return path.resolve(keyPath)
  }

  throw new Error(
    `未找到微信上传私钥。请通过 WECHAT_UPLOAD_PRIVATE_KEY_PATH 指定私钥路径，或在项目根目录放置 private.${appid}.key`,
  )
}

function assertRobot(robot) {
  if (!Number.isInteger(robot) || robot < 1 || robot > 30) {
    throw new Error('robot 必须是 1 到 30 之间的整数')
  }
}

function buildMiniProgram(skipBuild) {
  if (skipBuild) {
    console.log('跳过构建，使用已有 dist/build/mp-weixin')
    return
  }

  console.log('开始构建微信小程序，已设置 SKIP_OPEN_DEVTOOLS=true')
  execSync(DEFAULT_BUILD_COMMAND, {
    cwd: ROOT_DIR,
    env: {
      ...process.env,
      SKIP_OPEN_DEVTOOLS: 'true',
    },
    stdio: 'inherit',
  })
}

async function main() {
  const env = loadEnvFile('production')
  const params = parseArgs()
  const appid = env.VITE_WX_APPID

  if (!appid) {
    throw new Error('未找到 VITE_WX_APPID，请检查 env/.env 或 env/.env.production')
  }

  const version = params.version || env.WECHAT_UPLOAD_VERSION || readPackageVersion()
  const desc = params.desc || env.WECHAT_UPLOAD_DESC || readGitCommitMessage() || `上传于 ${new Date().toLocaleString('zh-CN')}`
  const robot = params.robot ?? Number.parseInt(env.WECHAT_UPLOAD_ROBOT || '1', 10)
  assertRobot(robot)

  const privateKeyPath = resolvePrivateKeyPath(appid, params, env)

  console.log('\n微信小程序上传参数')
  console.log(`- AppID: ${appid}`)
  console.log(`- 版本号: ${version}`)
  console.log(`- 描述: ${desc}`)
  console.log(`- 机器人: ${robot}`)
  console.log(`- 私钥: ${privateKeyPath}`)
  console.log(`- dry-run: ${params.dryRun ? '是' : '否'}`)

  buildMiniProgram(params.skipBuild)

  if (!fs.existsSync(DEFAULT_PROJECT_PATH)) {
    throw new Error(`构建产物不存在: ${DEFAULT_PROJECT_PATH}`)
  }

  if (params.dryRun) {
    console.log('\ndry-run 模式：已完成参数、私钥和构建产物校验，不执行上传。')
    return
  }

  const project = new ci.Project({
    appid,
    ignores: ['node_modules/**/*'],
    privateKeyPath,
    projectPath: DEFAULT_PROJECT_PATH,
    type: 'miniProgram',
  })

  await ci.upload({
    desc,
    project,
    robot,
    setting: {
      autoPrefixWXSS: true,
      es6: true,
      es7: true,
      minify: true,
      minifyJS: true,
      minifyWXML: true,
      minifyWXSS: true,
    },
    version,
    onProgressUpdate(task) {
      if (task?._status === 'done') {
        console.log(task._msg)
      }
    },
  })

  console.log('\n微信小程序上传成功')
}

main().catch((error) => {
  console.error('\n微信小程序上传失败:', error.message)
  process.exit(1)
})
