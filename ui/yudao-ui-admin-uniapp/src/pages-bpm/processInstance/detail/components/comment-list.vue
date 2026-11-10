<template>
  <view class="mx-24rpx mt-24rpx rounded-16rpx bg-white">
    <view class="p-24rpx">
      <view class="mb-24rpx flex items-center">
        <text class="text-28rpx text-[#333] font-bold">流程评论</text>
        <text class="ml-12rpx text-24rpx text-[#999]">共 {{ comments.length }} 条</text>
        <wd-loading v-if="commentLoading" class="ml-auto" size="28rpx" />
      </view>

      <view v-if="!commentLoading && comments.length === 0" class="py-40rpx text-center">
        <text class="text-24rpx text-[#999]">暂无评论</text>
      </view>

      <view v-else class="pl-8rpx">
        <view
          v-for="(comment, index) in comments"
          :key="comment.id"
          class="relative flex gap-20rpx pb-32rpx"
        >
          <view
            v-if="index < comments.length - 1"
            class="absolute bottom-0 left-16rpx top-36rpx w-1rpx bg-[#e5e6eb]"
          />
          <view
            class="z-1 h-64rpx w-64rpx flex shrink-0 items-center justify-center border-4rpx border-white rounded-full text-26rpx text-white font-bold shadow-sm"
            :style="{ backgroundColor: getCommentColor(comment.type) }"
          >
            {{ getCommentText(comment.type) }}
          </view>
          <view class="min-w-0 flex-1">
            <view class="flex items-center gap-12rpx">
              <view class="h-56rpx w-56rpx flex shrink-0 items-center justify-center overflow-hidden rounded-full bg-[#f2f3f5] text-24rpx text-[#666]">
                <image
                  v-if="comment.user?.avatar"
                  :src="comment.user.avatar"
                  class="h-full w-full"
                  mode="aspectFill"
                />
                <text v-else>{{ getUserInitial(comment.user) }}</text>
              </view>
              <text class="shrink-0 text-26rpx text-[#333] font-semibold">{{ comment.user?.nickname || '系统' }}</text>
              <dict-tag :type="DICT_TYPE.BPM_COMMENT_TYPE" :value="comment.type" />
            </view>
            <view v-if="comment.task?.name" class="mt-12rpx max-w-full inline-flex items-center border border-[#d9ecff] rounded-8rpx bg-[#ecf5ff] px-12rpx py-6rpx">
              <text class="mr-8rpx text-22rpx text-[#409eff]">任务</text>
              <text class="truncate text-24rpx text-[#333] font-semibold">{{ comment.task.name }}</text>
            </view>
            <text class="mt-10rpx block text-22rpx text-[#999]">{{ formatDateTime(comment.createTime) }}</text>
            <view class="mt-12rpx rounded-8rpx bg-[#f7f8fa] px-20rpx py-16rpx">
              <text class="whitespace-pre-wrap break-words text-26rpx text-[#606266] leading-40rpx">{{ comment.message }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { Comment } from '@/api/bpm/comment'
import type { User } from '@/api/bpm/processInstance'
import { getCommentListByProcessInstanceId } from '@/api/bpm/comment'
import { getDictObj } from '@/hooks/useDict'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'

const props = defineProps<{
  id?: string
}>()

const commentLoading = ref(false) // 评论列表的加载中
const comments = ref<Comment[]>([]) // 评论列表
const commentColorMap: Record<string, string> = {
  primary: '#409eff',
  success: '#67c23a',
  warning: '#e6a23c',
  danger: '#f56c6c',
  error: '#f56c6c',
  info: '#909399',
  default: '#909399',
} // 评论类型颜色映射

/** 查询评论列表 */
async function getList() {
  if (!props.id) {
    comments.value = []
    return
  }
  commentLoading.value = true
  try {
    comments.value = await getCommentListByProcessInstanceId(props.id)
  } finally {
    commentLoading.value = false
  }
}

/** 获得评论类型简称 */
function getCommentText(type: string) {
  return (getDictObj(DICT_TYPE.BPM_COMMENT_TYPE, type)?.label || '评论').slice(0, 1)
}

/** 获得评论类型颜色 */
function getCommentColor(type: string) {
  const dict = getDictObj(DICT_TYPE.BPM_COMMENT_TYPE, type)
  return dict?.cssClass || commentColorMap[dict?.colorType || 'primary'] || commentColorMap.primary
}

/** 获得评论人首字 */
function getUserInitial(user?: User) {
  return (user?.nickname || '系').slice(0, 1)
}

watch(() => props.id, getList)

/** 暴露方法 */
defineExpose({ getList })
</script>
