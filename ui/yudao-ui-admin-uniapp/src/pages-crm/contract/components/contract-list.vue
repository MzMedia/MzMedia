<template>
  <!-- 合同列表（按客户/商机加载，卡片样式对齐合同列表页） -->
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
      empty-view-text="暂无合同"
      @query="queryList"
    >
      <view class="p-24rpx pb-160rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name || '-' }}
            </view>
            <dict-tag v-if="item.auditStatus != null" :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="item.auditStatus" />
          </view>
          <view v-if="item.no" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">编号：</text>{{ item.no }}
          </view>
          <view v-if="item.totalPrice != null" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.totalPrice) }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getContractPageByBusiness, getContractPageByCustomer } from '@/api/crm/contract'
import { DICT_TYPE } from '@/utils/constants'
import { formatMoney } from '@/utils/format'

const props = defineProps<{ customerId?: number, businessId?: number }>()
const list = ref<Record<string, any>[]>([]) // 合同列表
const pagingRef = ref<any>() // 分页组件引用

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!props.customerId && !props.businessId) {
    pagingRef.value?.complete([])
    return
  }
  try {
    const data = props.businessId
      ? await getContractPageByBusiness({
          pageNo,
          pageSize,
          businessId: props.businessId,
        })
      : await getContractPageByCustomer({
          pageNo,
          pageSize,
          customerId: props.customerId!,
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
  uni.navigateTo({ url: `/pages-crm/contract/detail/index?id=${item.id}` })
}

/** 新增合同 */
function openAdd() {
  const params = [
    props.customerId ? `customerId=${props.customerId}` : '',
    props.businessId ? `businessId=${props.businessId}` : '',
  ].filter(Boolean).join('&')
  uni.navigateTo({ url: `/pages-crm/contract/form/index${params ? `?${params}` : ''}` })
}

watch(() => [props.customerId, props.businessId], () => reload())

defineExpose({ reload, getList: reload, openAdd })

/** 初始化 */
onMounted(() => {
  uni.$on('crm:contract:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('crm:contract:reload', reload)
})
</script>
