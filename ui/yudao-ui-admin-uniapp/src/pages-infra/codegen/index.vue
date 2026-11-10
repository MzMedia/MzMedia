<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="代码生成"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 代码生成列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无代码生成数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-16rpx flex items-center justify-between">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.tableName }}
            </view>
            <dict-tag class="ml-16rpx shrink-0" :type="DICT_TYPE.INFRA_CODEGEN_TEMPLATE_TYPE" :value="item.templateType" />
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">表描述：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.tableComment || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">实体类：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.className || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">模块/业务：</text>
            <text class="min-w-0 flex-1 truncate">{{ item.moduleName }} / {{ item.businessName }}</text>
          </view>
          <view class="flex items-center justify-between text-28rpx text-[#666]">
            <view class="flex items-center">
              <text class="mr-8rpx shrink-0 text-[#999]">场景：</text>
              <dict-tag :type="DICT_TYPE.INFRA_CODEGEN_SCENE" :value="item.scene" />
            </view>
            <text class="text-24rpx text-[#999]">{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 导入按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:codegen:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleImport"
    />
  </view>
</template>

<script lang="ts" setup>
import type { CodegenTable } from '@/api/infra/codegen'
import { onMounted, onUnmounted, ref } from 'vue'
import { getCodegenTablePage } from '@/api/infra/codegen'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<CodegenTable[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询代码生成列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getCodegenTablePage(params)
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

/** 查看详情 */
function handleDetail(item: CodegenTable) {
  uni.navigateTo({
    url: `/pages-infra/codegen/detail/index?id=${item.id}`,
  })
}

/** 导入表 */
function handleImport() {
  uni.navigateTo({
    url: '/pages-infra/codegen/import/index',
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('infra:codegen:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:codegen:reload', reload)
})
</script>
