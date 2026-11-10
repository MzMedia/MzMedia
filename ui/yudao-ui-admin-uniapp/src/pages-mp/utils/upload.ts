import type { Material } from '@/api/mp/material'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import { uploadPermanentMaterial, uploadTemporaryMaterial } from '@/api/mp/material'

export type MaterialUploadType = 'image' | 'voice' | 'video' | 'thumb'

interface UploadLimit {
  label: string
  maxSize: number
  types: string[]
  extensions: string[]
}

interface NormalizedFile {
  path: string
  name: string
  size: number
  type: string
}

interface UploadOptions {
  accountId: number
  permanent?: boolean // 默认 true（永久素材）；自动回复/客服/菜单回复用临时素材传 false
  title?: string // 永久视频素材必填
  introduction?: string // 永久视频素材必填
}

const IMAGE_LIMIT: UploadLimit = {
  label: '图片',
  maxSize: 2 * 1024 * 1024,
  types: ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/jpg'],
  extensions: ['jpg', 'jpeg', 'png', 'gif', 'bmp'],
}

/** 各素材类型上传限制 */
export const UPLOAD_LIMITS: Record<MaterialUploadType, UploadLimit> = {
  image: IMAGE_LIMIT,
  thumb: IMAGE_LIMIT,
  voice: {
    label: '语音',
    maxSize: 2 * 1024 * 1024,
    types: ['audio/mp3', 'audio/mpeg', 'audio/wma', 'audio/wav', 'audio/amr'],
    extensions: ['mp3', 'wma', 'wav', 'amr'],
  },
  video: {
    label: '视频',
    maxSize: 10 * 1024 * 1024,
    types: ['video/mp4'],
    extensions: ['mp4'],
  },
}

/** 公众号素材选择 + 上传 */
export function useMaterialUpload() {
  const toast = useToast()
  const uploading = ref(false)

  /** 选择并上传素材，返回上传成功的素材（取消 / 失败返回 undefined） */
  async function chooseAndUpload(type: MaterialUploadType, options: UploadOptions): Promise<Material | undefined> {
    if (!options.accountId) {
      toast.show('请先选择公众号')
      return undefined
    }
    const file = await chooseFile(type)
    if (!file || !validateFile(type, file)) {
      return undefined
    }
    uploading.value = true
    try {
      // thumb 仅用于临时素材（音乐缩略图），永久上传按 image 处理
      if (options.permanent !== false) {
        // 仅视频素材带 title/introduction；非视频不带这两个 key，避免 H5 下 formData 把 undefined 写成字符串 "undefined"
        const data: { accountId: number, type: string, title?: string, introduction?: string } = {
          accountId: options.accountId,
          type: type === 'thumb' ? 'image' : type,
        }
        if (options.title !== undefined) {
          data.title = options.title
        }
        if (options.introduction !== undefined) {
          data.introduction = options.introduction
        }
        return await uploadPermanentMaterial(file.path, data)
      }
      return await uploadTemporaryMaterial(file.path, {
        accountId: options.accountId,
        type,
      })
    } catch (error) {
      toast.show(getUploadErrorMessage(error))
      return undefined
    } finally {
      uploading.value = false
    }
  }

  /** 按类型选择文件 */
  function chooseFile(type: MaterialUploadType): Promise<NormalizedFile | undefined> {
    if (type === 'video') {
      return chooseVideoFile()
    }
    if (type === 'voice') {
      return chooseVoiceFile()
    }
    return chooseImageFile()
  }

  /** 选择图片 */
  function chooseImageFile() {
    return new Promise<NormalizedFile | undefined>((resolve) => {
      uni.chooseImage({
        count: 1,
        success: res => resolve(normalizeSelectedFile(res)),
        fail: () => resolve(undefined),
      })
    })
  }

  /** 选择视频 */
  function chooseVideoFile() {
    return new Promise<NormalizedFile | undefined>((resolve) => {
      // #ifdef H5
      // H5 端 chooseVideo 的 extension 过滤不稳定，优先使用 chooseFile 约束格式
      const chooseFn = (uni as any).chooseFile
      if (chooseFn) {
        chooseFn({
          count: 1,
          type: 'video',
          extension: UPLOAD_LIMITS.video.extensions.map(item => `.${item}`),
          success: (res: any) => resolve(normalizeSelectedFile(res)),
          fail: () => resolve(undefined),
        })
        return
      }
      // #endif
      uni.chooseVideo({
        extension: UPLOAD_LIMITS.video.extensions.map(item => `.${item}`),
        success: res => resolve(normalizeSelectedFile(res)),
        fail: () => resolve(undefined),
      })
    })
  }

  /** 选择语音（App 端无通用文件选择器，给引导文案） */
  function chooseVoiceFile() {
    return new Promise<NormalizedFile | undefined>((resolve) => {
      const chooseFn = (uni as any).chooseFile || (uni as any).chooseMessageFile
      if (!chooseFn) {
        toast.show('当前端不支持选择语音文件，请在 H5 端上传')
        resolve(undefined)
        return
      }
      chooseFn({
        count: 1,
        type: (uni as any).chooseFile ? 'all' : 'file',
        extension: UPLOAD_LIMITS.voice.extensions.map(item => `.${item}`),
        success: (res: any) => resolve(normalizeSelectedFile(res)),
        fail: () => resolve(undefined),
      })
    })
  }

  /** 规范选择文件结果 */
  function normalizeSelectedFile(res: any): NormalizedFile | undefined {
    const tempFiles = Array.isArray(res.tempFiles) ? res.tempFiles : [res.tempFiles]
    const tempFilePaths = Array.isArray(res.tempFilePaths) ? res.tempFilePaths : [res.tempFilePaths]
    const file = tempFiles.find(Boolean) || {}
    const path = res.tempFilePath || file.tempFilePath || file.path || tempFilePaths.find(Boolean)
    if (!path) {
      toast.show('未获取到文件路径')
      return undefined
    }
    return {
      path,
      name: file.name || path,
      size: file.size || res.size || 0,
      type: file.type || res.type || '',
    }
  }

  /** 校验上传文件格式与大小 */
  function validateFile(type: MaterialUploadType, file: NormalizedFile) {
    const limit = UPLOAD_LIMITS[type]
    const mimeType = file.type?.toLowerCase()
    const extension = file.name?.split('.').pop()?.toLowerCase()
    const isValidType = (!!mimeType && limit.types.includes(mimeType))
      || (!!extension && limit.extensions.includes(extension))
    if (!isValidType) {
      toast.show(`上传${limit.label}格式不对`)
      return false
    }
    if (file.size && file.size > limit.maxSize) {
      toast.show(`上传${limit.label}大小不能超过${limit.maxSize / 1024 / 1024}M`)
      return false
    }
    return true
  }

  return { uploading, chooseAndUpload }
}

/** 获取上传失败提示 */
function getUploadErrorMessage(error: any) {
  if (!error) {
    return '上传失败，请重试'
  }
  if (typeof error === 'string') {
    return error
  }
  const message = error.msg || error.message || error.errMsg || error.data?.msg || error.data?.message
  return message || '上传失败，请重试'
}
