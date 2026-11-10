<template>
  <el-button v-hasPermi="[`${permissionPrefix}:print`]" type="primary" plain @click="handlePrint">
    <Icon icon="ep:printer" /> 打印
  </el-button>

  <!-- 打印预览 -->
  <FmsPrintPreview ref="printPreviewRef" />
</template>

<script lang="ts" setup>
import FmsPrintPreview from '@/views/fms/components/print/FmsPrintPreview.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import { formatPeriodLabel } from '@/views/fms/utils/format'
import { buildFmsTablePrintHtml } from '@/views/fms/utils/print'

defineOptions({ name: 'FmsLedgerPrintButton' })

const props = withDefaults(
  defineProps<{
    target: string
    title: string
    startMonth: string
    endMonth: string
    permissionPrefix?: string
    centerText?: string
    beforePrint?: () => void | Promise<void>
  }>(),
  {
    permissionPrefix: 'fms:ledger:general',
    centerText: ''
  }
)

const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态
const printPreviewRef = ref<InstanceType<typeof FmsPrintPreview>>() // 打印预览 Ref

/** 打印账表 */
async function handlePrint() {
  await props.beforePrint?.()
  await nextTick()
  const tableElement = document.getElementById(props.target)
  if (!tableElement) {
    message.error('未找到可打印的表格')
    return
  }
  printPreviewRef.value?.printHtml(
    buildFmsTablePrintHtml({
      title: props.title,
      companyName: fmsStore.getAccountSet?.companyName || '',
      periodLabel: formatPeriodLabel(props.startMonth, props.endMonth),
      centerText: props.centerText,
      tableElement
    })
  )
}
</script>
