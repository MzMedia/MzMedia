<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="币别编码" prop="code">
        <el-input
          v-model="formData.code"
          :disabled="standardCurrency"
          maxlength="64"
          placeholder="请输入币别编码，如 USD"
          @blur="formData.code = formData.code.toUpperCase()"
        />
      </el-form-item>
      <el-form-item label="币别名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入币别名称" />
      </el-form-item>
      <el-form-item label="汇率" prop="exchangeRate">
        <el-input-number
          v-model="formData.exchangeRate"
          :disabled="standardCurrency"
          :min="0.000001"
          :max="999999999999.999999"
          :precision="6"
          :step="0.01"
          controls-position="right"
          class="!w-1/1"
        />
        <div class="text-12px leading-24px text-[var(--el-text-color-secondary)]">
          {{ standardCurrency ? '本位币汇率固定为 1' : '按 1 单位外币折算本位币填写' }}
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsCurrencyApi } from '@/api/fms/config/currency'
import type { FmsCurrencyVO } from '@/api/fms/config/currency'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsCurrencyForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const standardCurrency = ref(false) // 是否本位币
const formData = ref<FmsCurrencyVO>({
  id: undefined,
  accountSetId: 0,
  code: '',
  name: '',
  exchangeRate: 1,
  standard: false
})
const formRules = reactive<FormRules>({
  code: [
    { required: true, message: '币别编码不能为空', trigger: 'blur' },
    {
      pattern: /^[A-Za-z][A-Za-z0-9_]*$/,
      message: '币别编码必须以字母开头，只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  name: [{ required: true, message: '币别名称不能为空', trigger: 'blur' }],
  exchangeRate: [{ required: true, message: '汇率不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(type: string, accountSetId: number, row?: FmsCurrencyVO) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm(accountSetId)
  // 修改时，设置数据
  if (row) {
    formData.value = { ...row }
  }
  standardCurrency.value = Boolean(row?.standard)
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
    formData.value.code = formData.value.code.toUpperCase()
    if (formType.value === 'create') {
      await FmsCurrencyApi.createCurrency(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsCurrencyApi.updateCurrency(formData.value)
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
function resetForm(accountSetId: number) {
  formData.value = {
    id: undefined,
    accountSetId,
    code: '',
    name: '',
    exchangeRate: 1,
    standard: false
  }
  formRef.value?.resetFields()
}
</script>
