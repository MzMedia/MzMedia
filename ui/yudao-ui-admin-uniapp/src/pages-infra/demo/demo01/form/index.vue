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
          <wd-form-item title="名字" title-width="200rpx" prop="name">
            <wd-input v-model="formData.name" clearable placeholder="请输入名字" />
          </wd-form-item>
          <wd-form-item title="性别" title-width="200rpx" prop="sex" center>
            <wd-radio-group v-model="formData.sex" type="button">
              <wd-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item
            title="出生年"
            title-width="200rpx"
            prop="birthday"
            is-link
            :value="formData.birthday ? formatDate(formData.birthday) : ''"
            placeholder="请选择出生年"
            @click="pickerVisible.birthday = true"
          />
          <wd-datetime-picker
            v-model="formData.birthday"
            v-model:visible="pickerVisible.birthday"
            title="请选择出生年"
            type="date"
          />
          <wd-form-item title="简介" title-width="200rpx" prop="description">
            <wd-textarea v-model="formData.description" clearable placeholder="请输入简介" />
          </wd-form-item>
          <wd-form-item title="头像" title-width="200rpx" prop="avatar">
            <yd-upload-img v-model="formData.avatar" directory="demo01" />
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
import type { Demo01Contact } from '@/api/infra/demo/demo01'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, reactive, ref } from 'vue'
import { createDemo01Contact, getDemo01Contact, updateDemo01Contact } from '@/api/infra/demo/demo01'
import { getIntDictOptions } from '@/hooks/useDict'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate } from '@/utils/date'
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
const getTitle = computed(() => props.id ? '编辑示例联系人' : '新增示例联系人')
const formLoading = ref(false) // 表单提交状态
const pickerVisible = reactive({ birthday: false }) // 日期选择器显示状态
const formData = ref<Demo01Contact>({
  id: undefined,
  name: '',
  sex: undefined,
  birthday: undefined,
  description: '',
  avatar: '',
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '名字不能为空' }],
  sex: [{ required: true, message: '性别不能为空' }],
  birthday: [{ required: true, message: '出生年不能为空' }],
  description: [{ required: true, message: '简介不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/demo/demo01/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getDemo01Contact(props.id)
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
      await updateDemo01Contact(formData.value)
      toast.success('修改成功')
    } else {
      await createDemo01Contact(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:demo01-contact:reload')
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
