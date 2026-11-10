<template>
  <Dialog v-model="dialogVisible" title="试算平衡" width="680px">
    <el-result
      :icon="result?.balanced ? 'success' : 'warning'"
      :title="result?.balanced ? '期初余额试算平衡' : '期初余额试算不平衡'"
      :sub-title="result?.balanced ? '借贷金额相等，可以开始记账' : '请检查期初余额和累计发生额'"
    />
    <el-table :data="rows" border>
      <el-table-column label="项目" prop="name" min-width="180" />
      <el-table-column align="right" label="借方" prop="debitAmount" min-width="130" />
      <el-table-column align="right" label="贷方" prop="creditAmount" min-width="130" />
      <el-table-column align="right" label="差额" prop="differenceAmount" min-width="130" />
    </el-table>
    <template #footer>
      <el-button type="primary" @click="dialogVisible = false">我知道了</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsInitialBalanceApi } from '@/api/fms/config/initial-balance'
import type { FmsTrialBalanceVO } from '@/api/fms/config/initial-balance'
import { formatAmount } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsTrialBalanceDialog' })

const dialogVisible = ref(false) // 弹窗的是否展示
const result = ref<FmsTrialBalanceVO>() // 试算平衡结果
// 试算平衡明细行
const rows = computed(() =>
  result.value
    ? [
        {
          name: '期初余额（综合本位币）',
          debitAmount: formatAmount(result.value.openingDebitAmount),
          creditAmount: formatAmount(result.value.openingCreditAmount),
          differenceAmount: formatAmount(result.value.openingDifferenceAmount)
        },
        {
          name: '累计发生额（综合本位币）',
          debitAmount: formatAmount(result.value.yearDebitAmount),
          creditAmount: formatAmount(result.value.yearCreditAmount),
          differenceAmount: formatAmount(result.value.yearDifferenceAmount)
        }
      ]
    : []
)

/** 打开弹窗 */
async function open(accountSetId: number) {
  result.value = await FmsInitialBalanceApi.getTrialBalance(accountSetId)
  dialogVisible.value = true
}

defineExpose({ open })
</script>
