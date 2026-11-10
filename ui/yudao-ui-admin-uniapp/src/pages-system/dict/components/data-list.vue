<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <DataSearchForm :dict-type="dictType" @search="handleQuery" @reset="handleReset" />

    <!-- 字典数据列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无字典数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="p-24rpx">
            <view class="mb-16rpx flex items-center justify-between">
              <view class="text-32rpx text-[#333] font-semibold">
                {{ item.label }}
              </view>
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">字典键值：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.value }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">字典排序：</text>
              <text>{{ item.sort }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">颜色类型：</text>
              <wd-tag v-if="item.colorType" :type="getTagType(item.colorType)">
                {{ item.colorType }}
              </wd-tag>
              <text v-else>-</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">CSS Class：</text>
              <view v-if="item.cssClass" class="rounded-4rpx px-8rpx py-2rpx text-24rpx text-white" :style="{ backgroundColor: item.cssClass }">
                {{ item.cssClass }}
              </view>
              <text v-else>-</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['system:dict:create']) && dictType"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { TagType } from '@wot-ui/ui/components/wd-tag/types'
import type { DictData } from '@/api/system/dict/data'
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getDictDataPage } from '@/api/system/dict/data'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import DataSearchForm from './data-search-form.vue'

const props = defineProps<{
  dictType?: string
}>()

const { hasAccessByCodes } = useAccess()
const list = ref<DictData[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 颜色类型 => wd-tag 的 type 映射，和 src/components/dict-tag/dict-tag.vue 是一致的 */
const COLOR_TYPE_MAP: Record<string, TagType> = {
  default: 'default',
  primary: 'primary',
  success: 'success',
  info: 'default', // wd-tag 无 info，映射为 default
  warning: 'warning',
  danger: 'danger',
}

/** 获取标签类型 */
function getTagType(colorType: string): TagType {
  return COLOR_TYPE_MAP[colorType] || 'default'
}

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      dictType: queryParams.value.dictType || props.dictType,
      pageNo,
      pageSize,
    }
    const data = await getDictDataPage(params)
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 搜索按钮操作 */
function handleQuery(data?: Record<string, any>) {
  queryParams.value = { ...data }
  reload()
}

/** 重置按钮操作 */
function handleReset() {
  handleQuery()
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增 */
function handleAdd() {
  uni.navigateTo({
    url: `/pages-system/dict/data/form/index?dictType=${props.dictType}`,
  })
}

/** 查看详情 */
function handleDetail(item: DictData) {
  uni.navigateTo({
    url: `/pages-system/dict/data/detail/index?id=${item.id}`,
  })
}

/** 监听 dictType 变化，重新查询 */
watch(
  () => props.dictType,
  () => {
    if (props.dictType) {
      reload()
    }
  },
)

/** 初始化 */
onMounted(() => {
  uni.$on('system:dict-data:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('system:dict-data:reload', reload)
})
</script>
