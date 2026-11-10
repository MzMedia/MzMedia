<template>
  <div v-loading="loading">
    <el-alert :title="employeeReminder" type="info" show-icon :closable="false" class="mb-15px" />

    <ContentWrap title="基本信息">
      <template #header>
        <el-button
          v-if="hasEditableFields"
          v-hasPermi="['hrm:portal:employee:update']"
          class="ml-auto"
          link
          type="primary"
          @click="emit('edit')"
        >
          编辑
        </el-button>
      </template>
      <el-descriptions :column="4" border>
        <el-descriptions-item v-if="isVisible('name')" label="姓名">
          {{ employee.name || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('sex')" label="性别">
          <dict-tag
            v-if="employee.sex != null"
            :type="DICT_TYPE.SYSTEM_USER_SEX"
            :value="employee.sex"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('birthday')" label="出生时间">
          {{ formatHrmDateTime(employee.birthday) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('age')" label="年龄">
          {{ employee.age ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('country')" label="国家或地区">
          {{ employee.country || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('nation')" label="民族">
          {{ employee.nation || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('nativePlace')" label="籍贯">
          {{ employee.nativePlace || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('highestEducation')" label="最高学历">
          <dict-tag
            v-if="employee.highestEducation != null"
            :type="DICT_TYPE.HRM_EMPLOYEE_EDUCATION"
            :value="employee.highestEducation"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('idType')" label="证件类型">
          {{ formatEmployeeIdType(employee.idType) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('idNumber')" label="证件号码">
          {{ employee.idNumber || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <ContentWrap v-if="hasVisibleContactFields" title="通讯信息">
      <el-descriptions :column="4" border>
        <el-descriptions-item v-if="isVisible('mobile')" label="手机号">
          {{ employee.mobile || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('email')" label="邮箱">
          {{ employee.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="isVisible('address')" label="户籍地址" :span="4">
          {{ employee.address || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <ContentWrap title="教育经历">
      <el-table v-if="educationExperienceList.length" :data="educationExperienceList" border>
        <el-table-column label="学历" width="110">
          <template #default="{ row }">
            <dict-tag :type="DICT_TYPE.HRM_EMPLOYEE_EDUCATION" :value="row.education" />
          </template>
        </el-table-column>
        <el-table-column label="毕业院校" prop="graduateSchool" min-width="160" />
        <el-table-column label="专业" prop="major" min-width="130" />
        <el-table-column label="入学日期" width="120">
          <template #default="{ row }">{{ formatHrmDate(row.admissionTime) }}</template>
        </el-table-column>
        <el-table-column label="毕业日期" width="120">
          <template #default="{ row }">{{ formatHrmDate(row.graduationTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-else :image-size="70" description="暂无数据" />
    </ContentWrap>

    <ContentWrap title="工作经历">
      <el-table v-if="workExperienceList.length" :data="workExperienceList" border>
        <el-table-column label="工作单位" prop="workUnit" min-width="170" />
        <el-table-column label="职务" prop="postName" min-width="130" />
        <el-table-column label="开始日期" width="120">
          <template #default="{ row }">{{ formatHrmDate(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束日期" width="120">
          <template #default="{ row }">{{ formatHrmDate(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="离职原因" prop="reason" min-width="180" />
      </el-table>
      <el-empty v-else :image-size="70" description="暂无数据" />
    </ContentWrap>

    <ContentWrap title="证书/证件">
      <el-table v-if="certificateList.length" :data="certificateList" border>
        <el-table-column label="证书名称" prop="name" min-width="160" />
        <el-table-column label="级别" prop="level" width="110" />
        <el-table-column label="证书编号" prop="no" min-width="150" />
        <el-table-column label="发证机构" prop="issuingAuthority" min-width="150" />
        <el-table-column label="发证日期" width="120">
          <template #default="{ row }">{{ formatHrmDate(row.issuingTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-else :image-size="70" description="暂无数据" />
    </ContentWrap>

    <ContentWrap title="培训经历">
      <el-table v-if="trainingExperienceList.length" :data="trainingExperienceList" border>
        <el-table-column label="培训课程" prop="course" min-width="150" />
        <el-table-column label="培训机构" prop="organizationName" min-width="150" />
        <el-table-column label="培训时间" min-width="230">
          <template #default="{ row }">
            {{ formatHrmDate(row.startTime) }} 至 {{ formatHrmDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="培训成绩" prop="result" width="110" />
        <el-table-column label="培训证书" prop="certificateName" min-width="150" />
      </el-table>
      <el-empty v-else :image-size="70" description="暂无数据" />
    </ContentWrap>

    <ContentWrap title="联系人">
      <el-table v-if="contactList.length" :data="contactList" border>
        <el-table-column label="联系人" prop="name" width="120" />
        <el-table-column label="关系" prop="relation" width="100" />
        <el-table-column label="联系电话" prop="phone" width="140" />
        <el-table-column label="工作单位" prop="workUnit" min-width="150" />
        <el-table-column label="联系地址" prop="address" min-width="180" />
      </el-table>
      <el-empty v-else :image-size="70" description="暂无数据" />
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import type { HrmEmployeeCertificateVO } from '@/api/hrm/employee/certificate'
import type { HrmEmployeeContactVO } from '@/api/hrm/employee/contact'
import type { HrmEmployeeFieldConfigVO } from '@/api/hrm/employee/config'
import type { HrmEmployeeEducationExperienceVO } from '@/api/hrm/employee/education-experience'
import type { HrmEmployeeTrainingExperienceVO } from '@/api/hrm/employee/training-experience'
import type { HrmEmployeeWorkExperienceVO } from '@/api/hrm/employee/work-experience'
import * as CertificateApi from '@/api/hrm/portal/employee/certificate'
import * as ContactApi from '@/api/hrm/portal/employee/contact'
import * as EducationExperienceApi from '@/api/hrm/portal/employee/education-experience'
import * as TrainingExperienceApi from '@/api/hrm/portal/employee/training-experience'
import * as WorkExperienceApi from '@/api/hrm/portal/employee/work-experience'
import type { HrmPortalEmployeeVO } from '@/api/hrm/portal/employee'
import { DICT_TYPE } from '@/utils/dict'
import { formatEmployeeIdType, formatHrmDate, formatHrmDateTime } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalEmployeeBaseInfo' })

const props = defineProps<{
  employee: HrmPortalEmployeeVO
  fieldConfigList: HrmEmployeeFieldConfigVO[]
}>()
const emit = defineEmits<{
  edit: []
}>() // 定义 edit 事件

const loading = ref(false) // 列表加载中
const educationExperienceList = ref<HrmEmployeeEducationExperienceVO[]>([]) // 教育经历列表
const workExperienceList = ref<HrmEmployeeWorkExperienceVO[]>([]) // 工作经历列表
const certificateList = ref<HrmEmployeeCertificateVO[]>([]) // 证书列表
const trainingExperienceList = ref<HrmEmployeeTrainingExperienceVO[]>([]) // 培训经历列表
const contactList = ref<HrmEmployeeContactVO[]>([]) // 联系人列表
const hasEditableFields = computed(() => props.fieldConfigList.some((field) => field.editable)) // 是否存在可编辑字段
const visibleFieldNames = computed(
  () => new Set(props.fieldConfigList.filter((field) => field.visible).map((field) => field.name))
)
const hasVisibleContactFields = computed(
  () => isVisible('mobile') || isVisible('email') || isVisible('address')
)
const employeeReminder = computed(() =>
  hasEditableFields.value
    ? '可编辑的信息由公司管理员设置，如有问题，请联系公司管理员。'
    : '您的编辑权限已被管理员关闭，如有问题，请联系公司管理员。'
)

/** 判断字段是否允许员工查看 */
function isVisible(name: string) {
  return visibleFieldNames.value.has(name)
}

/** 获得员工个人信息各子模块 */
async function getList() {
  loading.value = true
  try {
    const [educationExperiences, workExperiences, certificates, trainingExperiences, contacts] =
      await Promise.all([
        EducationExperienceApi.getEmployeeEducationExperienceList(),
        WorkExperienceApi.getEmployeeWorkExperienceList(),
        CertificateApi.getEmployeeCertificateList(),
        TrainingExperienceApi.getEmployeeTrainingExperienceList(),
        ContactApi.getEmployeeContactList()
      ])
    educationExperienceList.value = educationExperiences
    workExperienceList.value = workExperiences
    certificateList.value = certificates
    trainingExperienceList.value = trainingExperiences
    contactList.value = contacts
  } finally {
    loading.value = false
  }
}

defineExpose({ getList }) // 提供 getList 方法，用于刷新列表

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
