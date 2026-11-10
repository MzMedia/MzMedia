<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="应用详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 详情内容 -->
    <view>
      <wd-cell-group border>
        <wd-cell title="客户端编号" :value="formData?.clientId" />
        <wd-cell title="客户端密钥" :value="formData?.secret" />
        <wd-cell title="应用名" :value="formData?.name" />
        <wd-cell title="应用图标" center>
          <wd-img v-if="formData?.logo" :src="formData.logo" :width="48" :height="48" mode="aspectFill" />
          <text v-else>-</text>
        </wd-cell>
        <wd-cell title="应用描述" :value="formData?.description ?? '-'" />
        <wd-cell title="状态">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="formData?.status" />
        </wd-cell>
        <wd-cell title="访问令牌有效期" :value="`${formData?.accessTokenValiditySeconds} 秒`" />
        <wd-cell title="刷新令牌有效期" :value="`${formData?.refreshTokenValiditySeconds} 秒`" />
        <wd-cell title="授权类型" :value="formData?.authorizedGrantTypes?.join(', ') || '-'" />
        <wd-cell title="授权范围" :value="formData?.scopes?.join(', ') || '-'" />
        <wd-cell title="可重定向 URI" :value="formData?.redirectUris?.join(', ') || '-'" />
        <wd-cell title="自动授权范围" :value="formData?.autoApproveScopes?.join(', ') || '-'" />
        <wd-cell title="权限" :value="formData?.authorities?.join(', ') || '-'" />
        <wd-cell title="资源" :value="formData?.resourceIds?.join(', ') || '-'" />
        <wd-cell title="附加信息" :value="formData?.additionalInformation || '-'" />
        <wd-cell title="创建时间" :value="formatDateTime(formData?.createTime)" />
      </wd-cell-group>
    </view>

    <!-- 底部操作按钮 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          v-if="hasAccessByCodes(['system:oauth2-client:update'])"
          class="flex-1" type="warning" @click="handleEdit"
        >
          编辑
        </wd-button>
        <wd-button
          v-if="hasAccessByCodes(['system:oauth2-client:delete'])"
          class="flex-1" type="danger" :loading="deleting" @click="handleDelete"
        >
          删除
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { OAuth2Client } from '@/api/system/oauth2/client'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { deleteOAuth2Client, getOAuth2Client } from '@/api/system/oauth2/client'
import { useAccess } from '@/hooks/useAccess'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const formData = ref<OAuth2Client>() // 详情数据
const deleting = ref(false) // 删除状态

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/oauth2/index')
}

/** 加载应用详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  try {
    toast.loading('加载中...')
    formData.value = await getOAuth2Client(props.id)
  } finally {
    toast.close()
  }
}

/** 编辑应用 */
function handleEdit() {
  uni.navigateTo({
    url: `/pages-system/oauth2/client/form/index?id=${props.id}`,
  })
}

/** 删除应用 */
async function handleDelete() {
  if (!props.id) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定要删除该应用吗？',
    })
  } catch {
    return
  }
  // 执行删除
  deleting.value = true
  try {
    await deleteOAuth2Client(props.id)
    toast.success('删除成功')
    uni.$emit('system:oauth2-client:reload')
    delay(handleBack)
  } finally {
    deleting.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
  uni.$on('system:oauth2-client:reload', getDetail)
})

/** 卸载 */
onUnload(() => {
  uni.$off('system:oauth2-client:reload', getDetail)
})
</script>
