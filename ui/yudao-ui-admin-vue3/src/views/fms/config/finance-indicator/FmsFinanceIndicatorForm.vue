<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="620px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="90px"
    >
      <el-form-item label="指标名称" prop="name">
        <el-input v-model="formData.name" maxlength="100" placeholder="请输入指标名称" />
      </el-form-item>
      <el-form-item label="指标编码" prop="code">
        <el-input
          v-model="formData.code"
          :disabled="formType === 'update'"
          maxlength="64"
          placeholder="请输入指标编码"
        />
      </el-form-item>
      <el-form-item label="取数报表" prop="type">
        <el-select v-model="formData.type" class="w-full" placeholder="请选择取数报表">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.FMS_FINANCE_INDICATOR_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="formula">
        <template #label>
          <span class="inline-flex items-center whitespace-nowrap">
            指标公式
            <el-tooltip content="支持报表行次公式（L1+L2-L3）或报表科目公式 JSON" placement="top">
              <Icon class="ml-4px" icon="ep:question-filled" />
            </el-tooltip>
          </span>
        </template>
        <el-input
          v-model="formData.formula"
          :rows="4"
          maxlength="2000"
          placeholder="例如：L1+L2-L3，或科目公式 JSON"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="formData.sort" :min="0" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
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
import { FmsFinanceIndicatorApi } from '@/api/fms/config/finance-indicator'
import type { FmsFinanceIndicatorVO } from '@/api/fms/config/finance-indicator'
import { CommonStatusEnum } from '@/utils/constants'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { FMS_FINANCE_INDICATOR_TYPE } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsFinanceIndicatorForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<FmsFinanceIndicatorVO>({
  accountSetId: 0,
  name: '',
  code: '',
  type: FMS_FINANCE_INDICATOR_TYPE.INCOME_STATEMENT,
  formula: 'L1',
  sort: 10,
  status: CommonStatusEnum.ENABLE
}) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '指标名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '指标编码不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '请选择取数报表', trigger: 'change' }],
  formula: [{ required: true, message: '指标公式不能为空', trigger: 'blur' }],
  sort: [{ required: true, message: '排序不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(type: string, accountSetId: number, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm(accountSetId)
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await FmsFinanceIndicatorApi.getFinanceIndicator(accountSetId, id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

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
    if (formType.value === 'create') {
      await FmsFinanceIndicatorApi.createFinanceIndicator(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsFinanceIndicatorApi.updateFinanceIndicator(formData.value)
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
    accountSetId,
    name: '',
    code: '',
    type: FMS_FINANCE_INDICATOR_TYPE.INCOME_STATEMENT,
    formula: 'L1',
    sort: 10,
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}
</script>
