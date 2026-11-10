<template>
  <div v-loading="loading" class="min-h-180px px-8px pt-12px">
    <el-timeline v-if="records.length">
      <el-timeline-item
        v-for="(record, index) in records"
        :key="`${record.operateTime || ''}-${index}`"
        :timestamp="formatHrmDateTime(record.operateTime)"
        placement="top"
      >
        <div class="flex items-center justify-between gap-12px">
          <span class="font-600">{{ record.title || '-' }}</span>
          <span v-if="record.operatorName" class="text-13px text-[var(--el-text-color-secondary)]">
            {{ record.operatorName }}
          </span>
        </div>
        <div class="mt-6px whitespace-pre-wrap text-[var(--el-text-color-regular)] leading-[1.6]">
          {{ record.content || '-' }}
        </div>
        <div v-if="record.fileUrls?.length" class="mt-6px flex flex-wrap gap-12px">
          <el-link
            v-for="url in record.fileUrls"
            :key="url"
            type="primary"
            underline="never"
            @click="openSafeUrl(url)"
          >
            <Icon icon="ep:paperclip" class="mr-4px" />{{ getFileNameFromUrl(url) }}
          </el-link>
        </div>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else-if="!loading" description="暂无流程记录" />
  </div>
</template>

<script lang="ts" setup>
import type { PerformanceProcessRecordVO } from '@/api/hrm/performance/assessment'
import { getFileNameFromUrl } from '@/utils/file'
import { openSafeUrl } from '@/utils/url'
import { formatHrmDateTime } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPerformanceProcessRecordTimeline' })

defineProps<{
  records: PerformanceProcessRecordVO[]
  loading?: boolean
}>()
</script>
