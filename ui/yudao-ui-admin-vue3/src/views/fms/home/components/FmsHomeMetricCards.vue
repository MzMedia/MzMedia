<template>
  <div>
    <div
      class="flex items-center justify-between border-b border-[var(--el-border-color-lighter)] pb-14px"
    >
      <div class="text-18px font-600 text-[var(--el-text-color-primary)]">财务指标</div>
      <div class="text-13px text-[var(--el-text-color-secondary)]">
        {{ home?.currentMonth }} 当期数据
      </div>
    </div>

    <div class="mt-24px flex items-center gap-12px">
      <el-button circle :disabled="!canScrollLeft" @click="scrollCards(-1)">
        <Icon icon="ep:arrow-left" />
      </el-button>
      <div
        ref="cardScrollerRef"
        class="metric-card-scroller flex min-w-0 flex-1 gap-14px overflow-x-auto py-4px"
        @scroll="updateScrollState"
      >
        <button
          v-for="metric in home?.metrics || []"
          :key="metric.key"
          class="h-108px w-220px flex-none border border-[var(--el-border-color-lighter)] rounded-6px bg-[var(--el-bg-color)] px-20px text-left shadow-sm transition-all hover:border-[var(--el-color-primary)]"
          :class="{
            'border-[var(--el-color-primary)] bg-[var(--el-color-primary)] text-white shadow-md':
              selectedMetricKey === metric.key
          }"
          type="button"
          @click="emit('select', metric)"
        >
          <div class="truncate text-15px font-600">{{ metric.name }}</div>
          <div class="mt-10px truncate text-22px font-600">{{ formatAmount(metric.amount) }}</div>
        </button>
      </div>
      <el-button circle :disabled="!canScrollRight" @click="scrollCards(1)">
        <Icon icon="ep:arrow-right" />
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { FmsHomeMetricVO, FmsHomeVO } from '@/api/fms/home'
import { formatAmount } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsHomeMetricCards' })

const props = defineProps<{
  home?: FmsHomeVO
  selectedMetricKey?: string
}>()

const emit = defineEmits<{
  select: [metric: FmsHomeMetricVO]
}>()

// TODO DONE @AI：变量注释呀。
const cardScrollerRef = ref<HTMLElement>() // 指标卡片横向滚动容器
const canScrollLeft = ref(false) // 是否可以向左滚动
const canScrollRight = ref(false) // 是否可以向右滚动
const CARD_SCROLL_OFFSET = 468 // 单次滚动的卡片组宽度（两个卡片宽度 + 间距）

watch(
  () => props.home?.metrics,
  () => nextTick(updateScrollState),
  { immediate: true }
)

/** 更新左右滚动按钮的可用状态 */
function updateScrollState() {
  const scroller = cardScrollerRef.value
  if (!scroller) return
  canScrollLeft.value = scroller.scrollLeft > 1
  canScrollRight.value = scroller.scrollLeft + scroller.clientWidth < scroller.scrollWidth - 1
}

/** 按一个卡片组的宽度滚动指标卡片 */
function scrollCards(direction: -1 | 1) {
  cardScrollerRef.value?.scrollBy({ left: direction * CARD_SCROLL_OFFSET, behavior: 'smooth' })
}
</script>

<style scoped>
.metric-card-scroller {
  scrollbar-width: none;
}

.metric-card-scroller::-webkit-scrollbar {
  display: none;
}
</style>
