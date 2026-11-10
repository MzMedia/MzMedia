<!-- FMS 通用打印预览：在全屏弹窗中预览 HTML，并调用浏览器打印能力 -->
<template>
  <Dialog
    v-model="dialogVisible"
    :title="title"
    width="96%"
    fullscreen
    :close-on-click-modal="false"
    class="fms-print-preview-dialog"
  >
    <div class="flex justify-end gap-8px pb-12px">
      <el-button type="primary" @click="print">打印</el-button>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </div>
    <iframe
      ref="previewIframeRef"
      :srcdoc="html"
      class="h-[calc(100vh-118px)] w-full border border-[var(--el-border-color)] border-solid bg-[#eef0f3]"
    ></iframe>
  </Dialog>
</template>

<script lang="ts" setup>
defineOptions({ name: 'FmsPrintPreview' })

const dialogVisible = ref(false) // 弹窗是否可见
const title = ref('') // 打印预览标题
const html = ref('') // 待预览的 HTML
const previewIframeRef = ref<HTMLIFrameElement>() // 打印预览 iframe

/** 打开打印预览 */
function open(previewTitle: string, previewHtml: string) {
  title.value = previewTitle
  html.value = previewHtml
  dialogVisible.value = true
}
/** 直接打印 HTML，不打开预览弹窗 */
function printHtml(printHtml: string) {
  const iframe = document.createElement('iframe')
  iframe.style.position = 'fixed'
  iframe.style.right = '0'
  iframe.style.bottom = '0'
  iframe.style.width = '0'
  iframe.style.height = '0'
  iframe.style.border = '0'
  iframe.srcdoc = printHtml
  iframe.onload = () => {
    iframe.contentWindow?.focus()
    iframe.contentWindow?.print()
    window.setTimeout(() => iframe.remove(), 1000)
  }
  document.body.appendChild(iframe)
}

defineExpose({ open, printHtml })

/** 调用 iframe 的浏览器打印能力 */
function print() {
  previewIframeRef.value?.contentWindow?.print()
}
</script>
