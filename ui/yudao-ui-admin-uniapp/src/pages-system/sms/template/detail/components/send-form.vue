<template>
  <wd-popup
    v-model="visible"
    position="bottom"
    closable
    safe-area-inset-bottom
    custom-style="height: 80vh; border-radius: 16rpx 16rpx 0 0; overflow: hidden;"
  >
    <view class="h-full flex flex-col bg-white">
      <!-- 表单内容 -->
      <scroll-view scroll-y class="min-h-0 flex-1">
        <view class="p-24rpx">
          <view class="mb-24rpx text-32rpx text-[#333] font-semibold">
            发送测试短信
          </view>
          <wd-form ref="sendFormRef" :model="sendFormData" :schema="sendFormSchema">
            <wd-cell-group border>
              <wd-form-item title="模板内容" title-width="180rpx">
                <wd-textarea
                  v-model="sendFormData.content"
                  disabled
                  :rows="3"
                />
              </wd-form-item>
              <wd-form-item title="手机号码" title-width="180rpx" prop="mobile">
                <wd-input
                  v-model="sendFormData.mobile"
                  clearable
                  placeholder="请输入手机号码"
                />
              </wd-form-item>
              <template v-for="param in template?.params" :key="param">
                <wd-form-item :title="`参数 ${param}`" title-width="180rpx" :prop="`templateParams.${param}`">
                  <wd-input
                    v-model="sendFormData.templateParams[param]"
                    clearable
                    :placeholder="`请输入参数 ${param}`"
                  />
                </wd-form-item>
              </template>
            </wd-cell-group>
          </wd-form>
        </view>
      </scroll-view>

      <!-- 底部操作 -->
      <view class="border-t border-[#f0f0f0] bg-white p-24rpx">
        <wd-button type="primary" block :loading="sendLoading" @click="handleSendSubmit">
          发送
        </wd-button>
      </view>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import type { SmsTemplate } from '@/api/system/sms/template'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, ref, watch } from 'vue'
import { sendSms } from '@/api/system/sms/template'
import { createFormSchema } from '@/utils/wot'

const props = defineProps<{
  modelValue: boolean
  template?: SmsTemplate
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': []
}>()

const toast = useToast()

const visible = computed({
  get() {
    return props.modelValue
  },
  set(value: boolean) {
    emit('update:modelValue', value)
  },
}) // 发送弹窗显示状态

const sendLoading = ref(false)
const sendFormRef = ref<any>()
const sendFormData = ref({
  content: '',
  mobile: '',
  templateParams: {} as Record<string, string>,
})
const sendFormSchema = createFormSchema(() => {
  return {
    mobile: [{ required: true, message: '手机号码不能为空' }],
    ...Object.fromEntries(
      (props.template?.params || []).map(param => [
        `templateParams.${param}`,
        [{ required: true, message: `参数 ${param} 不能为空` }],
      ]),
    ),
  }
})

/** 初始化 */
function initSendForm() {
  sendFormData.value = {
    content: props.template?.content || '',
    mobile: '',
    templateParams: {},
  }
  if (props.template?.params) {
    props.template.params.forEach((param) => {
      sendFormData.value.templateParams[param] = ''
    })
  }
}

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      initSendForm()
    }
  },
)

/** 提交表单 */
async function handleSendSubmit() {
  const { valid } = await sendFormRef.value.validate()
  if (!valid) {
    return
  }

  sendLoading.value = true
  try {
    await sendSms({
      mobile: sendFormData.value.mobile,
      templateCode: props.template?.code || '',
      templateParams: sendFormData.value.templateParams,
    })
    toast.success('短信发送成功')
    emit('success')
    visible.value = false
  } finally {
    sendLoading.value = false
  }
}
</script>
