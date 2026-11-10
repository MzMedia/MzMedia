<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="错误日志详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 详情内容 -->
    <view>
      <wd-cell-group border>
        <wd-cell title="日志编号" :value="formData?.id || '-'" />
        <wd-cell title="链路追踪" :value="formData?.traceId || '-'" />
        <wd-cell title="应用名" :value="formData?.applicationName || '-'" />
        <wd-cell title="用户编号" :value="formData?.userId ?? '-'" />
        <wd-cell title="用户类型">
          <dict-tag :type="DICT_TYPE.USER_TYPE" :value="formData?.userType" />
        </wd-cell>
        <wd-cell title="用户 IP" :value="formData?.userIp || '-'" />
        <wd-cell title="用户 UA" :value="formData?.userAgent || '-'" />
        <wd-cell title="请求信息" :value="getRequestInfo()" />
        <wd-cell title="请求参数" is-link @click="handleCopyText(formData?.requestParams, '请求参数')">
          <view class="max-w-400rpx truncate text-right">
            {{ formData?.requestParams || '-' }}
          </view>
        </wd-cell>
        <wd-cell title="异常时间" :value="formatDateTime(formData?.exceptionTime) || '-'" />
        <wd-cell title="异常名" :value="formData?.exceptionName || '-'" />
        <wd-cell title="异常消息" is-link @click="handleCopyText(getExceptionMessage(), '异常消息')">
          <view class="max-w-400rpx truncate text-right">
            {{ getExceptionMessage() }}
          </view>
        </wd-cell>
        <wd-cell title="异常位置" is-link @click="handleCopyText(getExceptionLocation(), '异常位置')">
          <view class="max-w-400rpx truncate text-right">
            {{ getExceptionLocation() }}
          </view>
        </wd-cell>
        <wd-cell title="处理状态">
          <dict-tag :type="DICT_TYPE.INFRA_API_ERROR_LOG_PROCESS_STATUS" :value="formData?.processStatus" />
        </wd-cell>
        <wd-cell v-if="formData?.processUserId" title="处理人" :value="formData.processUserId" />
        <wd-cell v-if="formData?.processTime" title="处理时间" :value="formatDateTime(formData.processTime) || '-'" />
      </wd-cell-group>

      <!-- 异常堆栈 -->
      <view v-if="formData?.exceptionStackTrace" class="mt-24rpx">
        <view class="mb-16rpx text-28rpx text-[#333] font-semibold">
          异常堆栈
        </view>
        <view class="rounded-12rpx bg-white p-24rpx shadow-sm">
          <scroll-view scroll-y class="max-h-600rpx">
            <text class="whitespace-pre-wrap break-all text-24rpx text-[#666] leading-relaxed">{{ formData.exceptionStackTrace }}</text>
          </scroll-view>
        </view>
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view
      v-if="formData?.processStatus === InfraApiErrorLogProcessStatusEnum.INIT && hasAccessByCodes(['infra:api-error-log:update-status'])"
      class="yd-detail-footer"
    >
      <view class="yd-detail-footer-actions">
        <wd-button class="flex-1" type="success" :loading="processing" @click="handleProcess(InfraApiErrorLogProcessStatusEnum.DONE)">
          已处理
        </wd-button>
        <wd-button class="flex-1" type="warning" :loading="processing" @click="handleProcess(InfraApiErrorLogProcessStatusEnum.IGNORE)">
          已忽略
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { ApiErrorLog } from '@/api/infra/api-error-log'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, ref } from 'vue'
import { getApiErrorLog, updateApiErrorLogStatus } from '@/api/infra/api-error-log'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE, InfraApiErrorLogProcessStatusEnum } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'

const props = defineProps<{
  id: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const formData = ref<ApiErrorLog>() // 详情数据
const processing = ref(false) // 处理中

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/api-error-log/index')
}

/** 复制文本并提示 */
function handleCopyText(text?: string, title?: string) {
  if (!text || text === '-') {
    return
  }
  uni.setClipboardData({
    data: text,
    success: () => {
      uni.hideToast()
      toast.success(`${title || '内容'}已复制`)
    },
  })
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  toast.loading('加载中...')
  try {
    formData.value = await getApiErrorLog(props.id)
  } finally {
    toast.close()
  }
}

/** 获取请求信息 */
function getRequestInfo() {
  if (formData.value?.requestMethod && formData.value?.requestUrl) {
    return `${formData.value.requestMethod} ${formData.value.requestUrl}`
  }
  return '-'
}

/** 获取异常消息（优先根因消息） */
function getExceptionMessage() {
  return formData.value?.exceptionRootCauseMessage || formData.value?.exceptionMessage || '-'
}

/** 获取异常位置（类名:文件名:行号 方法名） */
function getExceptionLocation() {
  const data = formData.value
  if (!data?.exceptionClassName) {
    return '-'
  }
  return `${data.exceptionClassName}:${data.exceptionFileName}:${data.exceptionLineNumber}(${data.exceptionMethodName})`
}

/** 处理日志 */
async function handleProcess(processStatus: number) {
  if (!props.id) {
    return
  }
  const statusText = processStatus === InfraApiErrorLogProcessStatusEnum.DONE ? '已处理' : '已忽略'
  try {
    await dialog.confirm({
      title: '提示',
      msg: `确定标记为${statusText}吗？`,
    })
  } catch {
    return
  }

  processing.value = true
  try {
    await updateApiErrorLogStatus(props.id, processStatus)
    formData.value = await getApiErrorLog(props.id)
    toast.success('操作成功')
    uni.$emit('infra:api-error-log:reload')
  } finally {
    processing.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
})
</script>
