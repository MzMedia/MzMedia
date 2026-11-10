<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <FmsLedgerSearchBar
      :end-month="queryParams.endMonth"
      :export-loading="exportLoading"
      permission-prefix="fms:ledger:multi-column"
      print-target="fms-multi-column-ledger-table"
      print-title="多栏账"
      :subjects="multiColumnSubjects"
      :show-subject="true"
      :auto-query="true"
      :start-month="queryParams.startMonth"
      :subject-id="queryParams.subjectId"
      @export="handleExport"
      @search="handleQuery"
    />
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-multi-column-ledger-table"
      v-loading="loading"
      :data="result.rows"
      :row-class-name="getRowClassName"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column align="center" label="日期" prop="accountDate" width="110" fixed="left" />
      <el-table-column align="center" label="凭证字号" width="110" fixed="left">
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
      <el-table-column label="摘要" min-width="160" prop="digest" fixed="left" />
      <el-table-column align="right" label="借方" width="125">
        <template #default="scope">{{ formatMoney(scope.row.debitAmount) }}</template>
      </el-table-column>
      <el-table-column align="right" label="贷方" width="125">
        <template #default="scope">{{ formatMoney(scope.row.creditAmount) }}</template>
      </el-table-column>
      <el-table-column align="center" label="方向" prop="balanceDirection" width="70" />
      <el-table-column align="right" label="余额" width="130">
        <template #default="scope">{{ formatMoney(scope.row.balance) }}</template>
      </el-table-column>
      <el-table-column v-if="debitColumns.length" align="center" label="借方">
        <el-table-column
          v-for="column in debitColumns"
          :key="column.subjectId"
          align="right"
          :label="`${column.subjectCode}/${column.subjectName}`"
          min-width="145"
        >
          <template #default="scope">
            {{ formatMoney(scope.row.columnAmounts?.[column.subjectId]) }}
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column v-if="creditColumns.length" align="center" label="贷方">
        <el-table-column
          v-for="column in creditColumns"
          :key="column.subjectId"
          align="right"
          :label="`${column.subjectCode}/${column.subjectName}`"
          min-width="145"
        >
          <template #default="scope">
            {{ formatMoney(scope.row.columnAmounts?.[column.subjectId]) }}
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsMultiColumnVO } from '@/api/fms/ledger'
import type { FmsLedgerDetailVO, FmsLedgerListReqVO } from '@/api/fms/ledger/types'
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { handleTree, treeToList } from '@/utils/tree'
import FmsLedgerSearchBar from '@/views/fms/ledger/components/FmsLedgerSearchBar.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsMultiColumnLedger' })

const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const subjects = ref<FmsSubjectVO[]>([]) // 会计科目树
const multiColumnSubjects = computed(() => filterParentSubjects(subjects.value)) // 含下级科目的科目树
const result = reactive<FmsMultiColumnVO>({ columns: [], rows: [] }) // 多栏账查询结果
const debitColumns = computed(() =>
  result.columns.filter((column) => column.balanceDirection === 1)
) // 借方专栏列表
const creditColumns = computed(() =>
  result.columns.filter((column) => column.balanceDirection === 2)
) // 贷方专栏列表
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const queryParams = reactive<FmsLedgerListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth
})

watch(accountSetId, () => init())

/** 初始化多栏账页面 */
async function init() {
  if (!accountSetId.value) {
    subjects.value = []
    result.columns = []
    result.rows = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const [subjectList, accountingMonth] = await Promise.all([
    FmsSubjectApi.getSubjectSimpleList(accountSetId.value),
    fmsStore.loadCurrentMonth()
  ])
  subjects.value = handleTree(subjectList)
  if (accountingMonth) {
    queryParams.startMonth = accountingMonth
    queryParams.endMonth = accountingMonth
  }
  queryParams.subjectId = treeToList<FmsSubjectVO[]>(multiColumnSubjects.value)[0]?.id
  await getList()
}

/** 查询多栏账 */
async function getList() {
  if (!accountSetId.value || !queryParams.subjectId) return
  loading.value = true
  try {
    Object.assign(result, await FmsLedgerApi.getMultiColumn(queryParams))
  } finally {
    loading.value = false
  }
}

/** 处理查询条件变化 */
function handleQuery(value: Omit<FmsLedgerListReqVO, 'accountSetId'>) {
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  getList()
}

/** 过滤出含下级科目的科目树 */
function filterParentSubjects(items: FmsSubjectVO[]): FmsSubjectVO[] {
  return items.flatMap((item) =>
    item.children?.length ? [{ ...item, children: filterParentSubjects(item.children) }] : []
  )
}

/** 获得汇总行样式类名 */
function getRowClassName({ row }: { row: FmsLedgerDetailVO }) {
  return row.rowType === 2 ? '' : '[--el-table-tr-bg-color:var(--el-fill-color-light)] font-600'
}

/** 打开凭证详情 */
function openVoucher(row: FmsLedgerDetailVO) {
  router.push({ path: '/fms/voucher/create', query: { id: row.voucherId } })
}

/** 导出多栏账 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportMultiColumn(queryParams)
    download.excel(
      data,
      buildPeriodFilename('多栏账', queryParams.startMonth, queryParams.endMonth)
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
