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
          <wd-form-item title="邮箱" title-width="220rpx" prop="mail">
            <wd-input
              v-model="formData.mail"
              clearable
              placeholder="请输入邮箱"
            />
          </wd-form-item>
          <wd-form-item title="用户名" title-width="220rpx" prop="username">
            <wd-input
              v-model="formData.username"
              clearable
              placeholder="请输入用户名"
            />
          </wd-form-item>
          <wd-form-item title="密码" title-width="220rpx" prop="password">
            <wd-input
              v-model="formData.password"
              clearable
              placeholder="请输入密码"
              show-password
            />
          </wd-form-item>
          <wd-form-item title="SMTP 服务器域名" title-width="220rpx" prop="host">
            <wd-input
              v-model="formData.host"
              clearable
              placeholder="请输入 SMTP 服务器域名"
            />
          </wd-form-item>
          <wd-form-item title="SMTP 服务器端口" title-width="220rpx" prop="port" center>
            <wd-input-number v-model="formData.port" :min="1" :max="65535" />
          </wd-form-item>
          <wd-form-item title="是否开启 SSL" title-width="220rpx" prop="sslEnable" center>
            <wd-radio-group v-model="formData.sslEnable" type="button">
              <wd-radio
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="String(dict.value)"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="是否开启 STARTTLS" title-width="220rpx" prop="starttlsEnable" center>
            <wd-radio-group v-model="formData.starttlsEnable" type="button">
              <wd-radio
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="String(dict.value)"
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
import type { MailAccount } from '@/api/system/mail/account'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createMailAccount, getMailAccount, updateMailAccount } from '@/api/system/mail/account'
import { getBoolDictOptions } from '@/hooks/useDict'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
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
const getTitle = computed(() => props.id ? '编辑邮箱账号' : '新增邮箱账号')
const formLoading = ref(false) // 表单提交状态
const formData = ref<MailAccount>({
  id: undefined,
  mail: '',
  username: '',
  password: '',
  host: '',
  port: 465,
  sslEnable: true,
  starttlsEnable: false,
}) // 表单数据
const formSchema = createFormSchema({
  mail: [{ required: true, message: '邮箱不能为空' }, { type: 'email', message: '请输入正确的邮箱地址' }],
  username: [{ required: true, message: '用户名不能为空' }],
  password: [{ required: true, message: '密码不能为空' }],
  host: [{ required: true, message: 'SMTP 服务器域名不能为空' }],
  port: [{ required: true, message: 'SMTP 服务器端口不能为空' }],
  sslEnable: [{ required: true, message: '是否开启 SSL 不能为空' }],
  starttlsEnable: [{ required: true, message: '是否开启 STARTTLS 不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/mail/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getMailAccount(props.id)
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
      await updateMailAccount(formData.value)
      toast.success('修改成功')
    } else {
      await createMailAccount(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('system:mail-account:reload')
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
