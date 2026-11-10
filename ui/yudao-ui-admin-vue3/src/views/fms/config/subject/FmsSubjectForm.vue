<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" scroll width="560px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-alert
        v-if="formType === 'create' && parentSubjectUsed"
        :closable="false"
        class="!mb-16px"
        type="warning"
      >
        <template #title>
          {{
            subjectUsage.childCount > 0
              ? '上级科目已有业务数据和下级科目，当前数据状态不允许继续新增下级'
              : `上级科目已有 ${subjectUsage.voucherEntryCount} 条凭证分录、${subjectUsage.initialBalanceCount} 条初始余额和 ${subjectUsage.auxiliaryCombinationCount} 个辅助核算组合，创建后将全部迁移到新科目`
          }}
        </template>
      </el-alert>
      <el-alert
        v-if="formType === 'update' && (subjectUsage.used || subjectUsage.childCount > 0)"
        :closable="false"
        class="!mb-16px"
        type="warning"
      >
        <template #title>
          {{
            subjectUsage.used
              ? '该科目已有业务数据，余额方向不能修改；首次启用辅助核算时，需要为历史数据指定迁移项目'
              : '该科目已有下级，科目类别、编码和辅助核算不能修改'
          }}
        </template>
      </el-alert>
      <el-form-item label="科目编码" prop="code">
        <el-input
          v-model="formData.code"
          :disabled="codeDisabled"
          maxlength="64"
          placeholder="请输入科目编码"
          @blur="handleCodeBlur"
        />
        <div class="text-12px leading-24px text-[var(--el-text-color-secondary)]">
          科目级次：{{ subjectCodeRule || '未配置' }}
        </div>
      </el-form-item>
      <el-form-item label="科目名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入科目名称" />
      </el-form-item>
      <el-form-item label="上级科目">
        <el-input
          :model-value="
            parentSubject ? `${parentSubject.code} ${parentSubject.name}` : '无上级科目'
          "
          disabled
        />
      </el-form-item>
      <el-form-item label="科目类别" prop="category">
        <el-select
          v-model="formData.category"
          :disabled="Boolean(parentSubject) || subjectUsage.childCount > 0"
          class="!w-1/1"
          placeholder="请选择科目类别"
        >
          <el-option
            v-for="dict in categoryOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="余额方向" prop="balanceDirection">
        <el-radio-group v-model="formData.balanceDirection" :disabled="subjectUsage.used">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.FMS_DEBIT_CREDIT_DIRECTION)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="辅助核算">
        <FmsAuxiliaryTypeSelect
          v-model="formData.auxiliaryTypeIds"
          :disabled="auxiliaryTypeDisabled"
          multiple
          placeholder="请选择辅助核算"
          @change="handleAuxiliaryTypeChange"
          @loaded="auxiliaryTypes = $event"
        />
      </el-form-item>
      <template v-if="auxiliaryMigrationRequired">
        <el-alert
          :closable="false"
          class="!mb-16px"
          title="请选择历史凭证要迁入的辅助核算项目，该操作不可撤销"
          type="warning"
        />
        <el-form-item
          v-for="(mapping, index) in formData.auxiliaryMappings"
          :key="mapping.typeId"
          :label="getAuxiliaryTypeName(mapping.typeId)"
          :prop="`auxiliaryMappings.${index}.itemId`"
          :rules="[{ required: true, message: '请选择迁移项目', trigger: 'change' }]"
        >
          <FmsAuxiliaryItemSelect
            v-model="mapping.itemId"
            :auxiliary-type-id="mapping.typeId"
            placeholder="请选择迁移项目"
          />
        </el-form-item>
      </template>
      <el-form-item label="外币核算">
        <FmsCurrencySelect
          v-model="formData.currencyIds"
          :disabled="parentSubjectUsed"
          :exclude-standard="true"
          multiple
          placeholder="请选择币别"
        />
      </el-form-item>
      <el-form-item label="数量核算">
        <el-checkbox
          v-model="formData.quantityAccounting"
          :disabled="parentSubjectUsed || subjectUsage.quantityDataCount > 0"
        >
          启用数量核算
        </el-checkbox>
      </el-form-item>
      <el-form-item v-if="formData.quantityAccounting" label="数量单位" prop="quantityUnit">
        <el-input
          v-model="formData.quantityUnit"
          :disabled="parentSubjectUsed"
          maxlength="255"
          placeholder="请输入数量单位"
        />
      </el-form-item>
      <el-form-item label="现金项">
        <el-checkbox v-model="formData.cash" :disabled="Boolean(parentSubject?.cash)">
          现金及现金等价物
        </el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        :disabled="formLoading || parentDataMigrationBlocked"
        type="primary"
        @click="submitForm"
      >
        确 定
      </el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsFinanceParameterApi } from '@/api/fms/config/finance-parameter'
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectUsageVO, FmsSubjectVO } from '@/api/fms/config/subject'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { useFmsStore } from '@/views/fms/store/fms'
import {
  FMS_DEBIT_CREDIT_DIRECTION,
  FMS_SUBJECT_PARENT_ID_ROOT,
  FMS_SUBJECT_TYPE
} from '@/views/fms/utils/constants'
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import FmsAuxiliaryTypeSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryTypeSelect.vue'
import FmsAuxiliaryItemSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryItemSelect.vue'
import FmsCurrencySelect from '@/views/fms/config/currency/components/FmsCurrencySelect.vue'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsSubjectForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<FmsSubjectVO>({} as FmsSubjectVO) // 表单数据
const formRules = reactive<FormRules>({
  code: [{ required: true, message: '科目编码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '科目名称不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '科目类别不能为空', trigger: 'change' }],
  balanceDirection: [{ required: true, message: '余额方向不能为空', trigger: 'change' }],
  quantityUnit: [
    {
      validator: (_rule, value, callback) => {
        if (!formData.value.quantityAccounting || value) {
          callback()
          return
        }
        callback(new Error('数量单位不能为空'))
      },
      trigger: 'blur'
    }
  ]
})
const formRef = ref<FormInstance>() // 表单 Ref

const parentSubject = ref<FmsSubjectVO>() // 上级科目
const subjectUsage = ref<FmsSubjectUsageVO>(createEmptySubjectUsage()) // 科目使用情况
const subjectCodeRule = ref('') // 科目编码规则
const originalAuxiliaryTypeIds = ref<number[]>([]) // 修改前的辅助核算类别编号数组
const auxiliaryTypes = ref<FmsAuxiliaryTypeVO[]>([]) // 辅助核算类别数组
const subjectCandidates = ref<FmsSubjectVO[]>([]) // 当前账套同类科目，用于编码识别和重名提示
const explicitParent = ref(false) // 是否由“新建下级”入口明确指定上级

/** 当前科目类型的类别选项 */
const categoryOptions = computed(() =>
  getStrDictOptions(DICT_TYPE.FMS_SUBJECT_CATEGORY)
    .filter((dict) => dict.value.startsWith(`${formData.value.type}-`))
    .map((dict) => ({
      ...dict,
      value: Number(dict.value.split('-')[1])
    }))
)
/** 存在下级科目时不允许修改编码 */
const codeDisabled = computed(
  () => formType.value === 'update' && subjectUsage.value.childCount > 0
)
/** 新建下级时，上级科目是否已有业务数据 */
const parentSubjectUsed = computed(
  () => formType.value === 'create' && Boolean(parentSubject.value) && subjectUsage.value.used
)
/** 上级科目存在业务数据和下级科目时不允许再次迁移 */
const parentDataMigrationBlocked = computed(
  () => parentSubjectUsed.value && subjectUsage.value.childCount > 0
)
/** 是否禁用辅助核算类别选择 */
const auxiliaryTypeDisabled = computed(
  () =>
    parentSubjectUsed.value ||
    subjectUsage.value.childCount > 0 ||
    (subjectUsage.value.used && originalAuxiliaryTypeIds.value.length > 0)
)
/** 是否需要迁移历史辅助核算数据 */
const auxiliaryMigrationRequired = computed(
  () =>
    formType.value === 'update' &&
    subjectUsage.value.voucherEntryCount > 0 &&
    originalAuxiliaryTypeIds.value.length === 0 &&
    formData.value.auxiliaryTypeIds.length > 0
)

/** 打开弹窗 */
async function open(type: string, subjectType: number, row?: FmsSubjectVO, parent?: FmsSubjectVO) {
  const accountSetId = fmsStore.getAccountSetId
  if (!accountSetId) return
  dialogVisible.value = true
  formType.value = type
  dialogTitle.value = type === 'update' ? '编辑科目' : parent ? '新建下级科目' : '新建科目'
  resetForm(accountSetId, subjectType, parent)
  formLoading.value = true
  try {
    // 修改时，设置科目数据
    if (type === 'update' && row) {
      const [financeParameter, subject, usage] = await Promise.all([
        FmsFinanceParameterApi.getFinanceParameter(accountSetId),
        FmsSubjectApi.getSubject(accountSetId, row.id),
        FmsSubjectApi.getSubjectUsage(accountSetId, row.id)
      ])
      subjectCodeRule.value = financeParameter?.subjectCodeRule || ''
      formData.value = subject
      formData.value.auxiliaryMappings = []
      originalAuxiliaryTypeIds.value = [...(subject.auxiliaryTypeIds || [])]
      subjectUsage.value = usage
      parentSubject.value = parent
      return
    }
    // 加载科目编码规则和上级科目使用情况
    const [financeParameter, parentUsage, subjects] = await Promise.all([
      FmsFinanceParameterApi.getFinanceParameter(accountSetId),
      parent ? FmsSubjectApi.getSubjectUsage(accountSetId, parent.id) : undefined,
      FmsSubjectApi.getSubjectList(accountSetId, subjectType)
    ])
    subjectCodeRule.value = financeParameter?.subjectCodeRule || ''
    subjectCandidates.value = subjects
    // 新建下级时，继承上级科目的核算配置
    if (parent) {
      formData.value.parentId = parent.id
      formData.value.code = suggestChildCode(parent)
      formData.value.category = parent.category
      formData.value.balanceDirection = parent.balanceDirection
      formData.value.auxiliaryTypeIds = [...(parent.auxiliaryTypeIds || [])]
      formData.value.currencyIds = [...(parent.currencyIds || [])]
      formData.value.quantityAccounting = parent.quantityAccounting
      formData.value.quantityUnit = parent.quantityUnit
      formData.value.cash = parent.cash
      subjectUsage.value = parentUsage!
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (!formData.value.quantityAccounting) {
      formData.value.quantityUnit = undefined
    }
    if (formType.value === 'create') {
      const duplicateSubject = subjectCandidates.value.find(
        (subject) =>
          subject.parentId === formData.value.parentId &&
          subject.name === formData.value.name.trim()
      )
      if (duplicateSubject) {
        try {
          await message.confirm(
            `同级已有名称为“${duplicateSubject.name}”的科目（${duplicateSubject.code}），是否仍要继续？`,
            '科目名称重复'
          )
        } catch {
          return
        }
      }
      if (parentSubjectUsed.value) {
        try {
          await message.confirm(
            `继续后会把上级科目的 ${subjectUsage.value.voucherEntryCount} 条凭证分录、${subjectUsage.value.initialBalanceCount} 条初始余额和 ${subjectUsage.value.auxiliaryCombinationCount} 个辅助核算组合迁移到新科目，且无法撤销。是否继续？`,
            '迁移上级科目历史数据'
          )
        } catch {
          return
        }
        formData.value.migrateParentData = true
      }
      await FmsSubjectApi.createSubject(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      if (auxiliaryMigrationRequired.value) {
        try {
          await message.confirm(
            `继续后会把该科目的 ${subjectUsage.value.voucherEntryCount} 条凭证分录迁移到所选辅助核算项目，且无法撤销。是否继续？`,
            '迁移历史辅助核算数据'
          )
        } catch {
          return
        }
      } else {
        formData.value.auxiliaryMappings = undefined
      }
      await FmsSubjectApi.updateSubject(formData.value)
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
function resetForm(accountSetId: number, type: number, parent?: FmsSubjectVO) {
  formData.value = createEmptyFormData(accountSetId, type)
  parentSubject.value = parent
  explicitParent.value = Boolean(parent)
  subjectCandidates.value = []
  subjectUsage.value = createEmptySubjectUsage()
  subjectCodeRule.value = ''
  originalAuxiliaryTypeIds.value = []
  formRef.value?.resetFields()
}

/** 创建空的科目使用情况 */
function createEmptySubjectUsage(): FmsSubjectUsageVO {
  return {
    childCount: 0,
    voucherEntryCount: 0,
    initialBalanceCount: 0,
    auxiliaryCombinationCount: 0,
    quantityDataCount: 0,
    used: false
  }
}

/** 创建空表单数据 */
function createEmptyFormData(
  accountSetId: number = 0,
  type: number = FMS_SUBJECT_TYPE.ASSET
): FmsSubjectVO {
  return {
    id: 0,
    accountSetId,
    code: '',
    name: '',
    parentId: FMS_SUBJECT_PARENT_ID_ROOT,
    type,
    category: undefined as unknown as number,
    balanceDirection: FMS_DEBIT_CREDIT_DIRECTION.DEBIT,
    auxiliaryTypeIds: [],
    auxiliaryTypeNames: [],
    currencyIds: [],
    quantityAccounting: false,
    cash: false,
    migrateParentData: false,
    auxiliaryMappings: [],
    children: [],
    createTime: new Date()
  }
}

/** 同步辅助核算历史数据迁移项目 */
function handleAuxiliaryTypeChange(value: number | number[] | undefined) {
  const typeIds = Array.isArray(value) ? value : []
  const mappingMap = new Map(
    (formData.value.auxiliaryMappings || []).map((mapping) => [mapping.typeId, mapping])
  )
  formData.value.auxiliaryMappings = typeIds.map((typeId) => mappingMap.get(typeId) || { typeId })
}

/** 获得辅助核算类别名称 */
function getAuxiliaryTypeName(typeId: number) {
  return auxiliaryTypes.value.find((item) => item.id === typeId)?.name || '辅助核算项目'
}

/** 根据编码规则生成下级科目编码 */
function suggestChildCode(parent: FmsSubjectVO) {
  const codeRules = subjectCodeRule.value.split('-').map(Number)
  const segmentLength = codeRules[parent.level || 1] || 2
  const prefix = parent.code
  const usedCodes = new Set((parent.children || []).map((item) => item.code.slice(prefix.length)))
  const maxCode = 10 ** segmentLength - 1
  for (let code = 1; code <= maxCode; code++) {
    const suffix = String(code).padStart(segmentLength, '0')
    if (!usedCodes.has(suffix)) {
      return `${prefix}${suffix}`
    }
  }
  return `${prefix}${String(maxCode).padStart(segmentLength, '0')}`
}

/** 根据完整科目编码识别上级科目 */
async function handleCodeBlur() {
  if (formType.value !== 'create' || explicitParent.value) return
  const code = formData.value.code?.trim()
  const parent = findParentByCode(code)
  if (!parent) {
    parentSubject.value = undefined
    formData.value.parentId = FMS_SUBJECT_PARENT_ID_ROOT
    subjectUsage.value = createEmptySubjectUsage()
    return
  }
  parentSubject.value = parent
  formData.value.parentId = parent.id
  formData.value.category = parent.category
  formData.value.balanceDirection = parent.balanceDirection
  formData.value.auxiliaryTypeIds = [...(parent.auxiliaryTypeIds || [])]
  formData.value.currencyIds = [...(parent.currencyIds || [])]
  formData.value.quantityAccounting = parent.quantityAccounting
  formData.value.quantityUnit = parent.quantityUnit
  formData.value.cash = parent.cash
  const accountSetId = fmsStore.getAccountSetId
  if (!accountSetId) return
  formLoading.value = true
  try {
    const usage = await FmsSubjectApi.getSubjectUsage(accountSetId, parent.id)
    // 仅应用当前编码对应的上级结果，避免连续输入时旧请求覆盖新状态
    if (formData.value.code?.trim() === code && parentSubject.value?.id === parent.id) {
      subjectUsage.value = usage
    }
  } finally {
    formLoading.value = false
  }
}

/** 按会计科目级次和编码前缀查找已存在的上级科目 */
function findParentByCode(code?: string) {
  if (!code || !/^\d+$/.test(code)) return undefined
  const codeRules = subjectCodeRule.value.split('-').map(Number)
  const parentLength = codeRules.slice(0, codeRules.length).reduce((total, length) => {
    const currentLength = total + length
    return currentLength < code.length ? currentLength : total
  }, 0)
  if (parentLength <= 0 || parentLength >= code.length) return undefined
  const parentCode = code.slice(0, parentLength)
  return subjectCandidates.value.find((subject) => subject.code === parentCode)
}
</script>
