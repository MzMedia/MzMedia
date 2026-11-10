<template>
  <yd-upload
    :model-value="modelValue"
    accept="image"
    :multiple="true"
    :limit="limit"
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
// 多图上传：对外 v-model 为访问地址数组（string[]），对齐 PC 端 UploadImgs
const props = withDefaults(defineProps<{
  modelValue?: string[] // 已上传图片的访问地址数组
  directory?: string // 上传目录
  limit?: number // 最大上传数量
  disabled?: boolean // 是否禁用
  fileSize?: number // 图片大小限制（MB），不传则不限制
  fileType?: string[] // 图片类型限制，为空则不限制
}>(), {
  modelValue: () => [],
  directory: '',
  limit: 5,
  disabled: false,
  // 默认不限制：移动端 chooseImage 已限定图片，且拿不到可靠 MIME；如需限制传扩展名如 ['png','jpg']
  fileType: () => [],
})

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
  'uploaded': [value: string]
  'success': [value: any]
  'fail': [value: any]
  'remove': [value: any]
}>()

/** 转发地址数组更新 */
function handleUpdate(urls: string[]) {
  emit('update:modelValue', urls)
}
</script>
