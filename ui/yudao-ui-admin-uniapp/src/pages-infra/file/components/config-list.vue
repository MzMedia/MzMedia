<template>
  <view class="h-full min-h-0 flex flex-col">
    <!-- 搜索组件 -->
    <ConfigSearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 文件配置列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无文件配置数据"
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
              <view class="flex items-center gap-8rpx">
                <view v-if="item.master" class="rounded-4rpx bg-green-500 px-8rpx py-2rpx text-24rpx text-white">
                  主配置
                </view>
                <dict-tag :type="DICT_TYPE.INFRA_FILE_STORAGE" :value="item.storage" />
              </view>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">配置编号：</text>
              <text>{{ item.id }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">备注：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.remark || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text>{{ formatDateTime(item.createTime) }}</text>
            </view>
            <!-- 操作按钮 -->
            <view class="mt-16rpx flex justify-end gap-16rpx">
              <wd-button
                v-if="hasAccessByCodes(['infra:file-config:update']) && !item.master"
                size="small" type="info" @click.stop="handleMaster(item)"
              >
                设为主配置
              </wd-button>
              <wd-button
                v-if="hasAccessByCodes(['infra:file-config:query'])"
                size="small" type="info" @click.stop="handleTest(item)"
              >
                测试
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['infra:file-config:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { FileConfig } from '@/api/infra/file/config'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, onUnmounted, ref } from 'vue'
import { getFileConfigPage, testFileConfig, updateFileConfigMaster } from '@/api/infra/file/config'
import { useAccess } from '@/hooks/useAccess'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import ConfigSearchForm from './config-search-form.vue'

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const list = ref<FileConfig[]>([]) // 列表数据
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
    const data = await getFileConfigPage(params)
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
    url: '/pages-infra/file/config/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: FileConfig) {
  uni.navigateTo({
    url: `/pages-infra/file/config/detail/index?id=${item.id}`,
  })
}

/** 测试文件配置 */
async function handleTest(item: FileConfig) {
  let url: string
  try {
    toast.loading('测试上传中...')
    url = await testFileConfig(item.id!)
  } finally {
    toast.close()
  }
  if (!url) {
    return
  }

  try {
    await dialog.confirm({
      title: '测试上传成功',
      msg: '是否复制该文件链接？',
    })
  } catch {
    return
  }

  // 复制链接到剪贴板
  uni.setClipboardData({
    data: url,
    success: () => {
      uni.hideToast()
      toast.success('链接已复制，请在浏览器中打开')
    },
  })
}

/** 设为主配置 */
async function handleMaster(item: FileConfig) {
  try {
    await dialog.confirm({
      title: '提示',
      msg: `是否要将"${item.name}"设为主配置？`,
    })
  } catch {
    return
  }

  try {
    toast.loading('设置中...')
    await updateFileConfigMaster(item.id!)
    toast.success('设置成功')
    // 刷新列表
    reload()
  } catch {
    toast.close()
  }
}

/** 初始化 */
onMounted(() => {
  uni.$on('infra:file-config:reload', reload)
})

/** 卸载 */
onUnmounted(() => {
  uni.$off('infra:file-config:reload', reload)
})
</script>
