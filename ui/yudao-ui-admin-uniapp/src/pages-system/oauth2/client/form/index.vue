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
          <wd-form-item title="客户端编号" title-width="220rpx" prop="clientId">
            <wd-input
              v-model="formData.clientId"
              clearable
              placeholder="请输入客户端编号"
            />
          </wd-form-item>
          <wd-form-item title="客户端密钥" title-width="220rpx" prop="secret">
            <wd-input
              v-model="formData.secret"
              clearable
              placeholder="请输入客户端密钥"
            />
          </wd-form-item>
          <wd-form-item title="应用名" title-width="220rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入应用名"
            />
          </wd-form-item>
          <wd-form-item title="应用图标" title-width="220rpx" prop="logo">
            <yd-upload-img v-model="formData.logo" directory="oauth2" />
          </wd-form-item>
          <wd-form-item title="应用描述" title-width="220rpx" prop="description">
            <wd-textarea
              v-model="formData.description"
              clearable
              placeholder="请输入应用描述"
            />
          </wd-form-item>
          <wd-form-item title="状态" title-width="220rpx" prop="status" center>
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
          <wd-form-item title="访问令牌有效期" title-width="220rpx" prop="accessTokenValiditySeconds" center>
            <wd-input-number
              v-model="formData.accessTokenValiditySeconds"
              :min="0"
            />
          </wd-form-item>
          <wd-form-item title="刷新令牌有效期" title-width="220rpx" prop="refreshTokenValiditySeconds" center>
            <wd-input-number
              v-model="formData.refreshTokenValiditySeconds"
              :min="0"
            />
          </wd-form-item>
          <wd-form-item title="授权类型" title-width="220rpx" prop="authorizedGrantTypes">
            <wd-checkbox-group v-model="formData.authorizedGrantTypes" type="button">
              <wd-checkbox
                v-for="dict in getStrDictOptions(DICT_TYPE.SYSTEM_OAUTH2_GRANT_TYPE)"
                :key="dict.value"
                :name="dict.value"
              >
                {{ dict.label }}
              </wd-checkbox>
            </wd-checkbox-group>
          </wd-form-item>
          <wd-form-item title="重定向地址" title-width="220rpx" prop="redirectUris">
            <wd-textarea
              v-model="arrayText.redirectUris"
              clearable
              placeholder="请输入重定向地址，多个换行分隔"
              :rows="2"
            />
          </wd-form-item>
          <wd-form-item title="授权范围" title-width="220rpx" prop="scopes">
            <wd-textarea
              v-model="arrayText.scopes"
              clearable
              placeholder="请输入授权范围，多个换行分隔"
              :rows="2"
            />
          </wd-form-item>
          <wd-form-item title="自动授权范围" title-width="220rpx" prop="autoApproveScopes">
            <wd-textarea
              v-model="arrayText.autoApproveScopes"
              clearable
              placeholder="请输入自动授权范围，多个换行分隔"
              :rows="2"
            />
          </wd-form-item>
          <wd-form-item title="权限" title-width="220rpx" prop="authorities">
            <wd-textarea
              v-model="arrayText.authorities"
              clearable
              placeholder="请输入权限，多个换行分隔"
              :rows="2"
            />
          </wd-form-item>
          <wd-form-item title="资源" title-width="220rpx" prop="resourceIds">
            <wd-textarea
              v-model="arrayText.resourceIds"
              clearable
              placeholder="请输入资源，多个换行分隔"
              :rows="2"
            />
          </wd-form-item>
          <wd-form-item title="附加信息" title-width="220rpx" prop="additionalInformation">
            <wd-textarea
              v-model="formData.additionalInformation"
              clearable
              placeholder="请输入附加信息（JSON 格式）"
              :rows="2"
            />
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
import type { OAuth2Client } from '@/api/system/oauth2/client'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createOAuth2Client, getOAuth2Client, updateOAuth2Client } from '@/api/system/oauth2/client'
import { getIntDictOptions, getStrDictOptions } from '@/hooks/useDict'
import { arrayToLines, delay, linesToArray, navigateBackPlus } from '@/utils'
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
const getTitle = computed(() => props.id ? '编辑应用' : '新增应用')
const formLoading = ref(false) // 表单提交状态
const formData = ref<OAuth2Client>({
  id: undefined,
  clientId: '',
  secret: '',
  name: '',
  logo: '',
  description: '',
  status: CommonStatusEnum.ENABLE,
  accessTokenValiditySeconds: 1800,
  refreshTokenValiditySeconds: 43200,
  redirectUris: [],
  authorizedGrantTypes: [],
  scopes: [],
  autoApproveScopes: [],
  authorities: [],
  resourceIds: [],
  additionalInformation: '',
}) // 表单数据
const formSchema = createFormSchema({
  clientId: [{ required: true, message: '客户端编号不能为空' }],
  secret: [{ required: true, message: '客户端密钥不能为空' }],
  name: [{ required: true, message: '应用名不能为空' }],
  logo: [{ required: true, message: '应用图标不能为空' }],
  accessTokenValiditySeconds: [{ required: true, message: '访问令牌有效期不能为空' }],
  refreshTokenValiditySeconds: [{ required: true, message: '刷新令牌有效期不能为空' }],
  authorizedGrantTypes: [{ required: true, message: '授权类型不能为空' }],
  redirectUris: [{ required: true, message: '重定向地址不能为空' }],
  additionalInformation: [{
    validator: (value) => {
      if (!value) {
        return true
      }
      try {
        JSON.parse(String(value))
        return true
      } catch {
        return '附加信息必须是合法的 JSON'
      }
    },
  }],
})
const formRef = ref<FormInstance>() // 表单组件引用
const ARRAY_FIELDS = ['redirectUris', 'scopes', 'autoApproveScopes', 'authorities', 'resourceIds'] as const
const arrayText = ref( // 多值字段文本（换行分隔），键派生自 ARRAY_FIELDS
  Object.fromEntries(ARRAY_FIELDS.map(key => [key, ''])) as Record<typeof ARRAY_FIELDS[number], string>,
)

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/oauth2/index')
}

/** 加载应用详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getOAuth2Client(props.id)
  ARRAY_FIELDS.forEach((key) => {
    arrayText.value[key] = arrayToLines(formData.value[key])
  })
}

/** 提交表单 */
async function handleSubmit() {
  // 多值字段：换行文本转数组（先于校验，确保必填能读到）
  ARRAY_FIELDS.forEach((key) => {
    formData.value[key] = linesToArray(arrayText.value[key])
  })
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  formLoading.value = true
  try {
    if (props.id) {
      await updateOAuth2Client(formData.value)
      toast.success('修改成功')
    } else {
      await createOAuth2Client(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('system:oauth2-client:reload')
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
