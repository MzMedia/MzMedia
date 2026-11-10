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
          <wd-form-item title="任务名称" title-width="200rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入任务名称"
            />
          </wd-form-item>
          <wd-form-item title="处理器名称" title-width="200rpx" prop="handlerName">
            <wd-input
              v-model="formData.handlerName"
              :readonly="!!formData.id"
              clearable
              placeholder="请输入处理器名称"
            />
          </wd-form-item>
          <wd-form-item title="处理器参数" title-width="200rpx" prop="handlerParam">
            <wd-input
              v-model="formData.handlerParam"
              clearable
              placeholder="请输入处理器参数"
            />
          </wd-form-item>
          <wd-form-item title="CRON 表达式" title-width="200rpx" prop="cronExpression">
            <wd-input
              v-model="formData.cronExpression"
              clearable
              placeholder="请输入 CRON 表达式"
            />
          </wd-form-item>
          <wd-form-item title="重试次数" title-width="200rpx" prop="retryCount">
            <wd-input
              v-model.number="formData.retryCount"
              type="number"
              clearable
              placeholder="请输入重试次数"
            />
          </wd-form-item>
          <wd-form-item title="重试间隔(ms)" title-width="200rpx" prop="retryInterval">
            <wd-input
              v-model.number="formData.retryInterval"
              type="number"
              clearable
              placeholder="请输入重试间隔"
            />
          </wd-form-item>
          <wd-form-item title="监控超时(ms)" title-width="200rpx" prop="monitorTimeout">
            <wd-input
              v-model.number="formData.monitorTimeout"
              type="number"
              clearable
              placeholder="请输入监控超时时间"
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
import type { Job } from '@/api/infra/job'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createJob, getJob, updateJob } from '@/api/infra/job'
import { delay, navigateBackPlus } from '@/utils'
import { CommonStatusEnum } from '@/utils/constants/biz-system-enum'
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
const getTitle = computed(() => props.id ? '编辑定时任务' : '新增定时任务')
const formLoading = ref(false) // 表单提交状态
const formData = ref<Job>({
  id: undefined,
  name: '',
  status: CommonStatusEnum.ENABLE,
  handlerName: '',
  handlerParam: '',
  cronExpression: '',
  retryCount: 0,
  retryInterval: 0,
  monitorTimeout: 0,
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '任务名称不能为空' }],
  handlerName: [{ required: true, message: '处理器名称不能为空' }],
  cronExpression: [{ required: true, message: 'CRON 表达式不能为空' }],
  retryCount: [{ required: true, message: '重试次数不能为空' }],
  retryInterval: [{ required: true, message: '重试间隔不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/job/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getJob(props.id)
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
      await updateJob(formData.value)
      toast.success('修改成功')
    } else {
      await createJob(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:job:reload')
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
