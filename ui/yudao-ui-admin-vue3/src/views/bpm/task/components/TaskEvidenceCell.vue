<template>
  <div
    v-if="attachments.length || signPicUrl"
    class="flex flex-wrap items-center justify-center gap-8px"
  >
    <el-popover v-if="attachments.length" placement="top" :width="360" trigger="click">
      <template #reference>
        <el-button link type="primary">
          <Icon icon="ep:paperclip" class="mr-4px" />
          {{ attachments.length }} 个附件
        </el-button>
      </template>
      <div class="max-h-260px overflow-auto">
        <div
          v-for="attachment in attachments"
          :key="attachment"
          class="flex items-center gap-8px py-6px"
        >
          <el-image
            v-if="isImage(attachment)"
            class="h-44px w-44px shrink-0 rounded-4px border border-[var(--el-border-color)]"
            :src="attachment"
            :preview-src-list="[attachment]"
            fit="cover"
            preview-teleported
          />
          <Icon
            v-else
            icon="ep:document"
            class="shrink-0 text-18px text-[var(--el-color-primary)]"
          />
          <el-link
            class="min-w-0 flex-1 justify-start"
            :href="attachment"
            underline="never"
            target="_blank"
            type="primary"
          >
            <span class="truncate">{{ getFileNameFromUrl(attachment) }}</span>
          </el-link>
        </div>
      </div>
    </el-popover>
    <div v-if="signPicUrl" class="inline-flex items-center gap-6px">
      <span class="text-12px text-[var(--el-text-color-secondary)]">签名</span>
      <el-image
        class="h-32px w-72px rounded-4px border border-[var(--el-border-color)] bg-white"
        :src="signPicUrl"
        :preview-src-list="[signPicUrl]"
        fit="contain"
        preview-teleported
      />
    </div>
  </div>
  <span v-else class="text-[var(--el-text-color-placeholder)]">-</span>
</template>

<script lang="ts" setup>
import { getFileNameFromUrl, isImage } from '@/utils/file'

defineOptions({ name: 'BpmTaskEvidenceCell' })

withDefaults(
  defineProps<{
    attachments?: string[]
    signPicUrl?: string
  }>(),
  {
    attachments: () => []
  }
)
</script>
