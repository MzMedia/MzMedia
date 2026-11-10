<template>
  <div class="flex items-start justify-between gap-20px">
    <el-tabs v-model="activeTab" class="performance-tabs flex-1" @tab-change="emit('main-change')">
      <el-tab-pane v-for="item in mainTabs" :key="item.name" :name="item.name" :label="item.label">
        <template #label>
          <span>
            {{ item.label }}
            <i
              v-if="item.count"
              class="ml-6px h-18px min-w-18px inline-flex items-center justify-center rounded-9px bg-[var(--el-color-primary)] px-5px text-11px text-white not-italic"
            >
              {{ item.count }}
            </i>
          </span>
        </template>
      </el-tab-pane>
    </el-tabs>
    <el-input
      v-model="keyword"
      class="!w-280px"
      clearable
      placeholder="请输入姓名/工号/考核名称"
      :prefix-icon="Search"
      @clear="emit('query')"
      @keyup.enter="emit('query')"
    />
  </div>

  <el-tabs v-model="activeStatus" class="sub-tabs" @tab-change="emit('status-change')">
    <el-tab-pane v-for="item in statusTabs" :key="item.name" :name="item.name">
      <template #label>
        <span>
          {{ item.label }}
          <span v-if="item.count > 0" class="ml-5px text-[var(--el-color-primary)]">
            {{ item.count }}
          </span>
        </span>
      </template>
    </el-tab-pane>
  </el-tabs>
</template>

<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import type { PortalPerformanceTaskCountVO } from '@/api/hrm/portal/performance/assessment'
import { HrmPerformanceStageType } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmPortalPerformanceTaskTabs' })

const props = defineProps<{
  taskCount: PortalPerformanceTaskCountVO
  statusTabs: Array<{ label: string; name: number; count: number }>
}>()

const activeTab = defineModel<number>('activeTab', { required: true }) // 当前任务页签
const activeStatus = defineModel<number>('activeStatus', { required: true }) // 当前任务状态
const keyword = defineModel<string>('keyword', { required: true }) // 搜索关键字
const emit = defineEmits<{
  query: []
  'main-change': []
  'status-change': []
}>() // 定义 query/main-change/status-change 事件

const mainTabs = computed(() => [
  {
    label: '指标填写',
    name: HrmPerformanceStageType.FILL_QUOTA,
    count: props.taskCount.fillPendingCount
  },
  {
    label: '指标确认',
    name: HrmPerformanceStageType.TARGET_CONFIRM,
    count: props.taskCount.targetPendingCount
  },
  {
    label: '指标评分',
    name: HrmPerformanceStageType.OTHER_SCORE,
    count: props.taskCount.reviewPendingCount
  },
  {
    label: '结果审核',
    name: HrmPerformanceStageType.RESULT_AUDIT,
    count: props.taskCount.resultAuditPendingCount
  },
  {
    label: '结果确认',
    name: HrmPerformanceStageType.RESULT_CONFIRM,
    count: props.taskCount.resultConfirmationPendingCount
  },
  {
    label: '申诉确认',
    name: HrmPerformanceStageType.APPEAL_CONFIRM,
    count: props.taskCount.appealPendingCount
  }
])
</script>

<style scoped>
.performance-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.sub-tabs :deep(.el-tabs__header) {
  margin: 0 0 12px;
}

.sub-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>
