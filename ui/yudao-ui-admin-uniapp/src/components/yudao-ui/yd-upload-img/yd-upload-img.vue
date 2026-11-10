<template>
  <yd-upload
    :model-value="list"
    accept="image"
    :multiple="false"
    :limit="1"
    :directory="directory"
    :disabled="disabled"
    :file-size="fileSize"
    :file-type="fileType"
    @update:model-value="handleUpdate"
    @uploaded="emit('uploaded', $event)"
    @success="emit('success', $event)"
    @fail="emit('fail', $event)"
    @remove="emit('remove', $event)"
  />
</template>

<script lang="ts" setup>
import { computed } from 'vue'

// 单图上传：对外 v-model 为单个访问地址（string），交互与 yd-upload-imgs 完全一致，仅限一张
const props = withDefaults(defineProps<{
  modelValue?: string // 已上传图片的访问地址
  directory?: string // 上传目录
  disabled?: boolean // 是否禁用
  fileSize?: number // 图片大小限制（MB），不传则不限制
  fileType?: string[] // 图片类型限制，为空则不限制
}>(), {
  modelValue: '',
  directory: '',
  disabled: false,
  fileType: () => [],
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'uploaded': [value: string]
  'success': [value: any]
  'fail': [value: any]
  'remove': [value: any]
}>()

// 单图 → 数组（适配内核 yd-upload）
const list = computed(() => (props.modelValue ? [props.modelValue] : []))

/** 数组 → 单图 */
function handleUpdate(urls: string[]) {
  emit('update:modelValue', urls[0] || '')
}
</script>
