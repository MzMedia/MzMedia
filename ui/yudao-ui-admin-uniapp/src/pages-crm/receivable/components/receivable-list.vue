<template>
  <!-- 回款列表（按客户/合同加载，卡片样式对齐回款列表页） -->
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
      empty-view-text="暂无回款"
      @query="queryList"
    >
      <view class="p-24rpx pb-160rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.no || '-' }}
            </view>
            <view class="flex flex-wrap justify-end gap-8rpx">
              <dict-tag v-if="item.auditStatus != null" :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="item.auditStatus" />
              <dict-tag v-if="item.returnType != null" :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE" :value="item.returnType" />
            </view>
          </view>
          <view v-if="item.price != null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.price) }}
          </view>
          <view v-if="item.returnTime" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">日期：</text>{{ formatDate(item.returnTime) }}
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getReceivablePageByCustomer } from '@/api/crm/receivable'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate } from '@/utils/date'
import { formatMoney } from '@/utils/format'

const props = defineProps<{ customerId?: number, contractId?: number }>()
const list = ref<Record<string, any>[]>([]) // 回款列表
const pagingRef = ref<any>() // 分页组件引用

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!props.customerId && !props.contractId) {
    pagingRef.value?.complete([])
    return
  }
  try {
    const data = await getReceivablePageByCustomer({
      pageNo,
      pageSize,
      customerId: props.customerId,
      contractId: props.contractId,
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
  uni.navigateTo({ url: `/pages-crm/receivable/detail/index?id=${item.id}` })
}

/** 新增回款 */
function openAdd() {
  const params = [
    props.customerId ? `customerId=${props.customerId}` : '',
    props.contractId ? `contractId=${props.contractId}` : '',
  ].filter(Boolean).join('&')
  uni.navigateTo({ url: `/pages-crm/receivable/form/index${params ? `?${params}` : ''}` })
}

watch(() => [props.customerId, props.contractId], () => reload())

defineExpose({ reload, getList: reload, openAdd })

/** 初始化 */
onMounted(() => {
  uni.$on('crm:receivable:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('crm:receivable:reload', reload)
})
</script>
