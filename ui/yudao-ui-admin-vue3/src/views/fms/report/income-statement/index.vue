<template>
  <doc-alert title="【报表】财务报表" url="https://doc.iocoder.cn/fms/report/" />
  <!-- 工具栏 -->
  <ContentWrap>
    <FmsReportPeriodBar @query="handleQuery">
      <FmsReportPrintButton
        v-hasPermi="['fms:report:income-statement:print']"
        :disabled="!queryParams.endMonth"
        :period-label="periodLabel"
        target="fms-income-statement-table"
        title="利润表"
      />
      <el-button
        v-hasPermi="['fms:report:income-statement:export']"
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
    <FmsReportCheckAlert :result="checkResult" :report-type="FMS_REPORT_TYPE.INCOME_STATEMENT" />
    <el-table
      id="fms-income-statement-table"
      v-loading="loading"
      :data="list"
      border
      height="calc(100vh - 220px)"
    >
      <el-table-column label="项目" min-width="420">
        <template #default="scope">
          <div class="flex items-center gap-4px">
            <span :class="itemClass(scope.row)">{{ scope.row.name }}</span>
            <el-button
              v-if="scope.row.editable && fmsStore.isAccountSetWritable"
              v-hasPermi="['fms:report:income-statement:update']"
              link
              type="primary"
              title="编辑公式"
              @click="openFormula(scope.row)"
            >
              <Icon icon="ep:edit-pen" />
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="行次" width="90" prop="rowNo" align="center" />
      <el-table-column label="本年累计金额" min-width="180" align="right">
        <template #default="scope">{{ formatMoney(scope.row.yearAmount) }}</template>
      </el-table-column>
      <el-table-column label="本期金额" min-width="180" align="right">
        <template #default="scope">{{ formatMoney(scope.row.currentAmount) }}</template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 公式编辑弹窗 -->
  <FmsReportFormulaForm ref="formulaFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { FmsIncomeStatementApi } from '@/api/fms/report/incomeStatement'
import type { FmsIncomeStatementCheckVO } from '@/api/fms/report/incomeStatement'
import type { FmsReportItemVO, FmsReportListReqVO } from '@/api/fms/report'
import FmsReportFormulaForm from '@/views/fms/report/components/FmsReportFormulaForm.vue'
import FmsReportCheckAlert from '@/views/fms/report/components/FmsReportCheckAlert.vue'
import FmsReportPeriodBar from '@/views/fms/report/components/FmsReportPeriodBar.vue'
import FmsReportPrintButton from '@/views/fms/report/components/FmsReportPrintButton.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { formatMoney } from '@/views/fms/utils/format'
import { FMS_REPORT_TYPE } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsIncomeStatement' })

const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const loading = ref(false) // 列表的加载中
const exportLoading = ref(false) // 导出的加载中
const list = ref<FmsReportItemVO[]>([]) // 利润表的项目列表
const checkResult = ref<FmsIncomeStatementCheckVO>() // 检查结果
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
    list.value = await FmsIncomeStatementApi.getIncomeStatement(queryParams)
    checkResult.value = await FmsIncomeStatementApi.checkIncomeStatement(queryParams)
  } finally {
    loading.value = false
  }
}

/** 导出 Excel */
async function handleExport() {
  exportLoading.value = true
  try {
    const data = await FmsIncomeStatementApi.exportIncomeStatement(queryParams)
    download.excel(data, `利润表-${periodLabel.value}.xls`)
  } finally {
    exportLoading.value = false
  }
}

/** 项目名称样式：按层级缩进，汇总项加粗 */
function itemClass(item: FmsReportItemVO) {
  return [{ 'pl-20px': item.level === 2, 'pl-40px': item.level === 3, 'font-600': !item.editable }]
}

/** 打开公式编辑弹窗 */
function openFormula(item: FmsReportItemVO) {
  formulaFormRef.value?.open(item, 'income')
}
</script>
