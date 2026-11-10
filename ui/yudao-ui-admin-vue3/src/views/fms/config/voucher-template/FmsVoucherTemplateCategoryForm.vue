<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="90px"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入分类名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import {
  FmsVoucherTemplateCategoryApi,
  type FmsVoucherTemplateCategoryVO
} from '@/api/fms/config/voucher-template-category'

defineOptions({ name: 'FmsVoucherTemplateCategoryForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单提交的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<FmsVoucherTemplateCategoryVO>({ accountSetId: 0, name: '' })
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(type: string, accountSetId: number, row?: FmsVoucherTemplateCategoryVO) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  formData.value = row ? { ...row } : { accountSetId, name: '' }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await FmsVoucherTemplateCategoryApi.createVoucherTemplateCategory(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsVoucherTemplateCategoryApi.updateVoucherTemplateCategory(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = { accountSetId: 0, name: '' }
  formRef.value?.resetFields()
}
</script>
