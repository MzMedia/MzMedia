<template>
  <ContentWrap title="历史月度工资">
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column label="计薪月份" width="110">
        <template #default="scope">
          {{ formatHrmYearMonth(scope.row.year, scope.row.month) }}
        </template>
      </el-table-column>
      <el-table-column label="计薪周期" min-width="150">
        <template #default="scope">
          {{ scope.row.actualWorkDay ?? '-' }} / {{ scope.row.needWorkDay ?? '-' }} 天
        </template>
      </el-table-column>
      <el-table-column label="应发工资" align="right" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.expectedPaySalary) }}</template>
      </el-table-column>
      <el-table-column label="个人所得税" align="right" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.personalTax) }}</template>
      </el-table-column>
      <el-table-column label="实发工资" align="right" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.realPaySalary) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="80">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 工资明细弹窗 -->
  <Dialog v-model="detailVisible" title="工资明细" width="620px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="计薪月份">
        {{ formatHrmYearMonth(detail?.year, detail?.month) }}
      </el-descriptions-item>
      <el-descriptions-item label="出勤天数">
        {{ detail?.actualWorkDay ?? '-' }} / {{ detail?.needWorkDay ?? '-' }} 天
      </el-descriptions-item>
      <el-descriptions-item label="应发工资">
        {{ formatHrmMoney(detail?.expectedPaySalary) }}
      </el-descriptions-item>
      <el-descriptions-item label="个人所得税">
        {{ formatHrmMoney(detail?.personalTax) }}
      </el-descriptions-item>
      <el-descriptions-item label="实发工资" :span="2">
        {{ formatHrmMoney(detail?.realPaySalary) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-table v-if="detail?.optionValues?.length" :data="detail.optionValues" class="mt-16px">
      <el-table-column label="工资项" prop="name" min-width="180" />
      <el-table-column label="金额" align="right" width="140">
        <template #default="scope">{{ formatHrmMoney(scope.row.value) }}</template>
      </el-table-column>
    </el-table>
  </Dialog>
</template>

<script lang="ts" setup>
import * as SalaryMonthEmployeeRecordApi from '@/api/hrm/salary/month-record/employee'
import type { SalaryMonthEmployeeRecordVO } from '@/api/hrm/salary/month-record/employee'
import { HrmSalaryMonthStatus } from '@/views/hrm/utils/constants'
import { formatHrmMoney, formatHrmYearMonth } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmEmployeeSalaryHistoryList' })

const props = defineProps<{
  employeeId: number
}>()

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<SalaryMonthEmployeeRecordVO[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
}) // 查询参数

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      employeeId: props.employeeId,
      monthRecordStatus: HrmSalaryMonthStatus.HISTORY
    }
    const data = await SalaryMonthEmployeeRecordApi.getSalaryEmployeeMonthRecordPage(params)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const detailVisible = ref(false) // 工资明细弹窗是否展示
const detail = ref<SalaryMonthEmployeeRecordVO>() // 工资明细

/** 打开详情 */
function openDetail(row: SalaryMonthEmployeeRecordVO) {
  detail.value = row
  detailVisible.value = true
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
