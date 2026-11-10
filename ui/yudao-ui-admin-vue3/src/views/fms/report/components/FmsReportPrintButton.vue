<template>
  <el-button :disabled="disabled" @click="handlePrint">
    <Icon icon="ep:printer" class="mr-5px" />
    打印
  </el-button>
  <!-- 打印预览弹窗：挂到 body，避免行内渲染元素打断工具栏相邻按钮的间距 -->
  <Teleport to="body">
    <FmsPrintPreview ref="printPreviewRef" />
  </Teleport>
</template>

<script lang="ts" setup>
import { useFmsStore } from '@/views/fms/store/fms'
import FmsPrintPreview from '@/views/fms/components/print/FmsPrintPreview.vue'
import { buildFmsTablePrintHtml } from '@/views/fms/utils/print'

defineOptions({ name: 'FmsReportPrintButton' })

const props = defineProps<{
  disabled?: boolean
  periodLabel: string
  target: string
  title: string
}>()

const message = useMessage()
const fmsStore = useFmsStore()
const printPreviewRef = ref<InstanceType<typeof FmsPrintPreview>>() // 打印预览弹窗

/** 打印：构造目标表格的打印 HTML 并打开预览 */
function handlePrint() {
  const tableElement = document.getElementById(props.target)
  if (!tableElement) {
    message.error('未找到可打印的表格')
    return
  }
  printPreviewRef.value?.printHtml(
    buildFmsTablePrintHtml({
      title: props.title,
      companyName: fmsStore.getAccountSet?.companyName || '',
      periodLabel: props.periodLabel,
      footerLabels: ['单位负责人：', '会计负责人：', '制表人：'],
      tableElement
    })
  )
}
</script>
