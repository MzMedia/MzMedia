<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="意见反馈"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 表单区域 -->
    <view class="p-24rpx">
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group custom-class="cell-group" border>
          <wd-form-item title="反馈内容" title-width="180rpx" prop="content">
            <wd-textarea
              v-model="formData.content"
              placeholder="请输入您的宝贵意见和建议"
              :maxlength="500"
              show-word-limit
              clearable
              :rows="5"
            />
          </wd-form-item>
          <wd-form-item title="反馈图片" title-width="180rpx">
            <yd-upload-imgs v-model="images" directory="feedback" :limit="9" />
          </wd-form-item>
        </wd-cell-group>
      </wd-form>
    </view>

    <!-- 底部提交按钮 -->
    <view class="yd-detail-footer">
      <wd-button
        type="primary"
        block
        :loading="formLoading"
        @click="handleSubmit"
      >
        提交反馈
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import { delay, navigateBackPlus } from '@/utils/index'
import { createFormSchema } from '@/utils/wot'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const formLoading = ref(false) // 表单提交状态
const images = ref<string[]>([])
const formData = ref({
  content: '',
}) // 表单数据
const formSchema = createFormSchema({
  content: [
    { required: true, message: '请输入反馈内容' },
    { validator: value => String(value).length >= 10 || '反馈内容至少10个字符' },
  ],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages/user/index')
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  formLoading.value = true
  try {
    // 构建提交数据
    const submitData = {
      content: formData.value.content,
      images: images.value,
    }

    // TODO @芋艿：替换为真实反馈 API 调用
    await mockSubmitFeedback(submitData)

    toast.success('提交成功，感谢您的反馈！')
    delay(handleBack, 1500)
  } finally {
    formLoading.value = false
  }
}

// ===================== Mock API =====================
// TODO @芋艿：后端 API 实现后，删除此 mock 函数

interface FeedbackData {
  content: string
  images: string[]
}

/**
 * Mock 提交反馈接口
 *
 * @param data 反馈数据
 */
function mockSubmitFeedback(data: FeedbackData): Promise<{ code: number, message: string }> {
  return new Promise((resolve, reject) => {
    console.log('[Mock] 提交反馈数据:', data)

    // 模拟网络延迟
    setTimeout(() => {
      // 模拟成功
      if (data.content && data.content.length >= 10) {
        resolve({
          code: 0,
          message: '提交成功',
        })
      } else {
        reject(new Error('反馈内容不能少于 10 个字符'))
      }
    }, 1000)
  })
}
</script>

<style lang="scss" scoped>
:deep(.cell-group) {
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 3rpx 8rpx rgba(24, 144, 255, 0.06);
}

.safe-area-inset-bottom {
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
</style>
