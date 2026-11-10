<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="用户分组管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 用户分组列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无用户分组数据"
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
              <text class="mr-8rpx shrink-0 text-[#999]">描述：</text>
              <text class="min-w-0 flex-1 truncate">{{ item.description || '-' }}</text>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx shrink-0 text-[#999]">成员：</text>
              <view class="min-w-0 flex flex-1 flex-wrap gap-8rpx">
                <wd-tag
                  v-for="userId in (item.userIds || []).slice(0, 3)"
                  :key="userId"
                  type="primary"
                  variant="plain"
                >
                  {{ getUserNickname(userId) }}
                </wd-tag>
                <wd-tag v-if="(item.userIds || []).length > 3" type="default" variant="plain">
                  +{{ (item.userIds || []).length - 3 }}
                </wd-tag>
              </view>
            </view>
            <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
              <text class="mr-8rpx text-[#999]">创建时间：</text>
              <text class="line-clamp-1">{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['bpm:user-group:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { UserGroup } from '@/api/bpm/user-group'
import type { User } from '@/api/system/user'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getUserGroupPage } from '@/api/bpm/user-group'
import { getSimpleUserList } from '@/api/system/user'
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
const list = ref<UserGroup[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数
const userList = ref<User[]>([]) // 用户昵称回显数据源

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 获取用户昵称 */
function getUserNickname(userId: number) {
  const user = userList.value.find(u => u.id === userId)
  return user?.nickname || userId
}

/** 加载用户列表 */
async function loadUserList() {
  userList.value = await getSimpleUserList()
}

/** 查询用户分组列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getUserGroupPage(params)
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

/** 新增用户分组 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-bpm/user-group/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: UserGroup) {
  uni.navigateTo({
    url: `/pages-bpm/user-group/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  loadUserList()
  uni.$on('bpm:user-group:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('bpm:user-group:reload', reload)
})
</script>
