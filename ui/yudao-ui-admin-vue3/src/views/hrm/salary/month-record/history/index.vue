<template>
  <doc-alert title="【薪资】月度工资、工资条" url="https://doc.iocoder.cn/hrm/salary/payroll/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="年份" prop="year">
        <el-date-picker
          v-model="queryParams.year"
          class="!w-160px"
          clearable
          placeholder="请选择年份"
          type="year"
          value-format="YYYY"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 历史工资表列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column fixed="left" label="工资表" min-width="180" prop="title">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.title || '-' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="月份" width="100">
        <template #default="scope">
          {{ formatHrmYearMonth(scope.row.year, scope.row.month) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="计薪人数" prop="employeeCount" width="100" />
      <el-table-column align="right" label="应发工资" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.expectedPaySalary) }}</template>
      </el-table-column>
      <el-table-column align="right" label="实发工资" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.realPaySalary) }}</template>
      </el-table-column>
      <el-table-column align="right" label="个税总额" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.personalTax) }}</template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="90">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">详情</el-button>
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
</template>

<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import * as SalaryMonthRecordApi from '@/api/hrm/salary/month-record'
import type { SalaryMonthRecordVO } from '@/api/hrm/salary/month-record'
import { HrmSalaryMonthStatus } from '@/views/hrm/utils/constants'
import { formatHrmMoney, formatHrmYearMonth } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmSalaryHistory' })

const router = useRouter() // 路由
const loading = ref(false) // 列表加载中
const total = ref(0) // 列表总数
const list = ref<SalaryMonthRecordVO[]>([]) // 历史工资表列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  year: String(new Date().getFullYear())
}) // 查询参数
const queryFormRef = ref<FormInstance>() // 查询表单

/** 查询历史工资表列表 */
async function getList() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      year: queryParams.year ? Number(queryParams.year) : undefined,
      status: HrmSalaryMonthStatus.HISTORY
    }
    const data = await SalaryMonthRecordApi.getSalaryMonthRecordPage(params)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置搜索 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 打开历史工资表详情 */
function openDetail(id?: number) {
  if (!id) {
    return
  }
  router.push({ name: 'HrmSalaryHistoryDetail', params: { id } })
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
