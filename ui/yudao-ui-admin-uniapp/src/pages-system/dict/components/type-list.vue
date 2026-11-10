<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <TypeSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 字典类型列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无字典类型数据"
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
                {{ item.name }}
              </view>
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">字典类型：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.type }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">备注：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.remark || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
            <!-- 查看数据按钮 -->
            <view class="flex justify-end -mt-8">
              <wd-button size="small" type="info" @click.stop="handleSelectType(item)">
                字典数据
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['system:dict:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { DictType } from '@/api/system/dict/type'
import { onMounted, onUnmounted, ref } from 'vue'
import { getDictTypePage } from '@/api/system/dict/type'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import TypeSearchForm from './type-search-form.vue'

const emit = defineEmits<{
  select: [dictType: string]
}>()

const { hasAccessByCodes } = useAccess()
const list = ref<DictType[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getDictTypePage(params)
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
    url: '/pages-system/dict/type/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: DictType) {
  uni.navigateTo({
    url: `/pages-system/dict/type/detail/index?id=${item.id}`,
  })
}

/** 选择字典类型，查看数据 */
function handleSelectType(item: DictType) {
  emit('select', item.type)
}

/** 初始化 */
onMounted(() => {
  uni.$on('system:dict-type:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('system:dict-type:reload', reload)
})
</script>
