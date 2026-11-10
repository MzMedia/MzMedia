<!-- 业绩目标设置表单 -->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="960px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="年份" prop="year">
            <el-date-picker
              v-model="formData.year"
              class="!w-100%"
              type="year"
              value-format="YYYY"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="目标类型" prop="bizType">
            <el-select v-model="formData.bizType" class="!w-100%" placeholder="请选择目标类型">
              <el-option
                v-for="item in bizTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="对象类型" prop="objectType">
            <el-select
              v-model="formData.objectType"
              class="!w-100%"
              placeholder="请选择对象类型"
              @change="handleObjectTypeChange"
            >
              <el-option
                v-for="item in objectTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="目标对象" prop="objectId">
        <el-tree-select
          v-if="formData.objectType === PerformanceConfigObjectTypeEnum.DEPT"
          v-model="formData.objectId"
          :data="deptList"
          :props="defaultProps"
          check-strictly
          class="!w-100%"
          node-key="id"
          placeholder="请选择部门"
        />
        <el-select
          v-else
          v-model="formData.objectId"
          class="!w-100%"
          filterable
          placeholder="请选择员工"
        >
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-divider />
      <el-row :gutter="16">
        <el-col v-for="item in monthFields" :key="item.prop" :span="6">
          <el-form-item :label="item.label" :prop="item.prop">
            <el-input-number
              v-model="formData[item.prop]"
              :min="0"
              :precision="2"
              :step="1000"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="年度目标">
        <el-input :model-value="yearTargetPriceText" disabled />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import {
  PerformanceConfigObjectTypeEnum,
  PerformanceConfigApi,
  CrmPerformanceConfigVO
} from '@/api/crm/performance/config'
import { BizTypeEnum } from '@/api/crm/permission'
import { defaultProps, handleTree } from '@/utils/tree'
import { erpPriceInputFormatter } from '@/utils'

defineOptions({ name: 'CrmPerformanceConfigForm' })

const { t } = useI18n()
const message = useMessage()

// 目标类型选项
const bizTypeOptions = [
  { label: '销售目标', value: BizTypeEnum.CRM_CONTRACT },
  { label: '回款目标', value: BizTypeEnum.CRM_RECEIVABLE }
]
// 目标对象类型选项
const objectTypeOptions = [
  { label: '部门', value: PerformanceConfigObjectTypeEnum.DEPT },
  { label: '员工', value: PerformanceConfigObjectTypeEnum.USER }
]
// 月度目标字段
const monthFields = [
  { label: '一月', prop: 'januaryTargetPrice' },
  { label: '二月', prop: 'februaryTargetPrice' },
  { label: '三月', prop: 'marchTargetPrice' },
  { label: '四月', prop: 'aprilTargetPrice' },
  { label: '五月', prop: 'mayTargetPrice' },
  { label: '六月', prop: 'juneTargetPrice' },
  { label: '七月', prop: 'julyTargetPrice' },
  { label: '八月', prop: 'augustTargetPrice' },
  { label: '九月', prop: 'septemberTargetPrice' },
  { label: '十月', prop: 'octoberTargetPrice' },
  { label: '十一月', prop: 'novemberTargetPrice' },
  { label: '十二月', prop: 'decemberTargetPrice' }
] as const

const dialogVisible = ref(false) // 弹窗是否展示
const dialogTitle = ref('') // 弹窗标题
const formLoading = ref(false) // 表单加载中
const formType = ref('') // 表单类型
const formRef = ref() // 表单 Ref
const deptList = ref<Tree[]>([]) // 部门树形结构
const userList = ref<UserApi.UserVO[]>([]) // 全量用户清单
const formData = ref<any>({
  id: undefined,
  objectId: undefined,
  objectType: PerformanceConfigObjectTypeEnum.DEPT,
  year: String(new Date().getFullYear()),
  bizType: BizTypeEnum.CRM_CONTRACT,
  januaryTargetPrice: 0,
  februaryTargetPrice: 0,
  marchTargetPrice: 0,
  aprilTargetPrice: 0,
  mayTargetPrice: 0,
  juneTargetPrice: 0,
  julyTargetPrice: 0,
  augustTargetPrice: 0,
  septemberTargetPrice: 0,
  octoberTargetPrice: 0,
  novemberTargetPrice: 0,
  decemberTargetPrice: 0
})
const formRules = reactive({
  year: [{ required: true, message: '年份不能为空', trigger: 'change' }],
  bizType: [{ required: true, message: '目标类型不能为空', trigger: 'change' }],
  objectType: [{ required: true, message: '对象类型不能为空', trigger: 'change' }],
  objectId: [{ required: true, message: '目标对象不能为空', trigger: 'change' }]
})

/** 年度目标金额 */
const yearTargetPrice = computed(() =>
  monthFields.reduce((sum, item) => sum + Number(formData.value[item.prop] || 0), 0)
)
/** 年度目标金额展示文本 */
const yearTargetPriceText = computed(() => erpPriceInputFormatter(yearTargetPrice.value))

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await loadOptions()
  if (id) {
    formLoading.value = true
    try {
      const data = await PerformanceConfigApi.getPerformanceConfig(id)
      formData.value = {
        ...data,
        year: String(data.year)
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
/** 提交表单 */
const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = {
      ...formData.value,
      year: Number(formData.value.year)
    } as CrmPerformanceConfigVO
    if (formType.value === 'create') {
      await PerformanceConfigApi.createPerformanceConfig(data)
      message.success(t('common.createSuccess'))
    } else {
      await PerformanceConfigApi.updatePerformanceConfig(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 对象类型变化时重置目标对象 */
const handleObjectTypeChange = () => {
  formData.value.objectId = undefined
}

/** 加载部门和员工选项 */
const loadOptions = async () => {
  if (!deptList.value.length) {
    deptList.value = handleTree(await DeptApi.getSimpleDeptList())
  }
  if (!userList.value.length) {
    userList.value = await UserApi.getSimpleUserList()
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    objectId: undefined,
    objectType: PerformanceConfigObjectTypeEnum.DEPT,
    year: String(new Date().getFullYear()),
    bizType: BizTypeEnum.CRM_CONTRACT,
    januaryTargetPrice: 0,
    februaryTargetPrice: 0,
    marchTargetPrice: 0,
    aprilTargetPrice: 0,
    mayTargetPrice: 0,
    juneTargetPrice: 0,
    julyTargetPrice: 0,
    augustTargetPrice: 0,
    septemberTargetPrice: 0,
    octoberTargetPrice: 0,
    novemberTargetPrice: 0,
    decemberTargetPrice: 0
  }
  formRef.value?.resetFields()
}
</script>
