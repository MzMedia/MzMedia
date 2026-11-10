<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="90px"
    >
      <el-form-item label="凭证字" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入凭证字" />
      </el-form-item>
      <el-form-item label="打印标题" prop="printTitle">
        <el-input v-model="formData.printTitle" maxlength="255" placeholder="请输入打印标题" />
      </el-form-item>
      <el-form-item label="是否默认" prop="defaultStatus">
        <el-radio-group v-model="formData.defaultStatus">
          <el-radio
            v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
            :key="String(dict.value)"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
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
import { DICT_TYPE, getBoolDictOptions } from '@/utils/dict'
import { FmsVoucherWordApi } from '@/api/fms/config/voucher-word'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'

defineOptions({ name: 'FmsVoucherWordForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formData = ref<Omit<FmsVoucherWordVO, 'id'> & { id?: number }>({
  id: undefined,
  accountSetId: 0,
  name: '',
  printTitle: '记账凭证',
  defaultStatus: false
})
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '凭证字不能为空', trigger: 'blur' }],
  defaultStatus: [{ required: true, message: '请选择是否默认', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(type: string, accountSetId: number, row?: FmsVoucherWordVO) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  resetForm(accountSetId)
  // 修改时，设置数据
  if (row) {
    formData.value = { ...row, printTitle: row.printTitle || '记账凭证' }
  }
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open })

const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as FmsVoucherWordVO
    if (formData.value.id) {
      await FmsVoucherWordApi.updateVoucherWord(data)
      message.success(t('common.updateSuccess'))
    } else {
      await FmsVoucherWordApi.createVoucherWord(data)
      message.success(t('common.createSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(accountSetId: number) {
  formData.value = {
    id: undefined,
    accountSetId,
    name: '',
    printTitle: '记账凭证',
    defaultStatus: false
  }
  formRef.value?.resetFields()
}
</script>
