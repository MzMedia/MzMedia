<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" layout="vertical">
    <!-- 统一复用封装的 yd-upload 三件套，与静态表单保持同一套组件（对齐 PC + element-plus 的做法） -->
    <yd-upload-img
      v-if="isImage && single"
      :model-value="singleValue"
      :directory="directory"
      :disabled="disabled"
      :file-size="fileSize"
      :file-type="fileType"
      @update:model-value="onUpdate"
      @success="emit('success', $event)"
      @fail="emit('fail', $event)"
      @remove="emit('remove', $event)"
    />
    <yd-upload-imgs
      v-else-if="isImage"
      :model-value="arrayValue"
      :directory="directory"
      :disabled="disabled"
      :limit="configuredLimit"
      :file-size="fileSize"
      :file-type="fileType"
      @update:model-value="onUpdate"
      @success="emit('success', $event)"
      @fail="emit('fail', $event)"
      @remove="emit('remove', $event)"
    />
    <yd-upload-file
      v-else
      :model-value="single ? singleValue : arrayValue"
      :accept="accept"
      :directory="directory"
      :disabled="disabled"
      :limit="single ? 1 : configuredLimit"
      :file-size="fileSize"
      :file-type="fileType"
      @update:model-value="onUpdate"
      @success="emit('success', $event)"
      @fail="emit('fail', $event)"
      @remove="emit('remove', $event)"
    />
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { UploadFileType } from '@wot-ui/ui/components/wd-upload/types'
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
  'success': [value: any]
  'fail': [value: any]
  'remove': [value: any]
}>()

const IMAGE_UPLOAD_TYPES = new Set(['ImageUpload', 'ImagesUpload', 'UploadImg', 'UploadImgs', 'uploadImage', 'uploadImages'])
const SINGLE_UPLOAD_TYPES = new Set(['ImageUpload', 'UploadImg', 'UploadFile', 'uploadImage'])
const WOT_ACCEPT_TYPES = new Set<UploadFileType>(['image', 'video', 'media', 'all', 'file'])

// ===== rule → 组件 props 的映射（沿用 form-create 的字段配置，输出给 yd-upload 三件套）=====
const isImage = computed(() => IMAGE_UPLOAD_TYPES.has(props.rule.type))
const configuredLimit = computed(() => normalizeLimit(props.rule.props?.maxNumber ?? props.rule.props?.maxCount ?? props.rule.props?.limit))
const single = computed(() => {
  if (props.rule.props?.multiple || (configuredLimit.value && configuredLimit.value > 1)) {
    return false
  }
  return SINGLE_UPLOAD_TYPES.has(props.rule.type) || configuredLimit.value === 1
})
const directory = computed(() => props.rule.props?.directory || props.rule.props?.uploadDirectory || '')
const disabled = computed(() => props.disabled || !!props.rule.props?.disabled)
const accept = computed<UploadFileType>(() => normalizeAccept(props.rule.props?.acceptType || props.rule.props?.accept) || 'all')
const fileSize = computed(() => {
  const size = Number(props.rule.props?.maxSize ?? props.rule.props?.fileSize)
  return Number.isFinite(size) && size > 0 ? size : undefined
})
const fileType = computed(() => normalizeTypeList(props.rule.props?.fileType ?? props.rule.props?.extension ?? props.rule.props?.accept))

// ===== 表单值 ↔ 组件值的归一化 =====
const arrayValue = computed(() => toUrlArray(props.modelValue))
const singleValue = computed(() => arrayValue.value[0] || '')

function toUrlArray(value: any): string[] {
  if (Array.isArray(value)) {
    return value.filter(Boolean)
  }
  if (typeof value === 'string') {
    return value ? value.split(',').filter(Boolean) : []
  }
  return value ? [value] : []
}

function onUpdate(value: string | string[]) {
  emitValue(Array.isArray(value) ? value : (value ? [value] : []))
}

function emitValue(urls: string[]) {
  const value = single.value ? (urls[0] || '') : urls
  emit('update:modelValue', value)
  emit('change', value)
}

function normalizeLimit(value: any) {
  const limit = Number(value)
  return Number.isFinite(limit) && limit > 0 ? limit : undefined
}

function normalizeAccept(value: any): UploadFileType | undefined {
  if (typeof value !== 'string') {
    return undefined
  }
  const accept = value.toLowerCase() as UploadFileType
  return WOT_ACCEPT_TYPES.has(accept) ? accept : undefined
}

// 透传给 yd-upload 的 fileType：保留原值（MIME 或扩展名），由 yd-upload 内核做兼容匹配
function normalizeTypeList(value: any): string[] | undefined {
  const values = Array.isArray(value)
    ? value
    : typeof value === 'string'
      ? value.split(',')
      : []
  const list = values
    .map(item => String(item || '').trim())
    .filter(item => item && !WOT_ACCEPT_TYPES.has(item.toLowerCase() as UploadFileType))
  return list.length > 0 ? list : undefined
}
</script>
