<template>
  <doc-alert title="【账簿】账簿管理" url="https://doc.iocoder.cn/fms/ledger/" />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form class="-mb-15px" :inline="true" label-width="68px">
      <el-form-item label="会计期间">
        <FmsLedgerMonthRangePicker v-model="monthRange" />
      </el-form-item>
      <el-form-item label="辅助类">
        <FmsAuxiliaryTypeSelect
          v-model="queryParams.auxiliaryTypeId"
          :clearable="false"
          class="!w-240px"
          @change="handleTypeChange"
          @loaded="handleTypeLoaded"
        />
      </el-form-item>
      <el-form-item label="科目">
        <FmsSubjectSelect
          v-model="queryParams.subjectId"
          clearable
          placeholder="全部科目"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <FmsLedgerPrintButton
          :center-text="selectedItemPrintText"
          :end-month="monthRange[1]"
          permission-prefix="fms:ledger:detail"
          :start-month="monthRange[0]"
          target="fms-auxiliary-detail-ledger-table"
          title="核算项目明细账"
        />
        <el-button
          v-hasPermi="['fms:ledger:detail:export']"
          :loading="exportLoading"
          class="!ml-12px"
          type="success"
          plain
          @click="handleExport"
        >
          <Icon icon="ep:download" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 核算项目和明细账列表 -->
  <ContentWrap>
    <div v-if="selectedItem" class="pb-12px font-600">
      {{ selectedType?.name }}：{{ selectedItem.code }}_{{ selectedItem.name }}
    </div>
    <div class="flex items-stretch gap-12px">
      <el-table
        id="fms-auxiliary-detail-ledger-table"
        v-loading="loading"
        :data="list"
        :row-class-name="getRowClassName"
        border
        stripe
        height="calc(100vh - 300px)"
        class="flex-1"
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
        <el-table-column label="摘要" min-width="180" prop="digest" />
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
      <div
        v-if="!quickPanelCollapsed"
        class="h-[calc(100vh-300px)] flex-[0_0_240px] border-l border-l-[var(--el-border-color-lighter)] border-l-solid pl-14px"
      >
        <div class="mb-8px flex items-center justify-between">
          <span class="text-14px font-600">快捷项目</span>
          <el-tooltip content="收起快捷项目" placement="top">
            <el-button class="!h-28px !w-28px" circle plain @click="quickPanelCollapsed = true">
              <Icon icon="ep:arrow-right" />
            </el-button>
          </el-tooltip>
        </div>
        <el-input v-model="itemKeyword" clearable placeholder="搜索辅助项目" prefix-icon="Search" />
        <div class="mt-12px h-[calc(100%-80px)] overflow-auto">
          <button
            v-for="item in filteredItems"
            :key="item.id"
            type="button"
            class="flex w-full cursor-pointer flex-col gap-3px rounded-4px border-0 bg-transparent px-10px py-9px text-left text-[var(--el-text-color-regular)] hover:bg-[var(--el-color-primary-light-9)] hover:text-[var(--el-color-primary)]"
            :class="{
              '!bg-[var(--el-color-primary-light-9)] !text-[var(--el-color-primary)]':
                item.id === queryParams.auxiliaryItemId
            }"
            @click="handleItemClick(item.id)"
          >
            <span>{{ item.code }}</span>
            <span>{{ item.name }}</span>
          </button>
          <el-empty v-if="!filteredItems.length" description="暂无辅助项目" :image-size="64" />
        </div>
      </div>
      <div
        v-else
        class="h-[calc(100vh-300px)] flex-[0_0_36px] border-l border-l-[var(--el-border-color-lighter)] border-l-solid pl-8px"
      >
        <el-tooltip content="展开快捷项目" placement="top">
          <el-button class="!h-28px !w-28px" circle plain @click="quickPanelCollapsed = false">
            <Icon icon="ep:arrow-left" />
          </el-button>
        </el-tooltip>
      </div>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import { FmsAuxiliaryItemApi } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryItemOptionVO } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsLedgerAuxiliaryListReqVO, FmsLedgerDetailVO } from '@/api/fms/ledger/types'
import FmsAuxiliaryTypeSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryTypeSelect.vue'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsLedgerMonthRangePicker from '@/views/fms/ledger/components/FmsLedgerMonthRangePicker.vue'
import FmsLedgerPrintButton from '@/views/fms/ledger/components/FmsLedgerPrintButton.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsAuxiliaryDetailLedger' })

const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const list = ref<FmsLedgerDetailVO[]>([]) // 核算项目明细账列表
const types = ref<FmsAuxiliaryTypeVO[]>([]) // 辅助核算类别列表
const items = ref<FmsAuxiliaryItemOptionVO[]>([]) // 辅助核算项目列表
const dataItemIds = ref<number[]>([]) // 有发生额的辅助项目编号
const itemKeyword = ref('') // 辅助项目搜索关键字
const quickPanelCollapsed = ref(false) // 是否收起右侧快捷项目栏
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const monthRange = ref<string[]>([currentMonth, currentMonth]) // 会计期间范围
const queryParams = reactive<FmsLedgerAuxiliaryListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth,
  auxiliaryTypeId: 0,
  auxiliaryItemId: 0
})
const selectedType = computed(() =>
  types.value.find((type) => type.id === queryParams.auxiliaryTypeId)
) // 选中的辅助核算类别
const selectedItem = computed(() =>
  items.value.find((item) => item.id === queryParams.auxiliaryItemId)
) // 选中的辅助核算项目
const selectedItemPrintText = computed(() =>
  selectedItem.value
    ? `辅助项目：${selectedType.value?.name} ${selectedItem.value.code}_${selectedItem.value.name}`
    : ''
) // 选中辅助项目的打印文本
const filteredItems = computed(() => {
  const keyword = itemKeyword.value.toLowerCase()
  return items.value
    .filter((item) => dataItemIds.value.includes(item.id))
    .filter((item) => !keyword || `${item.code} ${item.name}`.toLowerCase().includes(keyword))
}) // 过滤后的辅助项目列表

watch(accountSetId, () => init())

/** 初始化核算项目明细账页面 */
async function init() {
  if (!accountSetId.value) {
    list.value = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const accountingMonth = await fmsStore.loadCurrentMonth()
  if (accountingMonth) {
    monthRange.value = [accountingMonth, accountingMonth]
    queryParams.startMonth = accountingMonth
    queryParams.endMonth = accountingMonth
  }
  if (queryParams.auxiliaryTypeId) {
    await loadItems()
  }
}

/** 处理辅助核算类别列表加载完成 */
function handleTypeLoaded(typeList: FmsAuxiliaryTypeVO[]) {
  types.value = typeList
  if (!types.value.some((type) => type.id === queryParams.auxiliaryTypeId)) {
    queryParams.auxiliaryTypeId = types.value[0]?.id || 0
  }
  loadItems()
}

/** 加载辅助项目并查询有发生额的项目 */
async function loadItems() {
  list.value = []
  items.value = []
  dataItemIds.value = []
  queryParams.auxiliaryItemId = 0
  if (!accountSetId.value || !queryParams.auxiliaryTypeId) return
  items.value = await FmsAuxiliaryItemApi.getAuxiliaryItemSimpleList(
    accountSetId.value,
    queryParams.auxiliaryTypeId
  )
  const balances = await FmsLedgerApi.getAuxiliaryBalanceList({
    accountSetId: accountSetId.value,
    startMonth: monthRange.value[0],
    endMonth: monthRange.value[1],
    auxiliaryTypeId: queryParams.auxiliaryTypeId,
    subjectId: queryParams.subjectId
  })
  // 右侧快捷项目只展示查询期间内实际发生过的项目；期初或历史余额不作为本期候选
  dataItemIds.value = balances
    .filter(
      (item) =>
        Number(item.periodDebitAmount || 0) !== 0 || Number(item.periodCreditAmount || 0) !== 0
    )
    .map((item) => item.auxiliaryItemId)
  queryParams.auxiliaryItemId = dataItemIds.value[0] || 0
  await getList()
}

/** 处理辅助类别变化 */
async function handleTypeChange() {
  itemKeyword.value = ''
  await loadItems()
}

/** 搜索按钮操作 */
function handleQuery() {
  loadItems()
}

/** 重置按钮操作 */
async function resetQuery() {
  const accountingMonth = await fmsStore.loadCurrentMonth()
  monthRange.value = [accountingMonth || currentMonth, accountingMonth || currentMonth]
  queryParams.auxiliaryTypeId = types.value[0]?.id || 0
  queryParams.subjectId = undefined
  itemKeyword.value = ''
  await loadItems()
}

/** 查询核算项目明细账 */
async function getList() {
  if (!accountSetId.value || !queryParams.auxiliaryItemId || monthRange.value.length !== 2) {
    list.value = []
    return
  }
  Object.assign(queryParams, {
    accountSetId: accountSetId.value,
    startMonth: monthRange.value[0],
    endMonth: monthRange.value[1]
  })
  loading.value = true
  try {
    list.value = await FmsLedgerApi.getAuxiliaryDetailList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 处理辅助项目点击 */
function handleItemClick(auxiliaryItemId: number) {
  queryParams.auxiliaryItemId = auxiliaryItemId
  getList()
}

/** 获得汇总行样式类名 */
function getRowClassName({ row }: { row: FmsLedgerDetailVO }) {
  return row.rowType === 2 ? '' : '[--el-table-tr-bg-color:var(--el-fill-color-light)] font-600'
}

/** 打开凭证详情 */
function openVoucher(row: FmsLedgerDetailVO) {
  router.push({ path: '/fms/voucher/create', query: { id: row.voucherId } })
}

/** 导出核算项目明细账 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  if (!queryParams.auxiliaryItemId) return
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportAuxiliaryDetail(queryParams)
    download.excel(
      data,
      buildPeriodFilename('核算项目明细账', queryParams.startMonth, queryParams.endMonth)
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
