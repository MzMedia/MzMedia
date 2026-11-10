<template>
  <!-- 数据源选择器：自加载数据源列表，默认选中第一项 -->
  <yd-form-picker
    :model-value="modelValue"
    :label="label"
    :prop="prop"
    :columns="list"
    label-key="name"
    value-key="id"
    @confirm="handleConfirm"
  />
</template>

<script lang="ts" setup>
import type { DataSourceConfig } from '@/api/infra/data-source-config'
import { onMounted, ref } from 'vue'
import { getDataSourceConfigList } from '@/api/infra/data-source-config'

const props = withDefaults(defineProps<{
  modelValue?: number // 选中的数据源编号
  label?: string // 字段标题
  prop?: string // wd-form 校验字段名
}>(), {
  label: '数据源',
  prop: '',
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
  'change': [value: number]
}>()

const list = ref<DataSourceConfig[]>([]) // 数据源列表

/** 选中数据源（同步 v-model + 抛 change） */
function handleConfirm(value: number) {
  emit('update:modelValue', value)
  emit('change', value)
}

/** 初始化：加载列表，未选中时默认第一项 */
onMounted(async () => {
  list.value = await getDataSourceConfigList()
  if (props.modelValue == null && list.value[0]?.id != null) {
    handleConfirm(list.value[0].id)
  }
})
</script>
