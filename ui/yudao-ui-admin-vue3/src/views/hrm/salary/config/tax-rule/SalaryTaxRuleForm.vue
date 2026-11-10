<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="620">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <!-- 计税规则信息 -->
      <el-form-item label="方案名称" prop="name">
        <el-input v-model="formData.name" maxlength="64" placeholder="请输入方案名称" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="个税类型" prop="type">
            <el-select
              v-model="formData.type"
              class="!w-1/1"
              placeholder="请选择个税类型"
              @change="handleTypeChange"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_SALARY_TAX_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否计税" prop="taxEnabled">
            <el-switch
              v-model="formData.taxEnabled"
              :disabled="formData.type === HrmSalaryTaxType.NONE"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.type !== HrmSalaryTaxType.NONE" :gutter="20">
        <el-col :span="12">
          <el-form-item label="起征点" prop="threshold">
            <el-input-number v-model="formData.threshold" :min="0" :precision="2" class="!w-1/1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="小数位" prop="decimalScale">
            <el-input-number v-model="formData.decimalScale" :min="0" :max="4" class="!w-1/1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-alert
        v-if="formData.type !== HrmSalaryTaxType.NONE"
        class="mb-16px"
        :closable="false"
        type="info"
        show-icon
        title="工资薪金默认起征点为 5000 元，劳务报酬默认 800 元，起征点不得小于 0；小数位决定个税计算结果保留 0～4 位。"
      />
      <el-form-item
        v-if="formData.type === HrmSalaryTaxType.SALARY"
        label="计税周期"
        prop="cycleType"
      >
        <el-radio-group v-model="formData.cycleType">
          <el-radio
            v-for="item in HrmSalaryTaxCycleTypeOptions"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as SalaryTaxRuleApi from '@/api/hrm/salary/config/tax-rule'
import {
  HrmSalaryTaxCycleType,
  HrmSalaryTaxCycleTypeOptions,
  HrmSalaryTaxType
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmSalaryTaxRuleForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否显示
const dialogTitle = ref('') // 弹窗标题
const formLoading = ref(false) // 表单加载中
const formType = ref('') // 表单类型
const formData = ref<SalaryTaxRuleApi.SalaryTaxRuleVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '方案名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '个税类型不能为空', trigger: 'change' }],
  taxEnabled: [{ required: true, message: '是否计税不能为空', trigger: 'change' }],
  threshold: [
    {
      validator: (_rule: unknown, value: number | undefined, callback: (error?: Error) => void) => {
        if (formData.value.type !== HrmSalaryTaxType.NONE && value == null) {
          callback(new Error('起征点不能为空'))
          return
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ],
  decimalScale: [
    {
      validator: (_rule: unknown, value: number | undefined, callback: (error?: Error) => void) => {
        if (formData.value.type !== HrmSalaryTaxType.NONE && value == null) {
          callback(new Error('小数位不能为空'))
          return
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ],
  cycleType: [
    {
      validator: (_rule: unknown, value: number | undefined, callback: (error?: Error) => void) => {
        if (formData.value.type === HrmSalaryTaxType.SALARY && value == null) {
          callback(new Error('计税周期不能为空'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ]
})
const formRef = ref<FormInstance>() // 表单引用

/** 创建默认表单数据 */
function createDefaultFormData(): SalaryTaxRuleApi.SalaryTaxRuleVO {
  return {
    id: undefined,
    name: '',
    type: HrmSalaryTaxType.SALARY,
    taxEnabled: true,
    threshold: 5000,
    decimalScale: 2,
    cycleType: HrmSalaryTaxCycleType.JANUARY_TO_DECEMBER
  }
}

/** 打开新增/修改表单 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      // 获取表单数据
      formData.value = await SalaryTaxRuleApi.getSalaryTaxRule(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件

/** 切换个税类型 */
function handleTypeChange(type?: number) {
  if (type === HrmSalaryTaxType.SALARY) {
    formData.value.taxEnabled = true
    formData.value.threshold = 5000
    formData.value.decimalScale = 2
    formData.value.cycleType = HrmSalaryTaxCycleType.JANUARY_TO_DECEMBER
  } else if (type === HrmSalaryTaxType.REMUNERATION) {
    formData.value.taxEnabled = true
    formData.value.threshold = 800
    formData.value.decimalScale = 2
    formData.value.cycleType = undefined
  } else {
    formData.value.taxEnabled = false
    formData.value.threshold = 0
    formData.value.decimalScale = undefined
    formData.value.cycleType = undefined
  }
  formRef.value?.clearValidate(['threshold', 'decimalScale', 'cycleType'])
}

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await SalaryTaxRuleApi.createSalaryTaxRule(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await SalaryTaxRuleApi.updateSalaryTaxRule(formData.value)
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
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}
</script>
