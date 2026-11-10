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
          <wd-form-item title="公告标题" title-width="180rpx" prop="title">
            <wd-input
              v-model="formData.title"
              clearable
              placeholder="请输入公告标题"
            />
          </wd-form-item>
          <wd-form-item title="公告内容" title-width="180rpx" prop="content">
            <wd-textarea
              v-model="formData.content"
              clearable
              :rows="4"
              placeholder="请输入公告内容"
            />
          </wd-form-item>
          <wd-form-item title="公告类型" title-width="180rpx" prop="type" center>
            <wd-radio-group v-model="formData.type" type="button">
              <wd-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_NOTICE_TYPE)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="公告状态" title-width="180rpx" prop="status" center>
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
          <wd-form-item title="备注" title-width="180rpx" prop="remark">
            <wd-textarea
              v-model="formData.remark"
              placeholder="请输入备注"
              :maxlength="200"
              show-word-limit
              clearable
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
import type { Notice } from '@/api/system/notice'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createNotice, getNotice, updateNotice } from '@/api/system/notice'
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
const getTitle = computed(() => props.id ? '编辑通知公告' : '新增通知公告')
const formLoading = ref(false) // 表单提交状态
const formData = ref<Notice>({
  id: undefined,
  title: '',
  content: '',
  type: undefined,
  status: CommonStatusEnum.ENABLE,
  remark: '',
}) // 表单数据
const formSchema = createFormSchema({
  title: [{ required: true, message: '公告标题不能为空' }],
  content: [{ required: true, message: '公告内容不能为空' }],
  type: [{ required: true, message: '公告类型不能为空' }],
  status: [{ required: true, message: '公告状态不能为空' }],
  remark: [{ max: 200, message: '备注长度不能超过 200 个字符' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/notice/index')
}

/** 加载通知公告详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getNotice(props.id)
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
      await updateNotice(formData.value)
      toast.success('修改成功')
    } else {
      await createNotice(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('system:notice:reload')
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
