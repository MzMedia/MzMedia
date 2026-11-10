<template>
  <doc-alert title="【结账】期末结账" url="https://doc.iocoder.cn/fms/closing/" />
  <!-- 会计期间 -->
  <ContentWrap>
    <el-form class="-mb-15px" :inline="true" label-width="68px">
      <el-form-item label="会计期间">
        <el-date-picker
          v-model="month"
          type="month"
          value-format="YYYY-MM"
          format="YYYY年MM月"
          :clearable="false"
          :disabled-date="disabledMonth"
          @change="getOverview"
        />
      </el-form-item>
      <el-form-item>
        <el-button :loading="loading" @click="getOverview">
          <Icon icon="ep:refresh" class="mr-5px" />刷新
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 期末结转方案 -->
  <ClosingSchemeList
    v-if="accountSetId"
    :account-set-id="accountSetId"
    :month="month"
    :current-period="isCurrentPeriod"
    :closed="overview.closed"
    :voucher-count="overview.voucherCount"
    :profit-loss-balance="overview.profitLossBalance"
    @success="getOverview"
  />

  <!-- 结账检查 -->
  <ContentWrap>
    <el-alert
      :title="overview.closed ? `${monthLabel} 已结账` : `${monthLabel} 尚未结账`"
      :type="overview.closed ? 'success' : 'info'"
      :closable="false"
      show-icon
      class="!mb-16px"
    />

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="凭证审核"
          :value="`${overview.pendingVoucherCount} 张待审核`"
          :tag-type="
            !overview.voucherReviewRequired || overview.pendingVoucherCount === 0
              ? 'success'
              : 'danger'
          "
          :tag-label="overview.voucherReviewRequired ? '结账前必须审核' : '当前未强制审核'"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="初始余额"
          :value="overview.initialBalanceBalanced ? '试算平衡' : '试算不平衡'"
          :tag-type="overview.initialBalanceBalanced ? 'success' : 'danger'"
          :tag-label="overview.initialBalanceBalanced ? '检查通过' : '需要处理'"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="凭证编号"
          :value="overview.voucherNumberContinuous ? '编号连续' : '存在断号'"
          :tag-type="overview.voucherNumberContinuous ? 'success' : 'danger'"
          :tag-label="overview.voucherNumberContinuous ? '检查通过' : '需要整理'"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="损益结转"
          :value="formatMoney(overview.profitLossBalance)"
          :tag-type="
            overview.profitLossVoucherGenerated && overview.profitLossBalance === 0
              ? 'success'
              : 'warning'
          "
          :tag-label="
            !overview.profitLossVoucherGenerated
              ? '未生成结转凭证'
              : overview.profitLossBalance === 0
                ? '已结平'
                : '待结转'
          "
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="利润表检查"
          :value="
            overview.incomeStatementUnmappedSubjectCount
              ? `${overview.incomeStatementUnmappedSubjectCount} 个科目未纳入公式`
              : overview.incomeStatementBalanced
                ? '勾稽平衡'
                : '勾稽不平衡'
          "
          :tag-type="
            overview.incomeStatementBalanced && overview.incomeStatementUnmappedSubjectCount === 0
              ? 'success'
              : 'danger'
          "
          :tag-label="
            overview.incomeStatementBalanced && overview.incomeStatementUnmappedSubjectCount === 0
              ? '检查通过'
              : '需要处理'
          "
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="资产负债平衡"
          :value="`差额 ${formatMoney(overview.balanceSheetDifference)}`"
          :tag-type="
            overview.balanceSheetProfitLossTransferred &&
            overview.balanceSheetBalanced &&
            overview.balanceSheetUnmappedSubjectCount === 0
              ? 'success'
              : 'danger'
          "
          :tag-label="balanceSheetCheckLabel"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <ClosingStatusCard
          title="期间状态"
          :value="overview.closed ? '已结账' : '未结账'"
          :tag-type="overview.closed ? 'success' : 'info'"
          :tag-label="overview.closed ? '账簿已锁定' : '允许继续记账'"
        />
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 执行结账 -->
  <ContentWrap title="执行结账">
    <el-space>
      <el-button
        v-if="fmsStore.isAccountSetWritable && !overview.closed && !isBeforeCurrentPeriod"
        type="primary"
        :loading="submitting"
        :disabled="!canClose"
        @click="closeToPeriod"
        v-hasPermi="['fms:closing:close']"
      >
        {{ isCurrentPeriod ? '结账' : `结账到 ${monthLabel}` }}
      </el-button>
      <el-button
        v-if="fmsStore.isAccountSetWritable && overview.closed"
        type="danger"
        plain
        :loading="submitting"
        @click="cancelToPeriod"
        v-hasPermi="['fms:closing:cancel']"
      >
        {{ isCurrentPeriod ? '反结账' : `反结账到 ${monthLabel}` }}
      </el-button>
      <el-text v-if="!overview.closed && !isBeforeCurrentPeriod && !canClose" type="warning">
        完成上方检查后才可结账
      </el-text>
      <el-text v-if="!overview.closed && isBeforeCurrentPeriod" type="warning">
        结账目标不能早于当前会计期间 {{ currentMonthLabel }}
      </el-text>
    </el-space>
  </ContentWrap>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { FmsClosingPeriodApi } from '@/api/fms/closing/period'
import type { FmsClosingOverviewVO } from '@/api/fms/closing/period'
import { FmsAccountSetApi } from '@/api/fms/config/account-set'
import { useFmsStore } from '@/views/fms/store/fms'
import { formatMoney } from '@/views/fms/utils/format'
import ClosingSchemeList from './ClosingSchemeList.vue'
import ClosingStatusCard from './ClosingStatusCard.vue'

defineOptions({ name: 'FmsClosing' })

const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const month = ref(dayjs().format('YYYY-MM')) // 选中的会计期间
const currentMonth = ref(dayjs().format('YYYY-MM')) // 当前会计期间
const startMonth = ref(dayjs().format('YYYY-MM')) // 账套启用期间
const loading = ref(false) // 页面数据的加载中
const submitting = ref(false) // 结账操作的提交中
const monthLabel = computed(() => dayjs(`${month.value}-01`).format('YYYY年MM月')) // 会计期间文本
const currentMonthLabel = computed(() => dayjs(`${currentMonth.value}-01`).format('YYYY年MM月')) // 当前会计期间文本
const isCurrentPeriod = computed(() => month.value === currentMonth.value) // 是否当前会计期间
const overview = reactive<FmsClosingOverviewVO>({
  month: month.value,
  closed: false,
  voucherReviewRequired: false,
  pendingVoucherCount: 0,
  voucherCount: 0,
  profitLossBalance: 0,
  balanceSheetDifference: 0,
  initialBalanceBalanced: false,
  voucherNumberContinuous: false,
  profitLossVoucherGenerated: false,
  incomeStatementBalanced: false,
  incomeStatementUnmappedSubjectCount: 0,
  balanceSheetProfitLossTransferred: false,
  balanceSheetBalanced: false,
  balanceSheetUnmappedSubjectCount: 0,
  canClose: false
}) // 结账概况
const isBeforeCurrentPeriod = computed(() =>
  dayjs(`${month.value}-01`).isBefore(dayjs(`${currentMonth.value}-01`), 'month')
) // 是否早于当前会计期间
const canClose = computed(() => overview.canClose) // 是否满足后端全部结账条件
const balanceSheetCheckLabel = computed(() => {
  if (!overview.balanceSheetProfitLossTransferred) return '损益未结转'
  if (overview.balanceSheetUnmappedSubjectCount > 0) {
    return `${overview.balanceSheetUnmappedSubjectCount} 个科目未纳入公式`
  }
  return overview.balanceSheetBalanced ? '检查通过' : '不平衡'
}) // 资产负债表检查结果文本

/** 初始化页面 */
async function init() {
  if (!accountSetId.value) {
    return
  }
  const [currentMonthValue, accountSet] = await Promise.all([
    fmsStore.loadCurrentMonth(),
    FmsAccountSetApi.getAccountSet(accountSetId.value)
  ])
  currentMonth.value = currentMonthValue || dayjs().format('YYYY-MM')
  startMonth.value = dayjs(accountSet.startTime).format('YYYY-MM')
  month.value = currentMonth.value
  await getOverview()
}

/** 获得结账概况 */
async function getOverview() {
  if (!accountSetId.value || !month.value) return
  loading.value = true
  try {
    Object.assign(
      overview,
      await FmsClosingPeriodApi.getClosingOverview({
        accountSetId: accountSetId.value,
        month: month.value
      })
    )
  } finally {
    loading.value = false
  }
}

/** 结账 */
async function closeToPeriod() {
  if (!accountSetId.value || isBeforeCurrentPeriod.value) return
  try {
    await message.confirm(
      isCurrentPeriod.value
        ? `结账后将锁定 ${monthLabel.value}，是否继续？`
        : `将按期间顺序结账至 ${monthLabel.value}，是否继续？`
    )
  } catch {
    return
  }
  submitting.value = true
  try {
    await FmsClosingPeriodApi.closePeriod({
      accountSetId: accountSetId.value,
      month: month.value
    })
    message.success('结账成功')
    currentMonth.value = (await fmsStore.loadCurrentMonth()) || currentMonth.value
    month.value = currentMonth.value
    await getOverview()
  } finally {
    submitting.value = false
  }
}

/** 反结账 */
async function cancelToPeriod() {
  if (!accountSetId.value || !overview.closed) return
  try {
    await message.confirm(
      `反结账会影响历史报表数据，将撤销 ${monthLabel.value} 及之后的结账，确认继续吗？`
    )
  } catch {
    return
  }
  submitting.value = true
  try {
    await FmsClosingPeriodApi.cancelClosePeriod({
      accountSetId: accountSetId.value,
      month: month.value
    })
    message.success('反结账成功')
    currentMonth.value = (await fmsStore.loadCurrentMonth()) || currentMonth.value
    month.value = currentMonth.value
    await getOverview()
  } finally {
    submitting.value = false
  }
}

/** 禁用账套启用前和可操作范围外的月份 */
function disabledMonth(date: Date) {
  const selectedMonth = dayjs(date).startOf('month')
  const latestSelectableMonth = dayjs().startOf('month')
  return (
    selectedMonth.isBefore(dayjs(`${startMonth.value}-01`), 'month') ||
    selectedMonth.isAfter(latestSelectableMonth, 'month')
  )
}

watch(accountSetId, init, { immediate: true })
</script>
