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
          <wd-form-item title="数据源名称" title-width="200rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入数据源名称"
            />
          </wd-form-item>
          <wd-form-item title="数据源连接" title-width="200rpx" prop="url">
            <wd-input
              v-model="formData.url"
              clearable
              placeholder="请输入数据源连接"
            />
          </wd-form-item>
          <wd-form-item title="用户名" title-width="200rpx" prop="username">
            <wd-input
              v-model="formData.username"
              clearable
              placeholder="请输入用户名"
            />
          </wd-form-item>
          <wd-form-item title="密码" title-width="200rpx" prop="password">
            <wd-input
              v-model="formData.password"
              show-password
              clearable
              :placeholder="props.id ? '编辑需重新输入密码' : '请输入密码'"
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
import type { DataSourceConfig } from '@/api/infra/data-source-config'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createDataSourceConfig, getDataSourceConfig, updateDataSourceConfig } from '@/api/infra/data-source-config'
import { delay, navigateBackPlus } from '@/utils'
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
const getTitle = computed(() => props.id ? '编辑数据源' : '新增数据源')
const formLoading = ref(false) // 表单提交状态
const formData = ref<DataSourceConfig>({
  id: undefined,
  name: '',
  url: '',
  username: '',
  password: '',
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '数据源名称不能为空' }],
  url: [{ required: true, message: '数据源连接不能为空' }],
  username: [{ required: true, message: '用户名不能为空' }],
  password: [{ required: true, message: '密码不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/data-source-config/index')
}

/** 加载数据源详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  try {
    toast.loading('加载中')
    formData.value = await getDataSourceConfig(props.id)
  } finally {
    toast.close()
  }
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
      await updateDataSourceConfig(formData.value)
      toast.success('修改成功')
    } else {
      await createDataSourceConfig(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:data-source-config:reload')
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
