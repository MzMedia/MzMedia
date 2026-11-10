<template>
  <div class="w-full">
    <div class="mb-12px flex items-center gap-12px">
      <el-button plain type="primary" @click="addCategory">
        <Icon class="mr-5px" icon="ep:plus" />新增分类
      </el-button>
      <slot name="actions"></slot>
    </div>
    <el-table :data="displayOptions" :max-height="props.maxHeight" border row-key="code">
      <el-table-column align="center" label="类型" width="80">
        <template #default="scope">
          <el-tag
            :type="scope.row.type === HrmSalarySlipTemplateOptionType.CATEGORY ? 'primary' : 'info'"
          >
            {{ scope.row.type === HrmSalarySlipTemplateOptionType.CATEGORY ? '分类' : '工资项' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="名称" min-width="160">
        <template #default="scope">
          <el-input v-model="scope.row.name" maxlength="64" placeholder="请输入名称" />
        </template>
      </el-table-column>
      <el-table-column label="所属分类" min-width="150">
        <template #default="scope">
          <el-select
            v-if="scope.row.type === HrmSalarySlipTemplateOptionType.ITEM"
            v-model="scope.row.parentCode"
            clearable
            placeholder="不分类"
          >
            <el-option
              v-for="category in categoryOptions"
              :key="category.code"
              :label="category.name"
              :value="category.code!"
            />
          </el-select>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="显示" width="80">
        <template #default="scope">
          <el-switch
            :model-value="!scope.row.hidden"
            :disabled="scope.row.code === HrmSalaryOptionCode.REAL_PAY"
            @change="handleVisibleChange(scope.row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="190">
        <template #default="scope">
          <el-input
            v-model="scope.row.remark"
            clearable
            maxlength="255"
            placeholder="展示在工资条提示中"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="138">
        <template #default="scope">
          <el-button link :disabled="isFirstOption(scope.row)" @click="moveOption(scope.row, -1)">
            <Icon icon="ep:top" />
          </el-button>
          <el-button link :disabled="isLastOption(scope.row)" @click="moveOption(scope.row, 1)">
            <Icon icon="ep:bottom" />
          </el-button>
          <el-button
            link
            type="danger"
            :disabled="scope.row.code === HrmSalaryOptionCode.REAL_PAY"
            @click="removeOption(scope.row)"
          >
            <Icon icon="ep:delete" />
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts" setup>
import type { SalarySlipTemplateOptionVO } from '@/api/hrm/salary/slip/template'
import {
  HrmSalaryOptionCategoryCode,
  HrmSalaryOptionCode,
  HrmSalarySlipTemplateOptionType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmSalarySlipTemplateOptionEditor' })

defineSlots<{
  actions?: () => unknown
}>()

const props = withDefaults(
  defineProps<{
    modelValue?: SalarySlipTemplateOptionVO[]
    maxHeight?: number
  }>(),
  {
    maxHeight: 420
  }
)
const emit = defineEmits<{
  'update:modelValue': [options: SalarySlipTemplateOptionVO[]]
  remove: [option: SalarySlipTemplateOptionVO]
}>() // 定义 modelValue 更新和 remove 事件

/** 模板分类 */
const categoryOptions = computed(() =>
  (props.modelValue || [])
    .filter((item) => item.type === HrmSalarySlipTemplateOptionType.CATEGORY)
    .sort(compareOption)
)

/** 按分类和排序整理后的模板明细 */
const displayOptions = computed(() => {
  const options = props.modelValue || []
  const result: SalarySlipTemplateOptionVO[] = []
  categoryOptions.value.forEach((category) => {
    result.push(category)
    result.push(
      ...options
        .filter(
          (item) =>
            item.type === HrmSalarySlipTemplateOptionType.ITEM && item.parentCode === category.code
        )
        .sort(compareOption)
    )
  })
  result.push(
    ...options
      .filter(
        (item) =>
          item.type === HrmSalarySlipTemplateOptionType.ITEM &&
          !categoryOptions.value.some((category) => category.code === item.parentCode)
      )
      .sort(compareOption)
  )
  return result
})

/** 新增自定义分类 */
function addCategory() {
  const codes = (props.modelValue || [])
    .map((item) => item.code)
    .filter((code): code is number => code !== undefined)
  const code = Math.min(-1, ...codes.filter((item) => item < 0)) - 1
  emit('update:modelValue', [
    ...(props.modelValue || []),
    {
      name: '新分类',
      type: HrmSalarySlipTemplateOptionType.CATEGORY,
      code,
      hidden: false,
      sort: getNextSort()
    }
  ])
}

/** 删除模板明细 */
function removeOption(option: SalarySlipTemplateOptionVO) {
  const options = (props.modelValue || [])
    .filter((item) => item !== option)
    .map((item) =>
      option.type === HrmSalarySlipTemplateOptionType.CATEGORY && item.parentCode === option.code
        ? { ...item, parentCode: undefined }
        : item
    )
  emit('update:modelValue', options)
  emit('remove', option)
}

/** 修改工资项显示状态 */
function handleVisibleChange(
  option: SalarySlipTemplateOptionVO,
  visible: string | number | boolean
) {
  option.hidden = !Boolean(visible)
  emit('update:modelValue', [...(props.modelValue || [])])
}

/** 调整同级模板明细顺序 */
function moveOption(option: SalarySlipTemplateOptionVO, offset: number) {
  const siblings = getSiblingOptions(option)
  const index = siblings.indexOf(option)
  const target = siblings[index + offset]
  if (!target) {
    return
  }
  const sort = option.sort
  option.sort = target.sort
  target.sort = sort
  emit('update:modelValue', [...(props.modelValue || [])])
}

/** 是否为同级第一项 */
function isFirstOption(option: SalarySlipTemplateOptionVO) {
  return getSiblingOptions(option)[0] === option
}

/** 是否为同级最后一项 */
function isLastOption(option: SalarySlipTemplateOptionVO) {
  const siblings = getSiblingOptions(option)
  return siblings[siblings.length - 1] === option
}

/** 获得同级模板明细 */
function getSiblingOptions(option: SalarySlipTemplateOptionVO) {
  return (props.modelValue || [])
    .filter((item) =>
      option.type === HrmSalarySlipTemplateOptionType.CATEGORY
        ? item.type === HrmSalarySlipTemplateOptionType.CATEGORY
        : item.type === HrmSalarySlipTemplateOptionType.ITEM &&
          item.parentCode === option.parentCode
    )
    .sort(compareOption)
}

/** 校验模板明细 */
function validate() {
  if (displayOptions.value.some((item) => !item.name?.trim())) {
    return '模板明细名称不能为空'
  }
  if (displayOptions.value.some((item) => (item.name?.length || 0) > 64)) {
    return '模板明细名称不能超过 64 个字符'
  }
  if (displayOptions.value.some((item) => (item.remark?.length || 0) > 255)) {
    return '模板明细备注不能超过 255 个字符'
  }
  if (
    categoryOptions.value.some(
      (category) =>
        !displayOptions.value.some(
          (item) =>
            item.type === HrmSalarySlipTemplateOptionType.ITEM && item.parentCode === category.code
        )
    )
  ) {
    return '模板分类下至少需要保留一个工资项'
  }
}

/** 获得标准化后的模板明细 */
function getNormalizedOptions() {
  return displayOptions.value.map((item, index) => ({
    ...item,
    parentCode:
      item.type === HrmSalarySlipTemplateOptionType.CATEGORY
        ? HrmSalaryOptionCategoryCode.ROOT
        : item.parentCode || HrmSalaryOptionCategoryCode.ROOT,
    sort: index + 1
  }))
}

/** 比较模板明细顺序 */
function compareOption(first: SalarySlipTemplateOptionVO, second: SalarySlipTemplateOptionVO) {
  return (first.sort || 0) - (second.sort || 0)
}

/** 获得下一个排序值 */
function getNextSort() {
  return Math.max(0, ...(props.modelValue || []).map((item) => item.sort || 0)) + 1
}

defineExpose({ getNormalizedOptions, validate }) // 提供模板明细读取和校验方法
</script>
