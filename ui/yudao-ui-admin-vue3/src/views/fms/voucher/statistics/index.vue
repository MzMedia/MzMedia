<template>
  <doc-alert title="【凭证】凭证管理" url="https://doc.iocoder.cn/fms/voucher/" />
  <!-- 搜索 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="会计期间">
        <el-date-picker
          v-model="monthRange"
          :clearable="false"
          class="!w-240px"
          end-placeholder="结束月份"
          start-placeholder="开始月份"
          type="monthrange"
          value-format="YYYY-MM"
        />
      </el-form-item>
      <el-form-item label="凭证字" prop="voucherWordId">
        <FmsVoucherWordSelect
          v-model="queryParams.voucherWordId"
          :options="voucherWords"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="凭证号">
        <div class="flex !w-240px items-center gap-8px [&_.el-input-number]:!w-105px">
          <el-input-number
            v-model="queryParams.minVoucherNumber"
            :controls="false"
            :min="1"
            placeholder="起始号"
          />
          <span>至</span>
          <el-input-number
            v-model="queryParams.maxVoucherNumber"
            :controls="false"
            :min="1"
            placeholder="结束号"
          />
        </div>
      </el-form-item>
      <el-form-item label="科目级次">
        <div class="flex !w-240px items-center gap-8px [&_.el-input-number]:!w-105px">
          <el-input-number v-model="queryParams.minLevel" :controls="false" :min="1" :max="10" />
          <span>至</span>
          <el-input-number v-model="queryParams.maxLevel" :controls="false" :min="1" :max="10" />
        </div>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="router.push('/fms/voucher/create')"
          v-hasPermi="['fms:voucher:create']"
          v-if="fmsStore.isAccountSetWritable"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button type="primary" plain @click="handlePrint" v-hasPermi="['fms:voucher:print']">
          <Icon icon="ep:printer" class="mr-5px" /> 打印
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          @click="handleExport"
          v-hasPermi="['fms:voucher:statistics:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap class="[&_.el-table__footer-wrapper_td]:font-600">
    <el-table
      id="voucher-statistics-table"
      v-loading="loading"
      :data="list"
      :summary-method="getSummaries"
      border
      show-summary
      stripe
    >
      <el-table-column label="科目编码" min-width="160" prop="subjectCode">
        <template #default="scope">
          <el-button
            v-if="checkPermi(['fms:ledger:detail:query'])"
            link
            type="primary"
            @click="openDetail(scope.row)"
          >
            {{ scope.row.subjectCode }}
          </el-button>
          <span v-else>{{ scope.row.subjectCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="科目名称" min-width="220" prop="subjectName" />
      <el-table-column align="right" label="借方金额" min-width="180" prop="debitAmount">
        <template #default="scope">{{ formatMoney(scope.row.debitAmount) }}</template>
      </el-table-column>
      <el-table-column align="right" label="贷方金额" min-width="180" prop="creditAmount">
        <template #default="scope">{{ formatMoney(scope.row.creditAmount) }}</template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 打印预览 -->
  <FmsPrintPreview ref="printPreviewRef" />
</template>

<script lang="ts" setup>
import type { FormInstance, TableColumnCtx } from 'element-plus'
import { FmsVoucherWordApi } from '@/api/fms/config/voucher-word'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import { FmsVoucherStatisticsApi } from '@/api/fms/voucher'
import type { FmsVoucherStatisticsReqVO, FmsVoucherStatisticsVO } from '@/api/fms/voucher'
import { checkPermi } from '@/utils/permission'
import { useFmsStore } from '@/views/fms/store/fms'
import download from '@/utils/download'
import { formatDate } from '@/utils/formatTime'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import FmsPrintPreview from '@/views/fms/components/print/FmsPrintPreview.vue'
import { buildPeriodFilename, formatMoney, formatPeriodLabel } from '@/views/fms/utils/format'
import { buildFmsTablePrintHtml } from '@/views/fms/utils/print'

defineOptions({ name: 'FmsVoucherStatistics' })

const message = useMessage() // 消息弹窗
const router = useRouter()
const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号

const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const loading = ref(false) // 列表的加载中
const monthRange = ref<string[]>([currentMonth, currentMonth]) // 凭证期间范围
const voucherWords = ref<FmsVoucherWordVO[]>([]) // 凭证字列表
const list = ref<FmsVoucherStatisticsVO[]>([]) // 凭证汇总列表
const queryParams = reactive<FmsVoucherStatisticsReqVO>(createDefaultQuery()) // 查询参数
const queryFormRef = ref<FormInstance>() // 搜索的表单 Ref
const printPreviewRef = ref<InstanceType<typeof FmsPrintPreview>>() // 打印预览 Ref

watch(accountSetId, () => init())

/** 初始化凭证汇总页面 */
async function init() {
  if (!accountSetId.value) {
    voucherWords.value = []
    list.value = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const [wordList, accountingMonth] = await Promise.all([
    FmsVoucherWordApi.getVoucherWordSimpleList(accountSetId.value),
    fmsStore.loadCurrentMonth()
  ])
  voucherWords.value = wordList
  if (accountingMonth) {
    monthRange.value = [accountingMonth, accountingMonth]
    Object.assign(queryParams, createDefaultQuery(accountSetId.value, accountingMonth))
  }
  await getList()
}

/** 查询凭证汇总列表 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    list.value = await FmsVoucherStatisticsApi.getVoucherStatisticsList(queryParams)
  } finally {
    loading.value = false
  }
}

/** 搜索凭证汇总 */
function handleQuery() {
  if (monthRange.value.length !== 2) return
  queryParams.startMonth = monthRange.value[0]
  queryParams.endMonth = monthRange.value[1]
  getList()
}

/** 重置搜索条件 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  const accountingMonth = fmsStore.getCurrentMonth || currentMonth
  monthRange.value = [accountingMonth, accountingMonth]
  Object.assign(queryParams, createDefaultQuery(accountSetId.value || 0, accountingMonth))
  getList()
}

/** 打开科目明细账 */
function openDetail(row: FmsVoucherStatisticsVO) {
  if (!checkPermi(['fms:ledger:detail:query'])) return
  router.push({
    path: '/fms/ledger/detail',
    query: {
      subjectId: row.subjectId,
      startMonth: queryParams.startMonth,
      endMonth: queryParams.endMonth
    }
  })
}

/** 导出凭证汇总表 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await FmsVoucherStatisticsApi.exportVoucherStatistics(queryParams)
    download.excel(
      data,
      buildPeriodFilename('凭证汇总表', queryParams.startMonth, queryParams.endMonth)
    )
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 打印凭证汇总表 */
function handlePrint() {
  const tableElement = document.getElementById('voucher-statistics-table')
  if (!tableElement) {
    message.error('未找到可打印的表格')
    return
  }
  printPreviewRef.value?.printHtml(
    buildFmsTablePrintHtml({
      title: '凭证汇总表',
      companyName: fmsStore.getAccountSet?.companyName || '',
      periodLabel: formatPeriodLabel(queryParams.startMonth, queryParams.endMonth),
      tableElement
    })
  )
}

/** 计算凭证汇总合计行 */
function getSummaries({
  columns,
  data
}: {
  columns: TableColumnCtx<FmsVoucherStatisticsVO>[]
  data: FmsVoucherStatisticsVO[]
}) {
  const totalRows = data.filter((item) => item.level === queryParams.minLevel)
  return columns.map((column, index) => {
    if (index === 0) return '总计'
    if (index === 1) return ''
    const amount = totalRows.reduce(
      (sum, item) => sum + Number(item[column.property as 'debitAmount' | 'creditAmount'] || 0),
      0
    )
    return formatMoney(amount)
  })
}

/** 创建默认查询参数 */
function createDefaultQuery(
  accountId = 0,
  accountingMonth = currentMonth
): FmsVoucherStatisticsReqVO {
  return {
    accountSetId: accountId,
    startMonth: accountingMonth,
    endMonth: accountingMonth,
    minLevel: 1,
    maxLevel: 1
  }
}

/** 初始化 */
onMounted(() => {
  init()
})
</script>
