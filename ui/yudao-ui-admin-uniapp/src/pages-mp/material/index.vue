<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号素材"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 公众号选择 -->
    <AccountPicker v-model="accountId" @change="handleAccountChange" />

    <!-- 类型切换 -->
    <view class="bg-white">
      <wd-tabs v-model="tabIndex" shrink @change="handleTabChange">
        <wd-tab title="图片" />
        <wd-tab title="语音" />
        <wd-tab title="视频" />
      </wd-tabs>
    </view>

    <!-- 上传操作 -->
    <view class="bg-white px-24rpx py-16rpx">
      <wd-button
        v-if="hasAccessByCodes(['mp:material:upload-permanent'])"
        type="primary"
        block
        :loading="uploading"
        @click="handleUpload"
      >
        {{ uploadButtonText }}
      </wd-button>
    </view>

    <!-- 素材列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号素材数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <template v-if="currentType === 'image'">
            <wd-img
              v-if="item.url"
              :src="item.url"
              class="mb-16rpx"
              width="100%"
              height="360rpx"
              radius="8rpx"
              mode="aspectFit"
              enable-preview
            />
            <view class="text-30rpx text-[#333] font-semibold">
              {{ item.name || item.mediaId || '图片素材' }}
            </view>
          </template>
          <template v-else>
            <view v-if="currentType === 'video' && item.title" class="mb-12rpx text-30rpx text-[#333] font-semibold">
              {{ item.title }}
            </view>
            <view v-if="currentType === 'video' && item.introduction" class="mb-12rpx text-26rpx text-[#666]">
              {{ item.introduction }}
            </view>
            <MediaPreview :type="currentType" :url="item.url" class="mb-12rpx" />
            <view class="mb-8rpx text-24rpx text-[#999]">
              文件名：{{ item.name || '-' }}
            </view>
            <view class="mb-12rpx break-all text-24rpx text-[#999]">
              编号：{{ item.mediaId || '-' }}
            </view>
          </template>
          <view class="mb-20rpx text-24rpx text-[#999]">
            上传时间：{{ formatDateTime(item.createTime) || '-' }}
          </view>
          <wd-button
            v-if="hasAccessByCodes(['mp:material:delete'])"
            block size="small" type="danger" @click="handleDelete(item)"
          >
            删除
          </wd-button>
        </view>
      </view>
    </z-paging>

    <!-- 视频上传信息 -->
    <wd-popup v-model="videoFormVisible" position="bottom" safe-area-inset-bottom>
      <view class="bg-white p-24rpx">
        <view class="mb-24rpx text-center text-32rpx text-[#333] font-semibold">
          新建视频素材
        </view>
        <wd-input v-model="videoForm.title" placeholder="请输入标题" clearable />
        <view class="h-16rpx" />
        <wd-textarea v-model="videoForm.introduction" placeholder="请输入描述" clearable />
        <view class="mt-24rpx flex gap-16rpx">
          <wd-button class="flex-1" variant="plain" @click="videoFormVisible = false">
            取消
          </wd-button>
          <wd-button class="flex-1" type="primary" @click="handleChooseVideo">
            选择视频
          </wd-button>
        </view>
      </view>
    </wd-popup>
  </view>
</template>

<script lang="ts" setup>
import type { Material } from '@/api/mp/material'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, reactive, ref } from 'vue'
import { deletePermanentMaterial, getMaterialPage } from '@/api/mp/material'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'
import AccountPicker from '@/pages-mp/account/components/account-picker.vue'
import MediaPreview from '@/pages-mp/components/media-preview.vue'
import { useMaterialUpload } from '@/pages-mp/utils/upload'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

type MaterialUploadType = 'image' | 'voice' | 'video'

const typeList: MaterialUploadType[] = ['image', 'voice', 'video']
const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const { uploading, chooseAndUpload } = useMaterialUpload()
const accountId = ref<number>() // 当前公众号编号
const tabIndex = ref(0) // 素材类型索引
const list = ref<Material[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const videoFormVisible = ref(false) // 视频表单弹窗
const videoForm = reactive({
  title: '',
  introduction: '',
}) // 视频上传信息

const currentType = computed(() => typeList[tabIndex.value])
const uploadButtonText = computed(() => {
  if (currentType.value === 'image') {
    return '上传图片'
  }
  if (currentType.value === 'voice') {
    return '上传语音'
  }
  return '新建视频'
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  reload()
}

/** Tab 切换 */
function handleTabChange({ index }: { index: number }) {
  tabIndex.value = index
  list.value = []
  reload()
}

/** 查询素材列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!accountId.value) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const data = await getMaterialPage({
      accountId: accountId.value,
      permanent: true,
      type: currentType.value,
      pageNo,
      pageSize,
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

/** 上传素材 */
async function handleUpload() {
  if (!accountId.value) {
    toast.show('请先选择公众号')
    return
  }
  // 视频需先在弹窗收集标题与描述
  if (currentType.value === 'video') {
    videoForm.title = ''
    videoForm.introduction = ''
    videoFormVisible.value = true
    return
  }
  const material = await chooseAndUpload(currentType.value, {
    accountId: accountId.value,
    permanent: true,
  })
  if (material) {
    toast.success('上传成功')
    reload()
  }
}

/** 选择并上传视频 */
async function handleChooseVideo() {
  if (!accountId.value) {
    return
  }
  if (!videoForm.title || !videoForm.introduction) {
    toast.show('请输入标题和描述')
    return
  }
  videoFormVisible.value = false
  const material = await chooseAndUpload('video', {
    accountId: accountId.value,
    permanent: true,
    title: videoForm.title,
    introduction: videoForm.introduction,
  })
  if (material) {
    toast.success('上传成功')
    reload()
  }
}

/** 删除素材 */
async function handleDelete(item: Material) {
  try {
    await dialog.confirm({
      title: '提示',
      msg: '此操作将永久删除该素材，是否继续？',
    })
  } catch {
    return
  }
  await deletePermanentMaterial(item.id)
  toast.success('删除成功')
  reload()
}
</script>
