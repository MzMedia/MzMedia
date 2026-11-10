/**
 * 注入 Eruda 移动端调试面板
 * @param {object} options 配置项
 * @param {boolean} [options.open] 是否开启
 * @param {object} [options.erudaOptions] Eruda 配置
 * @param {string} [options.erudaUrl] Eruda CDN 地址
 */
export default function vitePluginEruda(options = {}) {
  const {
    open = true,
    erudaOptions = {},
    erudaUrl = 'https://cdn.jsdelivr.net/npm/eruda',
  } = options

  return {
    name: 'vite-plugin-eruda',

    transformIndexHtml(html) {
      if (!open) {
        return html
      }

      return {
        html,
        tags: [
          {
            tag: 'script',
            attrs: {
              src: erudaUrl,
            },
            injectTo: 'head',
          },
          {
            tag: 'script',
            children: `eruda.init(${JSON.stringify(erudaOptions)});`,
            injectTo: 'head',
          },
        ],
      }
    },
  }
}
