<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <FmsLedgerSearchBar
      :end-month="queryParams.endMonth"
      :export-loading="exportLoading"
      permission-prefix="fms:ledger:detail"
      print-target="fms-quantity-detail-ledger-table"
      print-title="数量金额明细账"
      :subjects="quantitySubjects"
      :show-subject="true"
      :start-month="queryParams.startMonth"
      :subject-id="queryParams.subjectId"
      @export="handleExport"
      @search="handleQuery"
    />
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-quantity-detail-ledger-table"
      v-loading="loading"
      :data="list"
      :row-class-name="getRowClassName"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column align="center" label="日期" prop="accountDate" width="110" />
      <el-table-column align="center" label="凭证字号" width="110">
        <template #default="scope">
          <el-button
            v-if="scope.row.voucherId"
            v-hasPermi="['fms:voucher:query']"
            link
            type="primary"
            @click="openVoucher(scope.row)"
          >
            {{ scope.row.voucherNumber }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="摘要" min-width="160" prop="digest" />
      <el-table-column align="center" label="借方发生额">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.debitQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="单价" width="110">
          <template #default="scope">{{ formatMoney(getDebitUnitPrice(scope.row)) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.debitAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="贷方发生额">
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.creditQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="单价" width="110">
          <template #default="scope">{{ formatMoney(getCreditUnitPrice(scope.row)) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.creditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="余额">
        <el-table-column align="center" label="方向" prop="balanceDirection" width="70" />
        <el-table-column align="right" label="数量" width="105">
          <template #default="scope">{{ formatMoney(scope.row.balanceQuantity) }}</template>
        </el-table-column>
        <el-table-column align="right" label="单价" width="110">
          <template #default="scope">{{ formatMoney(getBalanceUnitPrice(scope.row)) }}</template>
        </el-table-column>
        <el-table-column align="right" label="金额" width="125">
          <template #default="scope">{{ formatMoney(scope.row.balance) }}</template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsLedgerDetailVO, FmsLedgerListReqVO } from '@/api/fms/ledger/types'
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { handleTree, treeToList } from '@/utils/tree'
import FmsLedgerSearchBar from '@/views/fms/ledger/components/FmsLedgerSearchBar.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsQuantityDetailLedger' })

const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const list = ref<FmsLedgerDetailVO[]>([]) // 数量金额明细账列表
const quantitySubjects = ref<FmsSubjectVO[]>([]) // 数量核算科目树
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const queryParams = reactive<FmsLedgerListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth
})

watch(accountSetId, () => init())

/** 初始化数量金额明细账页面 */
async function init() {
  if (!accountSetId.value) {
    quantitySubjects.value = []
    list.value = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const [subjectList, accountingMonth] = await Promise.all([
    FmsSubjectApi.getSubjectSimpleList(accountSetId.value),
    fmsStore.loadCurrentMonth()
  ])
  quantitySubjects.value = filterQuantitySubjects(handleTree(subjectList))
  if (accountingMonth) {
    queryParams.startMonth = accountingMonth
    queryParams.endMonth = accountingMonth
  }
  queryParams.subjectId = treeToList<FmsSubjectVO[]>(quantitySubjects.value)[0]?.id
  await getList()
}

/** 查询数量金额明细账 */
async function getList() {
  if (!accountSetId.value || !queryParams.subjectId) {
    list.value = []
    return
  }
  loading.value = true
  try {
    list.value = await FmsLedgerApi.getQuantityDetailList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 处理查询条件变化 */
function handleQuery(value: Omit<FmsLedgerListReqVO, 'accountSetId'>) {
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  getList()
}

/** 计算借方单价 */
function getDebitUnitPrice(row: FmsLedgerDetailVO) {
  return row.debitQuantity ? row.debitAmount / row.debitQuantity : undefined
}

/** 计算贷方单价 */
function getCreditUnitPrice(row: FmsLedgerDetailVO) {
  return row.creditQuantity ? row.creditAmount / row.creditQuantity : undefined
}

/** 计算余额单价 */
function getBalanceUnitPrice(row: FmsLedgerDetailVO) {
  return row.balanceQuantity ? row.balance / row.balanceQuantity : undefined
}

/** 获得汇总行样式类名 */
function getRowClassName({ row }: { row: FmsLedgerDetailVO }) {
  return row.rowType === 2 ? '' : '[--el-table-tr-bg-color:var(--el-fill-color-light)] font-600'
}

/** 打开凭证详情 */
function openVoucher(row: FmsLedgerDetailVO) {
  router.push({ path: '/fms/voucher/create', query: { id: row.voucherId } })
}

/** 导出数量金额明细账 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportQuantityDetail(queryParams)
    download.excel(
      data,
      buildPeriodFilename('数量金额明细账', queryParams.startMonth, queryParams.endMonth)
    )
  } finally {
    exportLoading.value = false
  }
}

/** 过滤出数量核算科目树 */
function filterQuantitySubjects(subjects: FmsSubjectVO[]): FmsSubjectVO[] {
  return subjects.flatMap((subject) => {
    const children = filterQuantitySubjects(subject.children || [])
    return subject.quantityAccounting ? [{ ...subject, children }] : children
  })
}

/** 初始化 */
onMounted(() => {
  init()
})
</script>
