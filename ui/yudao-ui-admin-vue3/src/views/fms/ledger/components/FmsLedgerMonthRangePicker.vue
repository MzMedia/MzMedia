<template>
  <el-date-picker
    v-model="monthRange"
    type="monthrange"
    value-format="YYYY-MM"
    start-placeholder="开始月份"
    end-placeholder="结束月份"
    class="!w-240px"
    :clearable="false"
    :disabled-date="disabledDate"
    @change="emit('change', $event)"
  />
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsLedgerMonthRangePicker' })

const monthRange = defineModel<string[]>({ required: true }) // 会计期间范围
const emit = defineEmits<{ change: [value: string[] | undefined] }>()
const fmsStore = useFmsStore() // FMS 状态
const accountSetStartMonth = computed(() => {
  const accountSet = fmsStore.getAccountSetList.find((item) => item.id === fmsStore.getAccountSetId)
  return accountSet?.startTime ? dayjs(accountSet.startTime).format('YYYY-MM') : undefined
}) // 账套启用月份

/** 禁用账套启用月份之前的日期 */
function disabledDate(date: Date) {
  return Boolean(
    accountSetStartMonth.value && dayjs(date).format('YYYY-MM') < accountSetStartMonth.value
  )
}
</script>
