<template>
  <!-- 客户下的商机列表（卡片样式对齐商机列表页） -->
  <view class="min-h-0 flex flex-1 flex-col">
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无商机"
      @query="queryList"
    >
      <view class="p-24rpx pb-160rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name || '-' }}
            </view>
            <wd-tag v-if="item.statusName" type="primary" variant="plain">
              {{ item.statusName }}
            </wd-tag>
          </view>
          <view v-if="item.totalPrice != null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.totalPrice) }}
          </view>
          <view v-if="item.ownerUserName" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">负责人：</text>{{ item.ownerUserName }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getBusinessPageByCustomer } from '@/api/crm/business'
import { formatMoney } from '@/utils/format'

const props = defineProps<{ customerId: number }>()
const list = ref<Record<string, any>[]>([]) // 商机列表
const pagingRef = ref<any>() // 分页组件引用

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!props.customerId) {
    pagingRef.value?.complete([])
    return
  }
  try {
    const data = await getBusinessPageByCustomer({
      pageNo,
      pageSize,
      customerId: props.customerId,
    })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 查看详情 */
function handleDetail(item: Record<string, any>) {
  uni.navigateTo({ url: `/pages-crm/business/detail/index?id=${item.id}` })
}

/** 新增商机 */
function openAdd() {
  uni.navigateTo({ url: `/pages-crm/business/form/index?customerId=${props.customerId}` })
}

watch(() => props.customerId, () => reload())

defineExpose({ reload, getList: reload, openAdd })

/** 初始化 */
onMounted(() => {
  uni.$on('crm:business:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('crm:business:reload', reload)
})
</script>
