<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <FmsLedgerSearchBar
      :end-month="queryParams.endMonth"
      :export-loading="exportLoading"
      permission-prefix="fms:ledger:subject-balance"
      :max-level="FMS_SUBJECT_LEVEL_MAX"
      :before-print="expandBalanceForPrint"
      print-target="fms-subject-balance-table"
      print-title="科目余额表"
      :start-month="queryParams.startMonth"
      @export="handleExport"
      @search="handleQuery"
    >
      <template #actions>
        <el-button type="danger" plain @click="toggleExpandAll">
          <Icon icon="ep:sort" class="mr-5px" /> 展开/折叠
        </el-button>
      </template>
    </FmsLedgerSearchBar>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-subject-balance-table"
      v-if="refreshTable"
      v-loading="loading"
      :data="list"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children' }"
      row-key="nodeKey"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column label="科目编码" prop="subjectCode" min-width="125">
        <template #default="scope">
          <el-button
            v-if="scope.row.nodeType === FMS_SUBJECT_BALANCE_NODE_TYPE.SUBJECT"
            link
            type="primary"
            @click="openDetail(scope.row)"
          >
            {{ scope.row.subjectCode }}
          </el-button>
          <span v-else>{{ scope.row.subjectCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="科目名称" prop="subjectName" min-width="150" />
      <el-table-column align="center" label="期初余额">
        <el-table-column align="right" label="借方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.openingDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.openingCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本期发生额">
        <el-table-column align="right" label="借方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.periodDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.periodCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本年累计发生额">
        <el-table-column align="right" label="借方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.yearDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.yearCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="期末余额">
        <el-table-column align="right" label="借方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.endingDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" width="125">
          <template #default="scope">{{ formatMoney(scope.row.endingCreditAmount) }}</template>
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
import FmsLedgerSearchBar from '@/views/fms/ledger/components/FmsLedgerSearchBar.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_SUBJECT_BALANCE_NODE_TYPE, FMS_SUBJECT_LEVEL_MAX } from '@/views/fms/utils/constants'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsSubjectBalance' })

const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const isExpandAll = ref(false) // 是否展开所有级次
const refreshTable = ref(true) // 是否渲染表格，用于强制刷新展开状态
const list = ref<FmsSubjectBalanceVO[]>([]) // 科目余额列表
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const queryParams = reactive<FmsLedgerListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth,
  minLevel: 1,
  maxLevel: FMS_SUBJECT_LEVEL_MAX
})

watch(accountSetId, () => init())

/** 初始化科目余额表页面 */
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

/** 查询科目余额列表 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    list.value = await FmsLedgerApi.getSubjectBalanceList(queryParams)
    // 查询结果变更后，重新渲染树并重置展开状态，避免旧状态与新数据不一致
    isExpandAll.value = false
    refreshTable.value = false
    await nextTick()
    refreshTable.value = true
  } finally {
    loading.value = false
  }
}

/** 处理查询条件变化 */
function handleQuery(value: Omit<FmsLedgerListReqVO, 'accountSetId'>) {
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  getList()
}

/** 展开或折叠全部科目 */
async function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  await nextTick()
  refreshTable.value = true
  await nextTick()
}

/** 打印前展开所有级次 */
async function expandBalanceForPrint() {
  if (isExpandAll.value) return
  await toggleExpandAll()
}

/** 打开科目明细账 */
function openDetail(row: FmsSubjectBalanceVO) {
  router.push({
    path: '/fms/ledger/detail',
    query: {
      subjectId: row.subjectId,
      startMonth: queryParams.startMonth,
      endMonth: queryParams.endMonth
    }
  })
}

/** 导出科目余额表 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportSubjectBalance(queryParams)
    download.excel(
      data,
      buildPeriodFilename('科目余额表', queryParams.startMonth, queryParams.endMonth)
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
