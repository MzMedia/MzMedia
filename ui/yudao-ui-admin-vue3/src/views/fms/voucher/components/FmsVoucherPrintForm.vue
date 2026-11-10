<template>
  <!-- 凭证打印设置 -->
  <Dialog v-model="dialogVisible" title="凭证打印" width="500px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="[&_.el-form-item]:!mb-12px [&_.el-form-item__label]:!pb-2px"
    >
      <el-form-item label="打印类型" prop="paperType">
        <el-radio-group v-model="formData.paperType">
          <el-radio value="A4">A4</el-radio>
          <el-radio value="B5">B5</el-radio>
          <el-radio value="CUSTOM">自定义纸张</el-radio>
        </el-radio-group>
        <div v-if="formData.paperType === 'CUSTOM'" class="mt-10px flex items-center gap-20px">
          <div class="flex items-center gap-8px [&_.el-input-number]:!w-72px">
            <span>宽度</span>
            <el-input-number v-model="formData.width" :controls="false" :min="1" />
            <span>毫米</span>
          </div>
          <div class="flex items-center gap-8px [&_.el-input-number]:!w-72px">
            <span>长度</span>
            <el-input-number v-model="formData.height" :controls="false" :min="1" />
            <span>毫米</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="图像方向">
        <el-radio-group v-model="formData.orientation">
          <el-radio value="portrait">纵向</el-radio>
          <el-radio value="landscape">横向</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="边框调整">
        <div class="flex items-center gap-20px">
          <div class="flex items-center gap-8px [&_.el-input-number]:!w-72px">
            <span>左</span>
            <el-input-number v-model="formData.marginLeft" :controls="false" :min="0" />
            <span>毫米</span>
          </div>
          <div class="flex items-center gap-8px [&_.el-input-number]:!w-72px">
            <span>上</span>
            <el-input-number v-model="formData.marginTop" :controls="false" :min="0" />
            <span>毫米</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="字体大小">
        <div class="flex items-center gap-8px [&_.el-input-number]:!w-72px">
          <el-input-number v-model="formData.fontSize" :controls="false" :min="12" :max="24" />
          <span>像素</span>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">保存并打印</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <!-- 系统打印使用的隐藏 iframe -->
  <iframe
    ref="printIframeRef"
    class="pointer-events-none fixed -left-9999px top-0 h-1px w-1px border-0 opacity-0"
    title="凭证打印"
  ></iframe>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { FmsVoucherVO } from '@/api/fms/voucher'
import { useCache } from '@/hooks/web/useCache'
import {
  buildVoucherPrintHtml,
  DEFAULT_VOUCHER_PRINT_SETTING,
  type VoucherPrintSetting
} from './print'

defineOptions({ name: 'FmsVoucherPrintForm' })

const { wsCache } = useCache()
const message = useMessage()

const dialogVisible = ref(false) // 弹窗的是否展示
const accountSetId = ref(0) // 账套编号
const companyName = ref('') // 公司名称
const vouchers = ref<FmsVoucherVO[]>([]) // 待打印凭证列表
const formData = ref<VoucherPrintSetting>({ ...DEFAULT_VOUCHER_PRINT_SETTING }) // 打印设置
const formRules = reactive<FormRules>({
  paperType: [
    { required: true, message: '请选择打印类型', trigger: 'change' },
    {
      validator: (_rule, _value, callback) => {
        if (
          formData.value.paperType !== 'CUSTOM' ||
          (formData.value.width && formData.value.height)
        ) {
          callback()
          return
        }
        callback(new Error('请输入自定义纸张的宽度和长度'))
      },
      trigger: 'change'
    }
  ]
})
const formRef = ref<FormInstance>() // 表单 Ref
const printIframeRef = ref<HTMLIFrameElement>() // 打印 iframe Ref

/** 打开凭证打印弹窗 */
function open(accountId: number, accountCompanyName: string, voucherList: FmsVoucherVO[]) {
  // 初始化待打印凭证和打印设置
  accountSetId.value = accountId
  companyName.value = accountCompanyName
  vouchers.value = voucherList
  formData.value = {
    ...DEFAULT_VOUCHER_PRINT_SETTING,
    ...wsCache.get(getStorageKey(accountId))
  }
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

/** 提交凭证打印 */
async function submitForm() {
  // 校验打印设置
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 保存当前账套的打印设置并调用系统打印
  wsCache.set(getStorageKey(accountSetId.value), formData.value)
  await printHtml(buildVoucherPrintHtml(companyName.value, vouchers.value, formData.value))
  dialogVisible.value = false
}

/** 直接打印 HTML */
async function printHtml(html: string) {
  // 将完整打印文档写入隐藏 iframe
  const printDocument = printIframeRef.value?.contentDocument
  const printWindow = printIframeRef.value?.contentWindow
  if (!printDocument || !printWindow) return
  printDocument.open()
  printDocument.write(html)
  printDocument.close()
  // 等待字体完成加载，避免系统打印预览中的文字错位
  await printDocument.fonts?.ready
  printWindow.focus()
  printWindow.print()
}

/** 在新窗口打开凭证列表预览，供不带版式设置的列表打印使用 */
function previewHtml(html: string) {
  const previewWindow = window.open('', '_blank')
  if (!previewWindow) {
    message.warning('浏览器阻止了新窗口，请允许弹出窗口后重试')
    return
  }
  previewWindow.document.open()
  previewWindow.document.write(html)
  previewWindow.document.close()
  previewWindow.focus()
}

/** 获得凭证打印设置缓存键 */
function getStorageKey(accountId: number) {
  return `fmsVoucherPrintSetting:${accountId}`
}

defineExpose({ open, printHtml, previewHtml })
</script>
