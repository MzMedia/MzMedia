<template>
  <el-drawer v-model="drawerVisible" destroy-on-close size="980px" title="员工月度社保详情">
    <div v-loading="loading" class="min-h-320px">
      <div class="mb-18px flex items-start justify-between gap-16px">
        <div class="min-w-0">
          <div class="flex items-center gap-10px">
            <span class="truncate text-20px font-600">{{ detail?.employeeName || '--' }}</span>
            <dict-tag :type="DICT_TYPE.HRM_INSURANCE_EMP_STATUS" :value="detail?.status ?? ''" />
          </div>
          <div class="mt-6px text-13px text-[var(--el-text-color-secondary)]">
            {{ detail?.postName || '--' }} · {{ detail?.year || '--' }} 年
            {{ detail?.month || '--' }} 月
          </div>
        </div>
        <el-button
          v-if="editable && detail"
          v-hasPermi="['hrm:insurance:month-record:update']"
          plain
          type="primary"
          @click="handleEdit"
        >
          <Icon icon="ep:edit" class="mr-5px" />编辑
        </el-button>
      </div>

      <el-descriptions :column="3" border class="mb-20px">
        <el-descriptions-item label="性别">
          <dict-tag
            v-if="detail?.sex != null"
            :type="DICT_TYPE.SYSTEM_USER_SEX"
            :value="detail.sex"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detail?.age ?? '--' }}</el-descriptions-item>
        <el-descriptions-item label="工号">{{ detail?.jobNumber || '--' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detail?.deptName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="员工状态">
          <dict-tag
            v-if="detail?.employeeStatus != null"
            :type="DICT_TYPE.HRM_EMPLOYEE_STATUS"
            :value="detail.employeeStatus"
          />
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="入职日期">
          {{ formatHrmDate(detail?.entryTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="参保城市">{{ detail?.areaName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detail?.idNumber || '--' }}</el-descriptions-item>
        <el-descriptions-item label="个人社保号">
          {{ detail?.socialSecurityNumber || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="个人公积金号">
          {{ detail?.accumulationFundNumber || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="参保方案">
          {{ detail?.schemeName || '--' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="mb-10px text-15px font-600">缴费项目</div>
      <el-table :data="projects" border show-summary :summary-method="projectSummary">
        <el-table-column label="缴纳项目" min-width="130">
          <template #default="scope">{{ formatHrmInsuranceProjectName(scope.row) }}</template>
        </el-table-column>
        <el-table-column align="right" label="缴纳基数" min-width="100" prop="baseAmount">
          <template #default="scope">{{ formatHrmMoney(scope.row.baseAmount) }}</template>
        </el-table-column>
        <el-table-column
          v-if="detail?.schemeType === HrmInsuranceSchemeType.PROPORTION"
          align="right"
          label="企业比例"
          min-width="90"
          prop="corporateRate"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.corporateRate) }}</template>
        </el-table-column>
        <el-table-column
          v-if="detail?.schemeType === HrmInsuranceSchemeType.PROPORTION"
          align="right"
          label="个人比例"
          min-width="90"
          prop="personalRate"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.personalRate) }}</template>
        </el-table-column>
        <el-table-column align="right" label="个人缴纳" min-width="100" prop="personalAmount">
          <template #default="scope">{{ formatHrmMoney(scope.row.personalAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="企业缴纳" min-width="100" prop="corporateAmount">
          <template #default="scope">{{ formatHrmMoney(scope.row.corporateAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="合计缴费" min-width="100" prop="totalAmount">
          <template #default="scope">{{ formatHrmMoney(scope.row.totalAmount) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import type { TableColumnCtx } from 'element-plus'
import { DICT_TYPE } from '@/utils/dict'
import * as InsuranceMonthEmployeeRecordApi from '@/api/hrm/insurance/month-record/employee'
import type { InsuranceSchemeProjectVO } from '@/api/hrm/insurance/scheme'
import { HrmInsuranceSchemeType } from '@/views/hrm/utils/constants'
import {
  formatHrmDate,
  formatHrmInsuranceProjectName,
  formatHrmMoney,
  formatHrmRate
} from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmInsuranceMonthEmployeeDetail' })

defineProps<{
  editable?: boolean
}>()

type DetailProject = InsuranceSchemeProjectVO & { totalAmount: number }
interface SummaryParam {
  columns: TableColumnCtx<DetailProject>[]
  data: DetailProject[]
}

const emit = defineEmits<{
  edit: [detail: InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeRecordVO]
}>() // 定义 edit 事件，用于打开编辑

const drawerVisible = ref(false) // 抽屉的是否展示
const loading = ref(false) // 详情的加载中
const detail = ref<InsuranceMonthEmployeeRecordApi.InsuranceMonthEmployeeRecordVO>() // 员工月度社保详情
const projects = computed<DetailProject[]>(() => {
  if (!detail.value) {
    return []
  }
  return [...detail.value.socialSecurityProjectList, ...detail.value.providentFundProjectList].map(
    (project) => ({
      ...project,
      totalAmount: Number(project.personalAmount || 0) + Number(project.corporateAmount || 0)
    })
  )
})

/** 打开详情 */
async function open(id?: number) {
  if (!id) {
    return
  }
  drawerVisible.value = true
  loading.value = true
  detail.value = undefined
  try {
    // 获取详情数据
    detail.value = await InsuranceMonthEmployeeRecordApi.getInsuranceMonthEmployeeRecord(id)
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

/** 编辑员工月度社保 */
function handleEdit() {
  if (detail.value) {
    emit('edit', detail.value)
  }
}

/** 计算参保项目合计 */
function projectSummary({ columns, data }: SummaryParam) {
  return columns.map((column, index) => {
    if (index === 0) {
      return '缴费总价'
    }
    if (!['corporateAmount', 'personalAmount', 'totalAmount'].includes(String(column.property))) {
      return ''
    }
    return formatHrmMoney(
      data.reduce(
        (total, project) => total + Number(project[column.property as keyof DetailProject] || 0),
        0
      )
    )
  })
}
</script>
