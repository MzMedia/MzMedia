<template>
  <Dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    max-height="calc(100vh - 210px)"
    scroll
    top="4vh"
    width="1120"
  >
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="118px"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="方案名称" prop="name">
            <el-input v-model="formData.name" maxlength="64" placeholder="请输入方案名称" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="参保城市" prop="areaId">
            <AreaSelect
              v-model="formData.areaId"
              check-strictly
              :selectable-levels="[2, 3]"
              class="!w-1/1"
              placeholder="请选择参保城市"
              @update:model-value="handleAreaChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="可选参保方案" prop="householdType">
            <el-select
              v-model="formData.householdType"
              clearable
              filterable
              :loading="standardLoading"
              class="!w-1/1"
              placeholder="请选择参保方案"
              @change="handleHouseTypeChange"
            >
              <el-option
                v-for="insuranceType in insuranceTypeList"
                :key="insuranceType.code"
                :label="insuranceType.name"
                :value="insuranceType.code"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="方案类型" prop="type">
        <el-radio-group v-model="formData.type">
          <el-radio-button :value="HrmInsuranceSchemeType.PROPORTION">
            设置参保基数和比例
          </el-radio-button>
          <el-radio-button :value="HrmInsuranceSchemeType.AMOUNT"> 仅设置参保金额 </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-alert
        class="mb-16px"
        :closable="false"
        type="info"
        show-icon
        title="比例模式：公司或个人缴纳金额 = 参保基数 × 对应比例；金额模式直接填写公司和个人缴纳金额。"
      />
      <el-form-item prop="projectList" label-width="0">
        <div class="w-full">
          <div
            v-for="section in projectSections"
            :key="section.key"
            :class="section.key === 'social' ? '' : 'mt-20px'"
          >
            <div class="mb-12px mt-8px flex items-center justify-between">
              <div class="flex items-center text-16px font-600 text-[var(--el-text-color-primary)]">
                <span class="mr-10px h-18px w-4px rounded-2px bg-[var(--el-color-primary)]"></span>
                {{ section.label }}
              </div>
              <el-dropdown
                :ref="(dropdown) => setProjectDropdownRef(section.key, dropdown)"
                :hide-on-click="false"
                :max-height="280"
                trigger="click"
              >
                <el-button> <Icon icon="ep:plus" class="mr-5px" /> 添加项目 </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="option in section.options"
                      :key="option.value"
                      @click.stop
                    >
                      <el-checkbox
                        :model-value="isProjectTypeUsed(option.value)"
                        @change="(checked) => handleProjectChecked(checked, option.value)"
                      >
                        {{ option.label }}
                      </el-checkbox>
                    </el-dropdown-item>
                    <el-dropdown-item
                      divided
                      @click="addCustomProject(section.key, section.customType)"
                    >
                      <Icon icon="ep:plus" class="mr-5px" />其他
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <el-table
              :data="section.projects"
              :summary-method="getProjectSummaries"
              border
              show-summary
            >
              <el-table-column label="项目名称" min-width="150" prop="name">
                <template #default="scope">
                  <el-input
                    v-if="isCustomProject(scope.row.type)"
                    v-model="scope.row.name"
                    maxlength="64"
                    placeholder="请输入项目名称"
                  />
                  <span v-else>{{ getProjectTypeName(scope.row.type) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="默认基数" prop="baseAmount" width="140">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.baseAmount"
                    :controls="false"
                    :min="0"
                    :precision="2"
                    class="!w-1/1"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="formData.type === HrmInsuranceSchemeType.PROPORTION"
                label="公司缴纳比例"
                prop="corporateRate"
                width="140"
              >
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.corporateRate"
                    :controls="false"
                    :max="100"
                    :min="0"
                    :precision="2"
                    class="!w-1/1"
                  >
                    <template #suffix>%</template>
                  </el-input-number>
                </template>
              </el-table-column>
              <el-table-column
                v-if="formData.type === HrmInsuranceSchemeType.PROPORTION"
                label="个人缴纳比例"
                prop="personalRate"
                width="140"
              >
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.personalRate"
                    :controls="false"
                    :max="100"
                    :min="0"
                    :precision="2"
                    class="!w-1/1"
                  >
                    <template #suffix>%</template>
                  </el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="公司金额" prop="corporateAmount" width="140">
                <template #default="scope">
                  <el-input-number
                    v-if="formData.type === HrmInsuranceSchemeType.AMOUNT"
                    v-model="scope.row.corporateAmount"
                    :controls="false"
                    :min="0"
                    :precision="2"
                    class="!w-1/1"
                  />
                  <span v-else>{{ formatHrmMoney(calculateAmount(scope.row, 'corporate')) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="个人金额" prop="personalAmount" width="140">
                <template #default="scope">
                  <el-input-number
                    v-if="formData.type === HrmInsuranceSchemeType.AMOUNT"
                    v-model="scope.row.personalAmount"
                    :controls="false"
                    :min="0"
                    :precision="2"
                    class="!w-1/1"
                  />
                  <span v-else>{{ formatHrmMoney(calculateAmount(scope.row, 'personal')) }}</span>
                </template>
              </el-table-column>
              <el-table-column align="center" label="操作" width="80">
                <template #default="scope">
                  <el-button link type="danger" @click="removeProject(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
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
import type {
  CheckboxValueType,
  DropdownInstance,
  FormInstance,
  FormRules,
  TableColumnCtx
} from 'element-plus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as InsuranceSchemeApi from '@/api/hrm/insurance/scheme'
import * as InsuranceStandardApi from '@/api/hrm/insurance/standard'
import { HrmInsuranceProjectType, HrmInsuranceSchemeType } from '@/views/hrm/utils/constants'
import { formatHrmMoney } from '@/views/hrm/utils/format'
import AreaSelect from '@/views/system/area/components/AreaSelect.vue'

defineOptions({ name: 'HrmInsuranceSchemeForm' })

interface ProjectSummaryParam {
  columns: TableColumnCtx<InsuranceSchemeApi.InsuranceSchemeProjectVO>[]
  data: InsuranceSchemeApi.InsuranceSchemeProjectVO[]
}

type ProjectSectionKey = 'social' | 'providentFund'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const SOCIAL_PROJECT_TYPES = [
  HrmInsuranceProjectType.ENDOWMENT,
  HrmInsuranceProjectType.MEDICAL,
  HrmInsuranceProjectType.UNEMPLOYMENT,
  HrmInsuranceProjectType.EMPLOYMENT_INJURY,
  HrmInsuranceProjectType.MATERNITY,
  HrmInsuranceProjectType.SUPPLEMENTARY_MEDICAL,
  HrmInsuranceProjectType.SUPPLEMENTARY_ENDOWMENT,
  HrmInsuranceProjectType.DISABILITY
]
const PROVIDENT_FUND_PROJECT_TYPES = [HrmInsuranceProjectType.PROVIDENT_FUND] // 公积金项目类型

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型
const standardLoading = ref(false) // 标准参保数据的加载中
const insuranceTypeList = ref<InsuranceStandardApi.InsuranceStandardTypeVO[]>([]) // 标准参保方案列表
const formData = ref<InsuranceSchemeApi.InsuranceSchemeVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '方案名称不能为空', trigger: 'blur' }],
  areaId: [{ required: true, message: '参保城市不能为空', trigger: 'change' }],
  type: [{ required: true, message: '方案类型不能为空', trigger: 'change' }],
  projectList: [{ validator: validateProjectList, trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref
const projectDropdownRefs = ref<Partial<Record<ProjectSectionKey, DropdownInstance>>>({}) // 项目下拉 Ref

/** 社保、公积金项目分区 */
const projectSections = computed(() => [
  {
    key: 'social' as const,
    label: '社保',
    projects: formData.value.projectList?.filter((project) => isSocialProject(project.type)) || [],
    options: getProjectOptions(SOCIAL_PROJECT_TYPES),
    customType: HrmInsuranceProjectType.CUSTOM_SOCIAL_SECURITY
  },
  {
    key: 'providentFund' as const,
    label: '公积金',
    projects:
      formData.value.projectList?.filter((project) => isProvidentFundProject(project.type)) || [],
    options: getProjectOptions(PROVIDENT_FUND_PROJECT_TYPES),
    customType: HrmInsuranceProjectType.CUSTOM_PROVIDENT_FUND
  }
])

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (!id) {
    return
  }
  formLoading.value = true
  try {
    // 获取表单数据
    formData.value = await InsuranceSchemeApi.getInsuranceScheme(id)
    if (formData.value.areaId) {
      await getInsuranceTypeList(formData.value.areaId)
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits<{ success: [] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) {
    return
  }
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await InsuranceSchemeApi.createInsuranceScheme(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await message.confirm('编辑参保方案后，不会变更现有参保信息，确定提交吗？')
      await InsuranceSchemeApi.updateInsuranceScheme(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 添加参保项目 */
function addProject(type: number) {
  formData.value.projectList ||= []
  formData.value.projectList.push(createProject(type))
}

/** 设置项目下拉 Ref */
function setProjectDropdownRef(key: ProjectSectionKey, dropdown: unknown) {
  if (dropdown) {
    projectDropdownRefs.value[key] = dropdown as DropdownInstance
  } else {
    delete projectDropdownRefs.value[key]
  }
}

/** 勾选或取消标准参保项目 */
function handleProjectChecked(checked: CheckboxValueType, type: number) {
  const project = formData.value.projectList?.find((item) => item.type === type)
  if (checked) {
    if (!project) {
      addProject(type)
    }
    return
  }
  if (project) {
    removeProject(project)
  }
}

/** 添加自定义参保项目 */
function addCustomProject(key: ProjectSectionKey, type: number) {
  projectDropdownRefs.value[key]?.handleClose()
  addProject(type)
}

/** 删除参保项目 */
function removeProject(project: InsuranceSchemeApi.InsuranceSchemeProjectVO) {
  const index = formData.value.projectList?.indexOf(project) ?? -1
  if (index >= 0) {
    formData.value.projectList?.splice(index, 1)
  }
}

/** 获得标准参保项目选项 */
function getProjectOptions(types: number[]) {
  return types.map((type) => ({ label: getProjectTypeName(type), value: type }))
}

/** 标准参保项目是否已添加 */
function isProjectTypeUsed(type: number) {
  return formData.value.projectList?.some((project) => project.type === type) || false
}

/** 查询标准参保方案 */
async function getInsuranceTypeList(areaId: number) {
  standardLoading.value = true
  try {
    const data = await InsuranceStandardApi.getInsuranceStandardTypeList(areaId)
    if (formData.value.areaId !== areaId) {
      return
    }
    insuranceTypeList.value = data
    const selectedType = data.find(
      (item) =>
        item.name === formData.value.householdType && item.code !== formData.value.householdType
    )
    if (selectedType) {
      formData.value.householdType = selectedType.code
    }
  } finally {
    standardLoading.value = false
  }
}

/** 切换参保城市 */
async function handleAreaChange(areaId?: number) {
  formData.value.householdType = ''
  insuranceTypeList.value = []
  resetStandardProjectValues()
  if (areaId) {
    await getInsuranceTypeList(areaId)
  }
}

/** 切换标准参保方案 */
async function handleHouseTypeChange(typeCode?: string) {
  const areaId = formData.value.areaId
  if (!areaId || !typeCode) {
    return
  }
  standardLoading.value = true
  try {
    const projects = await InsuranceStandardApi.getInsuranceStandardProjectList({
      areaId,
      typeCode
    })
    if (formData.value.areaId !== areaId || formData.value.householdType !== typeCode) {
      return
    }
    const customProjects =
      formData.value.projectList?.filter((project) => isCustomProject(project.type)) || []
    formData.value.projectList = [
      ...projects.map((project) => ({
        ...project,
        id: undefined,
        schemeId: undefined,
        name: getProjectTypeName(project.type)
      })),
      ...customProjects
    ]
  } finally {
    standardLoading.value = false
  }
}

/** 清空标准参保项目金额 */
function resetStandardProjectValues() {
  formData.value.projectList?.forEach((project) => {
    if (isCustomProject(project.type)) {
      return
    }
    project.baseAmount = 0
    project.corporateRate = 0
    project.personalRate = 0
    project.corporateAmount = 0
    project.personalAmount = 0
  })
}

/** 校验参保项目 */
function validateProjectList(
  _rule: unknown,
  value: InsuranceSchemeApi.InsuranceSchemeProjectVO[] | undefined,
  callback: (error?: Error) => void
) {
  if (!value?.some((project) => isSocialProject(project.type))) {
    callback(new Error('请至少添加一个社保项目'))
    return
  }
  if (value.some((project) => !project.name?.trim())) {
    callback(new Error('参保项目名称不能为空'))
    return
  }
  callback()
}

/** 计算项目缴纳金额 */
function calculateAmount(
  project: InsuranceSchemeApi.InsuranceSchemeProjectVO,
  type: 'corporate' | 'personal'
) {
  const proportion = type === 'corporate' ? project.corporateRate : project.personalRate
  return Number(project.baseAmount || 0) * Number(proportion || 0) * 0.01
}

/** 计算参保项目合计 */
function getProjectSummaries({ columns, data }: ProjectSummaryParam) {
  return columns.map((column, index) => {
    if (index === 0) {
      return '总计'
    }
    if (!['corporateAmount', 'personalAmount'].includes(String(column.property))) {
      return ''
    }
    const type = column.property === 'corporateAmount' ? 'corporate' : 'personal'
    return formatHrmMoney(
      data.reduce(
        (total, project) =>
          total +
          (formData.value.type === HrmInsuranceSchemeType.PROPORTION
            ? calculateAmount(project, type)
            : Number(
                project[column.property as keyof InsuranceSchemeApi.InsuranceSchemeProjectVO] || 0
              )),
        0
      )
    )
  })
}

/** 获得项目类型名称 */
function getProjectTypeName(type?: number) {
  return getDictLabel(DICT_TYPE.HRM_INSURANCE_PROJECT_TYPE, type)
}

/** 是否为自定义项目 */
function isCustomProject(type?: number) {
  return (
    type === HrmInsuranceProjectType.CUSTOM_SOCIAL_SECURITY ||
    type === HrmInsuranceProjectType.CUSTOM_PROVIDENT_FUND
  )
}

/** 是否为社保项目 */
function isSocialProject(type?: number) {
  return type !== undefined && type < HrmInsuranceProjectType.PROVIDENT_FUND
}

/** 是否为公积金项目 */
function isProvidentFundProject(type?: number) {
  return type !== undefined && type >= HrmInsuranceProjectType.PROVIDENT_FUND
}

/** 创建参保项目 */
function createProject(type: number): InsuranceSchemeApi.InsuranceSchemeProjectVO {
  return {
    type,
    name: isCustomProject(type) ? '' : getProjectTypeName(type),
    baseAmount: 0,
    corporateRate: 0,
    personalRate: 0,
    corporateAmount: 0,
    personalAmount: 0
  }
}

/** 创建表单默认数据 */
function createDefaultFormData(): InsuranceSchemeApi.InsuranceSchemeVO {
  return {
    id: undefined,
    name: '',
    areaId: undefined,
    householdType: '',
    type: HrmInsuranceSchemeType.PROPORTION,
    projectList: [
      HrmInsuranceProjectType.ENDOWMENT,
      HrmInsuranceProjectType.MEDICAL,
      HrmInsuranceProjectType.UNEMPLOYMENT,
      HrmInsuranceProjectType.EMPLOYMENT_INJURY,
      HrmInsuranceProjectType.MATERNITY,
      HrmInsuranceProjectType.PROVIDENT_FUND
    ].map(createProject)
  }
}

/** 重置表单 */
function resetForm() {
  insuranceTypeList.value = []
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}
</script>
