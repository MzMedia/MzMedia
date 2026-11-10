<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      :title="getTitle"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 表单区域 -->
    <view>
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group border>
          <wd-form-item title="应用名" title-width="200rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入应用名"
            />
          </wd-form-item>
          <yd-form-picker
            v-model="formData.socialType"
            label="社交平台"
            prop="socialType"
            :dict-type="DICT_TYPE.SYSTEM_SOCIAL_TYPE"
            placeholder="请选择社交平台"
          />
          <wd-form-item title="用户类型" title-width="200rpx" prop="userType" center>
            <wd-radio-group v-model="formData.userType" type="button">
              <wd-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.USER_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="应用编号" title-width="200rpx" prop="clientId">
            <wd-input
              v-model="formData.clientId"
              clearable
              placeholder="请输入应用编号，对应各平台的 appKey"
            />
          </wd-form-item>
          <wd-form-item title="应用密钥" title-width="200rpx" prop="clientSecret">
            <wd-input
              v-model="formData.clientSecret"
              clearable
              placeholder="请输入应用密钥，对应各平台的 appSecret"
            />
          </wd-form-item>
          <wd-form-item v-if="formData.socialType === 30" title="agentId" title-width="200rpx" prop="agentId">
            <wd-input
              v-model="formData.agentId"
              clearable
              placeholder="授权方的网页应用 ID，有则填"
            />
          </wd-form-item>
          <wd-form-item v-if="formData.socialType === 40" title="publicKey" title-width="200rpx" prop="publicKey">
            <wd-input
              v-model="formData.publicKey"
              clearable
              placeholder="请输入 publicKey 公钥"
            />
          </wd-form-item>
          <wd-form-item title="状态" title-width="200rpx" prop="status" center>
            <wd-radio-group v-model="formData.status" type="button">
              <wd-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
        </wd-cell-group>
      </wd-form>
    </view>

    <!-- 底部保存按钮 -->
    <view class="yd-detail-footer">
      <wd-button
        type="primary"
        block
        :loading="formLoading"
        @click="handleSubmit"
      >
        保存
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import type { SocialClient } from '@/api/system/social/client'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createSocialClient, getSocialClient, updateSocialClient } from '@/api/system/social/client'
import { getIntDictOptions } from '@/hooks/useDict'
import { delay, navigateBackPlus } from '@/utils'
import { CommonStatusEnum, DICT_TYPE } from '@/utils/constants'
import { createFormSchema } from '@/utils/wot'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const getTitle = computed(() => props.id ? '编辑三方应用' : '新增三方应用')
const formLoading = ref(false) // 表单提交状态
const formData = ref<SocialClient>({
  id: undefined,
  name: '',
  socialType: undefined,
  userType: undefined,
  clientId: '',
  clientSecret: '',
  agentId: '',
  publicKey: '',
  status: CommonStatusEnum.ENABLE,
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '应用名不能为空' }],
  socialType: [{ required: true, message: '社交平台不能为空' }],
  userType: [{ required: true, message: '用户类型不能为空' }],
  clientId: [{ required: true, message: '应用编号不能为空' }],
  clientSecret: [{ required: true, message: '应用密钥不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/social/index')
}

/** 加载三方应用详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getSocialClient(props.id)
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  formLoading.value = true
  try {
    if (props.id) {
      await updateSocialClient(formData.value)
      toast.success('修改成功')
    } else {
      await createSocialClient(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('system:social-client:reload')
    delay(handleBack)
  } finally {
    formLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
})
</script>
