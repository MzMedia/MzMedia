<template>
  <doc-alert title="【报表】财务报表" url="https://doc.iocoder.cn/fms/report/" />
  <!-- 工具栏 -->
  <ContentWrap>
    <FmsReportPeriodBar @query="handleQuery">
      <FmsReportPrintButton
        v-hasPermi="['fms:report:balance-sheet:print']"
        :disabled="!queryParams.endMonth"
        :period-label="periodLabel"
        target="fms-balance-sheet-table"
        title="资产负债表"
      />
      <el-button
        v-hasPermi="['fms:report:balance-sheet:export']"
        :disabled="!queryParams.endMonth"
        :loading="exportLoading"
        type="success"
        plain
        @click="handleExport"
      >
        <Icon icon="ep:download" class="mr-5px" />
        导出
      </el-button>
    </FmsReportPeriodBar>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <FmsReportCheckAlert :result="checkResult" :report-type="FMS_REPORT_TYPE.BALANCE_SHEET" />
    <el-table
      id="fms-balance-sheet-table"
      v-loading="loading"
      :data="list"
      border
      height="calc(100vh - 250px)"
    >
      <el-table-column label="资产" min-width="210">
        <template #default="scope">
          <div class="flex items-center gap-4px">
            <span
              :class="
                itemClass(scope.row.assetLevel, scope.row.assetEditable, scope.row.assetRowNo)
              "
            >
              {{ scope.row.assetName }}
            </span>
            <el-button
              v-if="scope.row.assetEditable && fmsStore.isAccountSetWritable"
              v-hasPermi="['fms:report:balance-sheet:update']"
              link
              type="primary"
              title="编辑公式"
              @click="openFormula(scope.row, true)"
            >
              <Icon icon="ep:edit-pen" />
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="行次" width="64" align="center">
        <template #default="scope">{{ scope.row.assetRowNo || '' }}</template>
      </el-table-column>
      <el-table-column label="期末余额" width="140" align="right">
        <template #default="scope">{{ formatMoney(scope.row.assetClosingAmount) }}</template>
      </el-table-column>
      <el-table-column label="年初余额" width="140" align="right">
        <template #default="scope">{{ formatMoney(scope.row.assetOpeningAmount) }}</template>
      </el-table-column>
      <el-table-column label="负债和所有者权益" min-width="250">
        <template #default="scope">
          <div class="flex items-center gap-4px">
            <span
              :class="
                itemClass(
                  scope.row.liabilityLevel,
                  scope.row.liabilityEditable,
                  scope.row.liabilityRowNo
                )
              "
            >
              {{ scope.row.liabilityName }}
            </span>
            <el-button
              v-if="scope.row.liabilityEditable && fmsStore.isAccountSetWritable"
              v-hasPermi="['fms:report:balance-sheet:update']"
              link
              type="primary"
              title="编辑公式"
              @click="openFormula(scope.row, false)"
            >
              <Icon icon="ep:edit-pen" />
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="行次" width="64" align="center">
        <template #default="scope">{{ scope.row.liabilityRowNo || '' }}</template>
      </el-table-column>
      <el-table-column label="期末余额" width="140" align="right">
        <template #default="scope">{{ formatMoney(scope.row.liabilityClosingAmount) }}</template>
      </el-table-column>
      <el-table-column label="年初余额" width="140" align="right">
        <template #default="scope">{{ formatMoney(scope.row.liabilityOpeningAmount) }}</template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 公式编辑弹窗 -->
  <FmsReportFormulaForm ref="formulaFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { FmsBalanceSheetApi } from '@/api/fms/report/balanceSheet'
import type { FmsBalanceSheetCheckVO, FmsBalanceSheetRowVO } from '@/api/fms/report/balanceSheet'
import type { FmsReportItemVO, FmsReportListReqVO } from '@/api/fms/report'
import FmsReportFormulaForm from '@/views/fms/report/components/FmsReportFormulaForm.vue'
import FmsReportCheckAlert from '@/views/fms/report/components/FmsReportCheckAlert.vue'
import FmsReportPeriodBar from '@/views/fms/report/components/FmsReportPeriodBar.vue'
import FmsReportPrintButton from '@/views/fms/report/components/FmsReportPrintButton.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { formatMoney } from '@/views/fms/utils/format'
import { FMS_REPORT_TYPE } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsBalanceSheet' })

const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const list = ref<FmsBalanceSheetRowVO[]>([]) // 资产负债表的行列表
const checkResult = ref<FmsBalanceSheetCheckVO>() // 检查结果
const periodLabel = ref('') // 会计期间文本，用于导出文件名
const queryParams = reactive<FmsReportListReqVO>({
  accountSetId: 0,
  startMonth: '',
  endMonth: ''
})
const formulaFormRef = ref<InstanceType<typeof FmsReportFormulaForm>>() // 公式编辑弹窗

watch(accountSetId, () => getList())

/** 工具栏查询 */
async function handleQuery(value: { startMonth: string; endMonth: string; label: string }) {
  periodLabel.value = value.label
  Object.assign(queryParams, value, { accountSetId: accountSetId.value })
  await getList()
}

/** 查询列表和检查结果 */
async function getList() {
  if (!accountSetId.value || !queryParams.endMonth) {
    list.value = []
    checkResult.value = undefined
    return
  }
  loading.value = true
  try {
    queryParams.accountSetId = accountSetId.value
    list.value = await FmsBalanceSheetApi.getBalanceSheet(queryParams)
    checkResult.value = await FmsBalanceSheetApi.checkBalanceSheet(queryParams)
  } finally {
    loading.value = false
  }
}

/** 导出 Excel */
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsBalanceSheetApi.exportBalanceSheet(queryParams)
    download.excel(data, `资产负债表-${periodLabel.value}.xls`)
  } finally {
    exportLoading.value = false
  }
}

/** 项目名称样式：按层级缩进，汇总项加粗 */
function itemClass(level?: number, editable?: boolean, rowNo?: number) {
  return [{ 'pl-16px': level === 2, 'pl-32px': level === 3, 'font-600': !editable && rowNo }]
}

/** 打开公式编辑弹窗 */
function openFormula(row: FmsBalanceSheetRowVO, asset: boolean) {
  const item = {
    id: asset ? row.assetId : row.liabilityId,
    name: asset ? row.assetName : row.liabilityName,
    formula: asset ? row.assetFormula : row.liabilityFormula
  } as FmsReportItemVO
  formulaFormRef.value?.open(item, 'balance')
}
</script>
