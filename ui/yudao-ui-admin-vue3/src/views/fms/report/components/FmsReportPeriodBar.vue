<template>
  <!-- 报表周期与会计期间 -->
  <el-form class="-mb-15px" :inline="true" label-width="68px">
    <el-form-item label="报表周期">
      <el-radio-group v-model="periodType" @change="emitQuery">
        <el-radio-button value="month">月报</el-radio-button>
        <el-radio-button value="quarter">季报</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="会计期间">
      <el-date-picker
        v-model="reportMonth"
        type="month"
        value-format="YYYY-MM"
        :clearable="false"
        :disabled-date="disabledDate"
        :placeholder="periodType === 'month' ? '选择月份' : '选择季度内月份'"
        @change="emitQuery"
      />
    </el-form-item>
    <el-form-item>
      <el-button @click="emitQuery"> <Icon icon="ep:refresh" class="mr-5px" /> 刷新 </el-button>
      <slot></slot>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { FmsAccountSetApi } from '@/api/fms/config/account-set'
import { useFmsStore } from '@/views/fms/store/fms'

const emit = defineEmits<{
  query: [value: { startMonth: string; endMonth: string; label: string }]
}>()

const fmsStore = useFmsStore()
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const periodType = ref<'month' | 'quarter'>('month') // 报表周期
const reportMonth = ref(dayjs().format('YYYY-MM')) // 会计期间
const startMonth = ref(dayjs().format('YYYY-MM')) // 账套启用月份，可选期间的最早月份
const currentMonth = ref(dayjs().format('YYYY-MM')) // 账套当前月份，可选期间的最晚月份

watch(accountSetId, initializePeriod, { immediate: true })

/** 初始化可选期间并触发首次查询 */
async function initializePeriod() {
  const initializingAccountSetId = accountSetId.value
  if (!initializingAccountSetId) return
  const [currentMonthValue, accountSet] = await Promise.all([
    fmsStore.loadCurrentMonth(),
    FmsAccountSetApi.getAccountSet(initializingAccountSetId)
  ])
  if (accountSetId.value !== initializingAccountSetId) return
  startMonth.value = dayjs(accountSet.startTime).format('YYYY-MM')
  currentMonth.value = currentMonthValue || dayjs().format('YYYY-MM')
  reportMonth.value = currentMonth.value
  emitQuery()
}

/** 禁用账套启用月份之前和当前月份之后的日期 */
function disabledDate(date: Date) {
  const month = dayjs(date).format('YYYY-MM')
  return month < startMonth.value || month > currentMonth.value
}

/** 触发查询：月报取当月，季报取所选月份所在季度 */
function emitQuery() {
  const month = dayjs(`${reportMonth.value}-01`)
  if (periodType.value === 'month') {
    emit('query', {
      startMonth: month.format('YYYY-MM'),
      endMonth: month.format('YYYY-MM'),
      label: month.format('YYYY年MM月')
    })
    return
  }
  const quarter = Math.floor(month.month() / 3) + 1
  const quarterStartMonth = month.month((quarter - 1) * 3)
  emit('query', {
    startMonth: quarterStartMonth.format('YYYY-MM'),
    endMonth: quarterStartMonth.add(2, 'month').format('YYYY-MM'),
    label: `${month.year()}年第${quarter}季度`
  })
}
</script>
