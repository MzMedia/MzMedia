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
          class="!w-240px"
          :clearable="false"
          @change="handleTypeChange"
          @loaded="handleTypeLoaded"
        />
      </el-form-item>
      <el-form-item label="辅助项目">
        <FmsAuxiliaryItemSelect
          v-model="queryParams.auxiliaryItemId"
          :auxiliary-type-id="queryParams.auxiliaryTypeId"
          clearable
          placeholder="全部辅助项目"
          class="!w-240px"
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
          :end-month="monthRange[1]"
          permission-prefix="fms:ledger:subject-balance"
          :start-month="monthRange[0]"
          target="fms-auxiliary-balance-table"
          title="核算项目余额表"
        />
        <el-button
          v-hasPermi="['fms:ledger:subject-balance:export']"
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

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      id="fms-auxiliary-balance-table"
      v-loading="loading"
      :data="list"
      border
      stripe
      height="calc(100vh - 245px)"
    >
      <el-table-column align="center" label="编码" min-width="120" prop="code" fixed="left" />
      <el-table-column label="项目名称" min-width="180" prop="name" fixed="left" />
      <el-table-column align="center" label="期初余额">
        <el-table-column align="right" label="借方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.openingDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.openingCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本期发生额">
        <el-table-column align="right" label="借方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.periodDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.periodCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="本年累计发生额">
        <el-table-column align="right" label="借方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.yearDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.yearCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="期末余额">
        <el-table-column align="right" label="借方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.endingDebitAmount) }}</template>
        </el-table-column>
        <el-table-column align="right" label="贷方" min-width="130">
          <template #default="scope">{{ formatMoney(scope.row.endingCreditAmount) }}</template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { FmsLedgerApi } from '@/api/fms/ledger'
import type { FmsLedgerAuxiliaryBalanceVO } from '@/api/fms/ledger'
import type { FmsLedgerAuxiliaryListReqVO } from '@/api/fms/ledger/types'
import FmsAuxiliaryItemSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryItemSelect.vue'
import FmsAuxiliaryTypeSelect from '@/views/fms/config/auxiliary/components/FmsAuxiliaryTypeSelect.vue'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsLedgerMonthRangePicker from '@/views/fms/ledger/components/FmsLedgerMonthRangePicker.vue'
import FmsLedgerPrintButton from '@/views/fms/ledger/components/FmsLedgerPrintButton.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { buildPeriodFilename, formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsAuxiliaryBalance' })

const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const list = ref<FmsLedgerAuxiliaryBalanceVO[]>([]) // 核算项目余额列表
const types = ref<FmsAuxiliaryTypeVO[]>([]) // 辅助核算类别列表
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const monthRange = ref<string[]>([currentMonth, currentMonth]) // 会计期间范围
const queryParams = reactive<FmsLedgerAuxiliaryListReqVO>({
  accountSetId: 0,
  startMonth: currentMonth,
  endMonth: currentMonth,
  auxiliaryTypeId: 0
})

watch(accountSetId, () => init())

/** 初始化核算项目余额表页面 */
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
    await getList()
  }
}

/** 处理辅助核算类别列表加载完成 */
function handleTypeLoaded(typeList: FmsAuxiliaryTypeVO[]) {
  types.value = typeList
  if (!types.value.some((type) => type.id === queryParams.auxiliaryTypeId)) {
    queryParams.auxiliaryTypeId = types.value[0]?.id || 0
    queryParams.auxiliaryItemId = undefined
  }
  getList()
}

/** 搜索按钮操作 */
function handleQuery() {
  getList()
}

/** 重置按钮操作 */
async function resetQuery() {
  const accountingMonth = await fmsStore.loadCurrentMonth()
  monthRange.value = [accountingMonth || currentMonth, accountingMonth || currentMonth]
  queryParams.auxiliaryTypeId = types.value[0]?.id || 0
  queryParams.auxiliaryItemId = undefined
  queryParams.subjectId = undefined
  await getList()
}

/** 处理辅助类别变化 */
async function handleTypeChange() {
  queryParams.auxiliaryItemId = undefined
  await getList()
}

/** 查询核算项目余额表 */
async function getList() {
  if (!accountSetId.value || !queryParams.auxiliaryTypeId || monthRange.value.length !== 2) return
  Object.assign(queryParams, {
    accountSetId: accountSetId.value,
    startMonth: monthRange.value[0],
    endMonth: monthRange.value[1]
  })
  loading.value = true
  try {
    list.value = await FmsLedgerApi.getAuxiliaryBalanceList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 导出核算项目余额表 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  if (!queryParams.auxiliaryTypeId) return
  exportLoading.value = true
  try {
    const data = await FmsLedgerApi.exportAuxiliaryBalance(queryParams)
    download.excel(
      data,
      buildPeriodFilename('核算项目余额表', queryParams.startMonth, queryParams.endMonth)
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
