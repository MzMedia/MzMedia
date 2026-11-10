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
          <wd-form-item title="参数分类" title-width="200rpx" prop="category">
            <wd-input
              v-model="formData.category"
              clearable
              placeholder="请输入参数分类"
            />
          </wd-form-item>
          <wd-form-item title="参数名称" title-width="200rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入参数名称"
            />
          </wd-form-item>
          <wd-form-item title="参数键名" title-width="200rpx" prop="key">
            <wd-input
              v-model="formData.key"
              clearable
              placeholder="请输入参数键名"
            />
          </wd-form-item>
          <wd-form-item title="参数键值" title-width="200rpx" prop="value">
            <wd-input
              v-model="formData.value"
              clearable
              placeholder="请输入参数键值"
            />
          </wd-form-item>
          <wd-form-item title="是否可见" title-width="200rpx" prop="visible" center>
            <wd-radio-group v-model="formData.visible" type="button">
              <wd-radio
                v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
                :key="`${dict.value}`"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="备注" title-width="200rpx" prop="remark">
            <wd-textarea
              v-model="formData.remark"
              clearable
              placeholder="请输入备注"
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
import type { Config } from '@/api/infra/config'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createConfig, getConfig, updateConfig } from '@/api/infra/config'
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
const getTitle = computed(() => props.id ? '编辑参数配置' : '新增参数配置')
const formLoading = ref(false) // 表单提交状态
const formData = ref<Config>({
  id: undefined,
  category: '',
  name: '',
  key: '',
  value: '',
  type: undefined,
  visible: true,
  remark: '',
}) // 表单数据
const formSchema = createFormSchema({
  category: [{ required: true, message: '参数分类不能为空' }],
  name: [{ required: true, message: '参数名称不能为空' }],
  key: [{ required: true, message: '参数键名不能为空' }],
  value: [{ required: true, message: '参数键值不能为空' }],
  visible: [{ required: true, message: '是否可见不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/config/index')
}

/** 加载参数配置详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getConfig(props.id)
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
      await updateConfig(formData.value)
      toast.success('修改成功')
    } else {
      await createConfig(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:config:reload')
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
