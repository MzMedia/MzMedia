<template>
  <Dialog v-model="dialogVisible" title="添加明细" width="520px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
      <el-form-item label="科目">
        <el-input :model-value="`${subject?.subjectCode} ${subject?.subjectName}`" disabled />
      </el-form-item>
      <el-divider content-position="left">辅助核算</el-divider>
      <el-form-item
        v-for="config in subject?.auxiliaryConfigs || []"
        :key="config.auxiliaryTypeId"
        :label="config.name"
        :prop="`items.${config.auxiliaryTypeId}`"
      >
        <FmsAuxiliaryItemSelect
          v-model="formData.items[config.auxiliaryTypeId]"
          :auxiliary-type-id="config.auxiliaryTypeId"
          multiple
          :placeholder="`请选择${config.name}`"
          @change="(items) => handleItemsChange(config.auxiliaryTypeId, items)"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">确定</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { FmsAuxiliaryItemOptionVO } from '@/api/fms/config/auxiliary/item'
import type { FmsInitialBalanceVO } from '@/api/fms/config/initial-balance'
import FmsAuxiliaryItemSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryItemSelect.vue'

defineOptions({ name: 'FmsInitialAssistForm' })

const dialogVisible = ref(false) // 弹窗的是否展示
const subject = ref<FmsInitialBalanceVO>() // 当前科目
const formData = reactive<{ items: Record<number, number[]> }>({ items: {} }) // 表单数据
const selectedItems = ref<Record<number, FmsAuxiliaryItemOptionVO[]>>({}) // 各类别选中的项目
const formRules = computed<FormRules>(() => {
  const rules: FormRules = {}
  subject.value?.auxiliaryConfigs.forEach((config) => {
    rules[`items.${config.auxiliaryTypeId}`] = [
      { required: true, message: `请选择${config.name}`, trigger: 'change' }
    ]
  })
  return rules
})
const formRef = ref<FormInstance>() // 表单 Ref

/**
 * 更新指定辅助核算类别的选中项目
 *
 * @param auxiliaryTypeId 辅助核算类别编号
 * @param items 当前类别选中的项目
 */
function handleItemsChange(
  auxiliaryTypeId: number,
  items: FmsAuxiliaryItemOptionVO | FmsAuxiliaryItemOptionVO[] | undefined
) {
  selectedItems.value[auxiliaryTypeId] = Array.isArray(items) ? items : items ? [items] : []
}

/** 打开弹窗 */
async function open(row: FmsInitialBalanceVO) {
  dialogVisible.value = true
  subject.value = row
  resetForm()
}
defineExpose({ open })

const emit = defineEmits<{ success: [combinations: FmsAuxiliaryItemOptionVO[][]] }>() // 定义 success 事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  if (!subject.value) return
  // 发送操作成功的事件
  const itemGroups = subject.value.auxiliaryConfigs.map(
    (config) => selectedItems.value[config.auxiliaryTypeId] || []
  )
  emit('success', buildCombinations(itemGroups))
  dialogVisible.value = false
}

/** 将各辅助类别的多选项目展开为组合 */
function buildCombinations(itemGroups: FmsAuxiliaryItemOptionVO[][]) {
  return itemGroups.reduce<FmsAuxiliaryItemOptionVO[][]>(
    (combinations, items) =>
      combinations.flatMap((combination) => items.map((item) => [...combination, item])),
    [[]]
  )
}

/** 重置表单 */
function resetForm() {
  formData.items = {}
  selectedItems.value = {}
  formRef.value?.resetFields()
  nextTick(() => formRef.value?.clearValidate())
}
</script>
