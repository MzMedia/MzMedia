<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <FmsLedgerSearchBar
      :end-month="queryParams.endMonth"
      :export-loading="exportLoading"
      permission-prefix="fms:ledger:general"
      print-target="fms-general-ledger-table"
      print-title="总账"
      :start-month="queryParams.startMonth"
      @export="handleExport"
      @search="handleQuery"
    />
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-general-ledger-table"
      v-loading="loading"
      :data="list"
      :row-class-name="() => 'font-500'"
      :span-method="spanSubjectColumns"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column align="center" label="科目编码" prop="subjectCode" width="125">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)">
            {{ scope.row.subjectCode }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="科目名称" prop="subjectName" min-width="160" />
      <el-table-column align="center" label="期间" prop="period" width="100" />
      <el-table-column label="摘要" prop="digest" min-width="130" />
      <el-table-column align="right" label="借方" width="140">
        <template #default="scope">{{ formatMoney(scope.row.debitAmount) }}</template>
      </el-table-column>
      <el-table-column align="right" label="贷方" width="140">
        <template #default="scope">{{ formatMoney(scope.row.creditAmount) }}</template>
      </el-table-column>
      <el-table-column align="center" label="方向" prop="balanceDirection" width="80" />
      <el-table-column align="right" label="余额" width="150">
        <template #default="scope">{{ formatMoney(scope.row.balance) }}</template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsLedgerGeneralVO, FmsLedgerListReqVO } from '@/api/fms/ledger/types'
import FmsLedgerSearchBar from '@/views/fms/ledger/components/FmsLedgerSearchBar.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsGeneralLedger' })

const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const list = ref<FmsLedgerGeneralVO[]>([]) // 总账列表
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const queryParams = reactive<FmsLedgerListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth,
  minLevel: 1,
  maxLevel: 1
})

watch(accountSetId, () => init())

/** 初始化总账页面 */
async function init() {
  if (!accountSetId.value) {
    list.value = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const accountingMonth = await fmsStore.loadCurrentMonth()
  if (accountingMonth) {
    queryParams.startMonth = accountingMonth
    queryParams.endMonth = accountingMonth
  }
  await getList()
}

/** 查询总账列表 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    list.value = await FmsLedgerApi.getGeneralList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 处理查询条件变化 */
function handleQuery(value: Omit<FmsLedgerListReqVO, 'accountSetId'>) {
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  getList()
}

/** 打开科目明细账 */
function openDetail(row: FmsLedgerGeneralVO) {
  router.push({
    path: '/fms/ledger/detail',
    query: {
      subjectId: row.subjectId,
      startMonth: queryParams.startMonth,
      endMonth: queryParams.endMonth
    }
  })
}

/** 合并相同科目的编码和名称列 */
function spanSubjectColumns({ rowIndex, columnIndex }: { rowIndex: number; columnIndex: number }) {
  if (columnIndex > 1) return [1, 1]
  const subjectId = list.value[rowIndex]?.subjectId
  if (rowIndex > 0 && list.value[rowIndex - 1]?.subjectId === subjectId) return [0, 0]
  let rowspan = 1
  while (list.value[rowIndex + rowspan]?.subjectId === subjectId) {
    rowspan++
  }
  return [rowspan, 1]
}

/** 导出总账 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportGeneral(queryParams)
    download.excel(data, buildPeriodFilename('总账', queryParams.startMonth, queryParams.endMonth))
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  init()
})
</script>
