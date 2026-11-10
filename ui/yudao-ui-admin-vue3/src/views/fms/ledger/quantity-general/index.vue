<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <FmsLedgerSearchBar
      :end-month="queryParams.endMonth"
      :export-loading="exportLoading"
      permission-prefix="fms:ledger:general"
      :max-level="FMS_SUBJECT_LEVEL_MAX"
      print-target="fms-quantity-general-ledger-table"
      print-title="数量金额总账"
      :start-month="queryParams.startMonth"
      @export="handleExport"
      @search="handleQuery"
    />
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-quantity-general-ledger-table"
      v-loading="loading"
      :data="list"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column align="center" label="科目编码" prop="subjectCode" width="125" />
      <el-table-column label="科目名称" min-width="150" prop="subjectName" />
      <el-table-column align="center" label="单位" prop="quantityUnit" width="80" />
      <el-table-column align="center" label="期初余额">
        <el-table-column align="center" label="方向" prop="openingBalanceDirection" width="70" />
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.openingQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="单价" width="110">
          <template #default="scope">{{ formatMoney(scope.row.openingUnitPrice) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(getOpeningAmount(scope.row)) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本期借方">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.periodDebitQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.periodDebitAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本期贷方">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.periodCreditQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.periodCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本年累计借方">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.yearDebitQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.yearDebitAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本年累计贷方">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.yearCreditQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.yearCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="期末余额">
        <el-table-column align="center" label="方向" prop="endingBalanceDirection" width="70" />
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.endingQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="单价" width="110">
          <template #default="scope">{{ formatMoney(scope.row.endingUnitPrice) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(getEndingAmount(scope.row)) }}</template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsLedgerListReqVO, FmsSubjectBalanceVO } from '@/api/fms/ledger/types'
import { treeToList } from '@/utils/tree'
import FmsLedgerSearchBar from '@/views/fms/ledger/components/FmsLedgerSearchBar.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_SUBJECT_LEVEL_MAX, FMS_SUBJECT_LEVEL_MIN } from '@/views/fms/utils/constants'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsQuantityGeneralLedger' })

const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const list = ref<FmsSubjectBalanceVO[]>([]) // 数量金额总账列表
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const queryParams = reactive<FmsLedgerListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth,
  minLevel: FMS_SUBJECT_LEVEL_MIN,
  maxLevel: FMS_SUBJECT_LEVEL_MAX
})

watch(accountSetId, () => init())

/** 初始化数量金额总账页面 */
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

/** 查询数量金额总账 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    list.value = treeToList<FmsSubjectBalanceVO[]>(
      await FmsLedgerApi.getQuantityGeneralList(queryParams)
    )
  } finally {
    loading.value = false
  }
}

/** 处理查询条件变化 */
function handleQuery(value: Omit<FmsLedgerListReqVO, 'accountSetId'>) {
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  getList()
}

/** 获得期初余额金额 */
function getOpeningAmount(row: FmsSubjectBalanceVO) {
  return row.openingDebitAmount || row.openingCreditAmount
}

/** 获得期末余额金额 */
function getEndingAmount(row: FmsSubjectBalanceVO) {
  return row.endingDebitAmount || row.endingCreditAmount
}

/** 导出数量金额总账 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportQuantityGeneral(queryParams)
    download.excel(
      data,
      buildPeriodFilename('数量金额总账', queryParams.startMonth, queryParams.endMonth)
    )
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  init()
})
</script>
