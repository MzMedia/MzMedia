<template>
  <div
    v-bind="attrs"
    class="w-full"
    :class="disabled ? 'cursor-not-allowed' : 'cursor-pointer'"
    @click="handleClick"
    @mouseenter="hovering = true"
    @mouseleave="hovering = false"
  >
    <el-tooltip :content="displayLabel" :disabled="!displayLabel" placement="top" :show-after="500">
      <el-input
        :class="disabled ? 'is-select-disabled' : 'is-select-clickable'"
        :disabled="disabled"
        :model-value="displayLabel"
        :placeholder="placeholder"
        :suffix-icon="suffixIcon"
        readonly
      />
    </el-tooltip>
  </div>
  <HrmEmployeeSelectDialog
    ref="dialogRef"
    :entry-status="entryStatus"
    :multiple="multiple"
    :selectable="selectable"
    :title="title"
    @selected="handleSelected"
  />
</template>

<script lang="ts" setup>
import { CircleClose, Search } from '@element-plus/icons-vue'
import * as EmployeeApi from '@/api/hrm/employee'
import HrmEmployeeSelectDialog from './HrmEmployeeSelectDialog.vue'

defineOptions({ name: 'HrmEmployeeSelect', inheritAttrs: false })

type EmployeeSelectItem = EmployeeApi.HrmEmployeeVO & { id: number }

const attrs = useAttrs() // 透传属性
const props = withDefaults(
  defineProps<{
    modelValue?: number | number[]
    multiple?: boolean
    disabled?: boolean
    disabledIds?: number[]
    entryStatus?: number
    clearable?: boolean
    placeholder?: string
    selectable?: (employee: EmployeeApi.HrmEmployeeVO) => boolean
    title?: string
  }>(),
  {
    multiple: false,
    disabled: false,
    disabledIds: () => [],
    clearable: true,
    placeholder: '请选择员工',
    title: '选择员工'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | number[] | undefined]
  change: [value: EmployeeApi.HrmEmployeeVO | EmployeeApi.HrmEmployeeVO[] | undefined]
}>() // 定义 modelValue 更新和 change 事件

const dialogRef = ref<InstanceType<typeof HrmEmployeeSelectDialog>>() // 员工选择弹窗 Ref
const hovering = ref(false) // 鼠标是否悬停
const selectedItems = ref<EmployeeSelectItem[]>([]) // 当前选中的员工
const displayLabel = computed(() => selectedItems.value.map((item) => item.name).join('、')) // 选择器展示文本

/** 是否显示清除按钮 */
const showClear = computed(() =>
  Boolean(
    props.clearable && !props.disabled && hovering.value && normalizeIds(props.modelValue).length
  )
)
const suffixIcon = computed(() => (showClear.value ? CircleClose : Search)) // 选择器后缀图标

/** 将选择值统一转换为员工编号数组 */
function normalizeIds(value: number | number[] | undefined) {
  return Array.isArray(value) ? value : value == null ? [] : [value]
}

/** 回显已选择的员工 */
async function resolveItems(value: number | number[] | undefined) {
  const ids = normalizeIds(value)
  if (!ids.length) {
    selectedItems.value = []
    return
  }
  if (
    selectedItems.value.length === ids.length &&
    selectedItems.value.every((item, index) => item.id === ids[index])
  ) {
    return
  }
  try {
    const items = await EmployeeApi.getEmployeeSimpleList(ids)
    const itemMap = new Map(items.map((item) => [item.id, item]))
    selectedItems.value = ids
      .map((id) => itemMap.get(id))
      .filter((item): item is EmployeeSelectItem => item?.id != null)
  } catch {
    selectedItems.value = []
  }
}

/** 监听选中员工变化 */
watch(() => props.modelValue, resolveItems, { deep: true, immediate: true })

/** 打开员工选择弹窗，或清空已选员工 */
function handleClick(event: MouseEvent) {
  if (props.disabled) {
    return
  }
  const target = event.target as HTMLElement
  if (showClear.value && target.closest('.el-input__suffix')) {
    event.stopPropagation()
    selectedItems.value = []
    emit('update:modelValue', props.multiple ? [] : undefined)
    emit('change', props.multiple ? [] : undefined)
    return
  }
  dialogRef.value?.open(normalizeIds(props.modelValue), props.disabledIds)
}

/** 处理员工选择结果 */
function handleSelected(rows: EmployeeApi.HrmEmployeeVO[]) {
  const selectedRows = rows.filter((item): item is EmployeeSelectItem => item.id != null)
  selectedItems.value = selectedRows
  if (props.multiple) {
    emit(
      'update:modelValue',
      selectedRows.map((item) => item.id)
    )
    emit('change', selectedRows)
    return
  }
  emit('update:modelValue', selectedRows[0]?.id)
  emit('change', selectedRows[0])
}
</script>

<style lang="scss" scoped>
.is-select-clickable {
  :deep(.el-input__wrapper),
  :deep(.el-input__inner) {
    cursor: pointer;
  }
}

.is-select-disabled {
  :deep(.el-input__wrapper),
  :deep(.el-input__inner) {
    cursor: not-allowed;
  }
}
</style>
