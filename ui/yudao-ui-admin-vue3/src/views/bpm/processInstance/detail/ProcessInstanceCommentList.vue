<template>
  <div class="min-h-100% py-24px px-28px" v-loading="props.loading || commentLoading">
    <div
      class="flex items-center gap-12px pb-18px border-b border-b-[var(--el-border-color-lighter)]"
    >
      <div class="text-18px font-bold text-[var(--el-text-color-primary)]">流程评论</div>
      <div class="text-13px text-[var(--el-text-color-secondary)]">共 {{ comments.length }} 条</div>
    </div>
    <el-empty v-if="!comments.length" description="暂无评论" />
    <el-timeline
      v-else
      class="mt-24px pl-8px [&_.el-timeline-item]:pl-42px [&_.el-timeline-item__tail]:left-16px [&_.el-timeline-item__dot]:left-0 [&_.el-timeline-item__wrapper]:top-0 [&_.el-timeline-item__wrapper]:pl-0"
    >
      <el-timeline-item
        v-for="comment in comments"
        :key="comment.id"
        :color="getCommentColor(comment.type)"
      >
        <template #dot>
          <div
            class="flex items-center justify-center w-32px h-32px text-14px font-bold leading-none text-white border-2 border-white rounded-full shadow-[0_0_0_1px_var(--el-border-color-light)]"
            :style="{ backgroundColor: getCommentColor(comment.type) }"
          >
            {{ getCommentText(comment.type) }}
          </div>
        </template>
        <div class="pb-18px pl-12px">
          <div class="flex items-center gap-10px min-h-32px min-w-0">
            <div
              class="flex items-center gap-8px shrink-0 font-bold text-[var(--el-text-color-primary)]"
            >
              <el-avatar v-if="comment.user?.avatar" :size="28" :src="comment.user.avatar" />
              <el-avatar v-else :size="28">
                {{ comment.user?.nickname?.substring(0, 1) || '?' }}
              </el-avatar>
              <span>{{ comment.user?.nickname || '系统' }}</span>
            </div>
            <dict-tag class="shrink-0" :type="DICT_TYPE.BPM_COMMENT_TYPE" :value="comment.type" />
            <div
              v-if="comment.task?.name"
              class="inline-flex items-center h-26px min-w-0 max-w-520px gap-6px px-8px text-13px border border-[var(--el-color-primary-light-7)] rounded-6px bg-[var(--el-color-primary-light-9)] text-[var(--el-text-color-regular)]"
            >
              <span
                class="inline-flex items-center gap-4px shrink-0 font-medium text-[var(--el-color-primary)]"
              >
                <Icon icon="ep:connection" class="text-14px" />
                任务
              </span>
              <span class="min-w-0 font-medium truncate text-[var(--el-text-color-primary)]">
                {{ comment.task.name }}
              </span>
            </div>
            <span class="ml-auto shrink-0 text-13px text-[var(--el-text-color-secondary)]">
              {{ formatDate(comment.createTime) }}
            </span>
          </div>
          <div
            class="py-12px px-14px mt-10px leading-22px text-[var(--el-text-color-regular)] [overflow-wrap:anywhere] whitespace-pre-wrap bg-[var(--el-fill-color-lighter)] rounded-6px"
          >
            {{ comment.message }}
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getDictObj } from '@/utils/dict'
import * as CommentApi from '@/api/bpm/comment'

defineOptions({ name: 'BpmProcessInstanceCommentList' })

const props = withDefaults(
  defineProps<{
    id?: string
    loading?: boolean
  }>(),
  {
    loading: false
  }
)

const commentLoading = ref(false) // 评论列表的加载中
const comments = ref<CommentApi.CommentVO[]>([]) // 评论列表

const commentColorMap: Record<string, string> = {
  primary: 'var(--el-color-primary)',
  success: 'var(--el-color-success)',
  warning: 'var(--el-color-warning)',
  danger: 'var(--el-color-danger)',
  info: 'var(--el-color-info)'
} // 评论类型颜色映射

/** 获得评论类型简称 */
const getCommentText = (type: string) => {
  return (getDictObj(DICT_TYPE.BPM_COMMENT_TYPE, type)?.label || '评论').substring(0, 1)
}

/** 获得评论类型颜色 */
const getCommentColor = (type: string) => {
  const dict = getDictObj(DICT_TYPE.BPM_COMMENT_TYPE, type)
  return (
    dict?.cssClass ||
    commentColorMap[(dict?.colorType || 'primary') as string] ||
    commentColorMap.primary
  )
}

/** 查询评论列表 */
const getList = async () => {
  if (!props.id) {
    comments.value = []
    return
  }
  commentLoading.value = true
  try {
    comments.value = await CommentApi.getCommentListByProcessInstanceId(props.id)
  } finally {
    commentLoading.value = false
  }
}

watch(
  () => props.id,
  () => getList(),
  { immediate: true }
)

defineExpose({ getList })
</script>
