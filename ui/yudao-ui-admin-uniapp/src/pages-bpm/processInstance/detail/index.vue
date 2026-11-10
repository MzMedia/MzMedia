<template>
  <view class="yd-page-container pb-[80rpx]">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="审批详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 区域：流程信息（基本信息） -->
    <view class="relative mx-24rpx mt-24rpx overflow-hidden rounded-16rpx bg-white">
      <!-- 审批状态图标（盖章效果） -->
      <wd-img
        v-if="processInstance?.status !== undefined"
        :src="getStatusIcon(processInstance?.status)"
        width="144rpx"
        height="144rpx"
        mode="aspectFit"
        class="absolute right-20rpx top-20rpx z-10"
      />
      <view class="p-24rpx">
        <!-- 标题 -->
        <view class="mb-16rpx pr-160rpx">
          <text class="text-32rpx text-[#333] font-bold">{{ processInstance?.name }}</text>
        </view>
        <!-- 发起人信息 -->
        <view class="flex items-center">
          <view class="mr-12rpx h-64rpx w-64rpx flex items-center justify-center rounded-full bg-[#1890ff] text-white">
            {{ processInstance?.startUser?.nickname?.[0] || '?' }}
          </view>
          <view>
            <text class="text-28rpx text-[#333]">{{ processInstance?.startUser?.nickname }}</text>
            <text v-if="processInstance?.startUser?.deptName" class="ml-8rpx text-24rpx text-[#999]">
              {{ processInstance?.startUser?.deptName }}
            </text>
          </view>
        </view>
        <!-- 提交时间 -->
        <view class="mt-16rpx text-24rpx text-[#999]">
          提交于 {{ formatDateTime(processInstance?.startTime) }}
        </view>
      </view>
    </view>

    <!-- Tabs 区域：详情内容 -->
    <view class="mx-24rpx mt-24rpx overflow-hidden rounded-16rpx bg-white">
      <wd-tabs v-model="tabIndex" shrink>
        <wd-tab title="详情" />
        <wd-tab title="进度" />
        <wd-tab title="评论" />
        <wd-tab title="流程图" />
      </wd-tabs>
    </view>

    <!-- 区域：审批详情（表单） -->
    <FormDetail
      v-show="tabType === 'detail'"
      ref="formDetailRef"
      :process-definition="processDefinition"
      :process-instance="processInstance"
      :form-fields-permission="formFieldsPermission"
    />

    <!-- 区域：审批进度 -->
    <view v-show="tabType === 'progress'" class="mx-24rpx mt-24rpx rounded-16rpx bg-white">
      <view class="p-24rpx">
        <view class="mb-16rpx flex">
          <text class="text-28rpx text-[#333] font-bold">审批进度</text>
        </view>
        <!-- 流程时间线 -->
        <ProcessInstanceTimeline :activity-nodes="activityNodes" />
      </view>
    </view>

    <!-- 区域：流程评论 -->
    <ProcessInstanceCommentList v-show="tabType === 'comment'" :id="props.id" ref="commentListRef" />

    <!-- 区域：流程图（仅 PC 预览，移动端不支持） -->
    <view v-show="tabType === 'diagram'" class="mx-24rpx mt-24rpx rounded-16rpx bg-white">
      <view class="p-24rpx">
        <view class="flex items-center justify-between">
          <view class="flex items-center">
            <text class="text-28rpx text-[#333] font-bold">流程图</text>
            <text class="ml-12rpx rounded-6rpx bg-[#fff7e6] px-10rpx py-2rpx text-20rpx text-[#fa8c16]">PC</text>
          </view>
          <wd-button
            icon="printer"
            size="small"
            type="primary"
            variant="plain"
            :round="false"
            @click="handlePrintTip"
          >
            打印
          </wd-button>
        </view>
        <text class="mt-16rpx block text-24rpx text-[#999] leading-36rpx">仅 PC 支持预览，请前往 PC 端查看 BPMN 或简易流程图。</text>
      </view>
    </view>

    <!-- 区域：底部操作栏 -->
    <ProcessInstanceOperationButton
      ref="operationButtonRef"
      :validate-normal-form="validateNormalForm"
      :get-normal-form-variables="getNormalFormVariables"
    />
  </view>
</template>

<script lang="ts" setup>
import type { ApprovalNodeInfo, ProcessDefinition, ProcessInstance } from '@/api/bpm/processInstance'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onUnload } from '@dcloudio/uni-app'
import { computed, onMounted, ref } from 'vue'
import { getApprovalDetail } from '@/api/bpm/processInstance'
import { navigateBackPlus } from '@/utils'
import { BpmProcessInstanceStatus } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import ProcessInstanceCommentList from './components/comment-list.vue'
import FormDetail from './components/form-detail.vue'
import ProcessInstanceOperationButton from './components/operation-button.vue'
import ProcessInstanceTimeline from './components/time-line.vue'

const props = defineProps<{
  id: string // 流程实例的编号
  taskId?: string // 任务编号
  activityId?: string // 活动编号（抄送查看场景定位）
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const processInstance = ref<ProcessInstance>()
const processDefinition = ref<ProcessDefinition>()
const formFieldsPermission = ref<Record<string, string>>({})

const activityNodes = ref<ApprovalNodeInfo[]>([]) // 审批节点信息
const tabIndex = ref(0) // 当前选中的详情页签下标
const tabTypes = ['detail', 'progress', 'comment', 'diagram'] as const
const tabType = computed(() => tabTypes[tabIndex.value] || 'detail') // 当前选中的详情页签类型

const operationButtonRef = ref() // 操作按钮组件 ref
const formDetailRef = ref<InstanceType<typeof FormDetail>>() // 流程表单 ref
const commentListRef = ref<InstanceType<typeof ProcessInstanceCommentList>>() // 评论列表组件 ref

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages/bpm/index')
}

/** 获取状态图标 */
function getStatusIcon(status?: number): string {
  // 状态映射： 1-审批中, 2-审批通过, 3-审批不通过, 4-已取消, 7-审批通过中. -1 未开始不会出现
  const iconMap: Record<number, string> = {
    [BpmProcessInstanceStatus.RUNNING]: '/static/my-icons/bpm/bpm-running.svg', // 待审批
    [BpmProcessInstanceStatus.APPROVE]: '/static/my-icons/bpm/bpm-approve.svg', // 审批通过
    [BpmProcessInstanceStatus.REJECT]: '/static/my-icons/bpm/bpm-reject.svg', // 审批不通过
    [BpmProcessInstanceStatus.CANCEL]: '/static/my-icons/bpm/bpm-cancel.svg', // 已取消
    [BpmProcessInstanceStatus.APPROVING]: '/static/my-icons/bpm/bpm-running.svg', // 审批通过中
  }
  return iconMap[status ?? BpmProcessInstanceStatus.RUNNING] || iconMap[BpmProcessInstanceStatus.RUNNING]
}

/** 打印提示 */
function handlePrintTip() {
  toast.show('请前往 PC 打印')
}

/** 加载流程实例 */
async function loadProcessInstance() {
  const data = await getApprovalDetail({
    processInstanceId: props.id,
    activityId: props.activityId,
    taskId: props.taskId,
  })
  if (!data || !data.processInstance) {
    toast.show('查询不到审批详情信息')
    return
  }
  processInstance.value = data.processInstance
  processDefinition.value = data.processDefinition
  formFieldsPermission.value = data.formFieldsPermission || {}
  // 获取审批节点，显示 Timeline 的数据
  activityNodes.value = data.activityNodes

  // 初始化操作栏，并统一刷新评论列表
  operationButtonRef.value?.init(data.processInstance, data.todoTask)
  commentListRef.value?.getList()
}

/** 校验流程表单中当前节点允许编辑的字段 */
async function validateNormalForm() {
  const result = await formDetailRef.value?.validate()
  return result?.valid !== false
}

/** 获取流程表单中当前节点允许编辑的变量 */
function getNormalFormVariables() {
  return formDetailRef.value?.getWritableVariables() || {}
}

/** 初始化 */
onMounted(() => {
  if (!props.id) {
    toast.show('参数错误')
    return
  }
  uni.$on('bpm:processInstance:reload', loadProcessInstance)
  loadProcessInstance()
})

/** 页面卸载时注销刷新监听 */
onUnload(() => {
  uni.$off('bpm:processInstance:reload', loadProcessInstance)
})
</script>
