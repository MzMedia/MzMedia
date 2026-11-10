<template>
  <Dialog v-model="dialogVisible" title="新增凭证模板" width="480px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
      <el-form-item label="模板分类" prop="categoryId">
        <FmsVoucherTemplateCategorySelect
          v-model="formData.categoryId"
          :account-set-id="accountSetId"
          :categories="categories"
          @change="handleCategoryChange"
        />
      </el-form-item>
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="保存金额">
        <el-checkbox v-model="saveMoney">保留数量、单价和借贷金额</el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { FmsVoucherTemplateEntryVO } from '@/api/fms/config/voucher-template'
import { FmsVoucherTemplateApi } from '@/api/fms/config/voucher-template'
import {
  FmsVoucherTemplateCategoryApi,
  type FmsVoucherTemplateCategoryVO
} from '@/api/fms/config/voucher-template-category'
import FmsVoucherTemplateCategorySelect from './FmsVoucherTemplateCategorySelect.vue'

defineOptions({ name: 'FmsVoucherTemplateSaveForm' })

const emit = defineEmits<{ success: [] }>()
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const submitting = ref(false) // 表单提交的加载中
const accountSetId = ref<number>() // 当前账套编号
const sourceEntries = ref<FmsVoucherTemplateEntryVO[]>([]) // 来源凭证分录数组
const categories = ref<FmsVoucherTemplateCategoryVO[]>([]) // 模板分类列表
const saveMoney = ref(false) // 是否保存数量、单价和借贷金额
const formRef = ref<FormInstance>() // 表单 Ref
const formData = reactive({
  categoryId: undefined as number | undefined,
  name: ''
})
const formRules = reactive<FormRules>({
  categoryId: [{ required: true, message: '请选择模板分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
})

/** 打开弹窗 */
async function open(id: number, entries: FmsVoucherTemplateEntryVO[]) {
  // 1. 保存账套和来源凭证分录
  accountSetId.value = id
  sourceEntries.value = entries.map((entry) => ({
    ...entry,
    auxiliaries: entry.auxiliaries.map((item) => ({ ...item }))
  }))

  // 2. 重置模板和分类表单
  formData.categoryId = undefined
  formData.name = ''
  saveMoney.value = false

  // 3. 查询模板分类并默认选择首个分类
  await getCategoryList()
  formData.categoryId = categories.value[0]?.id
  dialogVisible.value = true
}

/** 查询模板分类列表 */
async function getCategoryList() {
  if (!accountSetId.value) return
  categories.value = await FmsVoucherTemplateCategoryApi.getVoucherTemplateCategorySimpleList(
    accountSetId.value
  )
}

/** 同步模板分类列表，并清理已删除的当前选项 */
function handleCategoryChange(nextCategories: FmsVoucherTemplateCategoryVO[]) {
  categories.value = nextCategories
  if (formData.categoryId && !nextCategories.some((item) => item.id === formData.categoryId)) {
    formData.categoryId = undefined
  }
}

/** 提交表单 */
async function submitForm() {
  // 1. 校验模板表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  if (!accountSetId.value || !formData.categoryId) return

  // 2. 保存凭证模板
  submitting.value = true
  try {
    await FmsVoucherTemplateApi.createVoucherTemplate({
      accountSetId: accountSetId.value,
      categoryId: formData.categoryId,
      name: formData.name,
      entries: sourceEntries.value.map((entry) => ({
        ...entry,
        quantity: saveMoney.value ? entry.quantity : undefined,
        unitPrice: saveMoney.value ? entry.unitPrice : undefined,
        debitAmount: saveMoney.value ? entry.debitAmount : undefined,
        creditAmount: saveMoney.value ? entry.creditAmount : undefined,
        auxiliaries: entry.auxiliaries.map((item) => ({
          typeId: item.typeId,
          itemId: item.itemId
        }))
      }))
    })
    message.success('保存成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
