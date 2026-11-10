<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" layout="vertical">
    <view class="fc-signature">
      <view v-if="signatureUrl" class="fc-signature__preview">
        <image class="fc-signature__image" :src="signatureUrl" mode="aspectFit" />
      </view>

      <template v-if="!disabled">
        <wd-signature
          :pen-color="penColor"
          :line-width="lineWidth"
          :confirm-text="confirmText"
          :clear-text="clearText"
          :revoke-text="revokeText"
          :restore-text="restoreText"
          :file-type="fileType"
          :quality="quality"
          :export-scale="exportScale"
          :height="height"
          :width="width"
          :background-color="backgroundColor"
          :disable-scroll="disableScroll"
          :enable-history="enableHistory"
          :pressure="pressure"
          :max-width="maxWidth"
          :min-width="minWidth"
          :min-speed="minSpeed"
          @confirm="handleConfirm"
          @clear="handleClear"
        />
        <view v-if="uploading" class="fc-signature__tip">
          签名上传中...
        </view>
      </template>

      <view v-else-if="!signatureUrl" class="fc-signature__empty">
        暂无签名
      </view>
    </view>
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { SignatureResult } from '@wot-ui/ui/components/wd-signature/types'
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed, ref } from 'vue'
import { uploadFileFromPath } from '@/utils/uploadFile'

defineOptions({
  name: 'FcSignature',
})

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
  'clear': []
  'confirm': [value: any]
}>()

const uploading = ref(false)
const signatureUrl = computed(() => normalizeSignatureUrl(props.modelValue))
const penColor = computed(() => props.rule.props?.penColor || '#000')
const lineWidth = computed(() => props.rule.props?.lineWidth || 3)
const confirmText = computed(() => props.rule.props?.confirmText || '保存签名')
const clearText = computed(() => props.rule.props?.clearText || '清除')
const revokeText = computed(() => props.rule.props?.revokeText || props.rule.props?.undoText)
const restoreText = computed(() => props.rule.props?.restoreText || props.rule.props?.redoText)
const fileType = computed<'png' | 'jpg'>(() => props.rule.props?.fileType === 'jpg' ? 'jpg' : 'png')
const quality = computed(() => props.rule.props?.quality ?? 1)
const exportScale = computed(() => props.rule.props?.exportScale ?? 1)
const height = computed(() => props.rule.props?.height || props.rule.props?.canvasHeight || '360rpx')
const width = computed(() => props.rule.props?.width || props.rule.props?.canvasWidth || '100%')
const backgroundColor = computed(() => props.rule.props?.backgroundColor || '#fff')
const disableScroll = computed(() => props.rule.props?.disableScroll ?? true)
const enableHistory = computed(() => props.rule.props?.enableHistory ?? false)
const pressure = computed(() => props.rule.props?.pressure ?? false)
const maxWidth = computed(() => props.rule.props?.maxWidth || 6)
const minWidth = computed(() => props.rule.props?.minWidth || 2)
const minSpeed = computed(() => props.rule.props?.minSpeed || 1.5)
const uploadDirectory = computed(() => props.rule.props?.uploadDirectory || props.rule.props?.directory || 'bpm/signature')
const uploadEnabled = computed(() => props.rule.props?.upload !== false && props.rule.props?.autoUpload !== false)
const uploadMimeType = computed(() => fileType.value === 'jpg' ? 'image/jpeg' : 'image/png')

async function handleConfirm(result: SignatureResult) {
  if (uploading.value) {
    return
  }
  if (!result.success || !result.tempFilePath) {
    uni.showToast({
      icon: 'none',
      title: '签名生成失败',
    })
    return
  }

  uploading.value = true
  try {
    const value = uploadEnabled.value
      ? await uploadFileFromPath(result.tempFilePath, uploadDirectory.value, uploadMimeType.value)
      : result.tempFilePath
    emitValue(value)
    emit('confirm', value)
  } catch (error) {
    console.error('签名上传失败:', error)
    uni.showToast({
      icon: 'none',
      title: '签名上传失败',
    })
  } finally {
    uploading.value = false
  }
}

function handleClear() {
  emitValue('')
  emit('clear')
}

function emitValue(value: string) {
  emit('update:modelValue', value)
  emit('change', value)
}

function normalizeSignatureUrl(value: any): string {
  if (!value) {
    return ''
  }
  if (Array.isArray(value)) {
    return normalizeSignatureUrl(value[0])
  }
  if (typeof value === 'object') {
    return normalizeSignatureUrl(value.url || value.path || value.fileUrl || value.src)
  }
  return String(value)
}
</script>

<style lang="scss" scoped>
.fc-signature {
  width: 100%;
}

.fc-signature__preview {
  margin-bottom: 16rpx;
}

.fc-signature__image {
  width: 100%;
  height: 220rpx;
  border: 1px solid #eee;
  border-radius: 8rpx;
  background: #fafafa;
}

.fc-signature__tip {
  color: #999;
  font-size: 24rpx;
  line-height: 34rpx;
  margin-top: 12rpx;
  text-align: right;
}

.fc-signature__empty {
  color: #999;
  font-size: 26rpx;
  padding: 8rpx 0;
}
</style>
