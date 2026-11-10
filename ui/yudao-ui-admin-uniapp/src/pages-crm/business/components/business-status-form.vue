<template>
  <wd-popup
    v-model="visible"
    position="bottom"
    safe-area-inset-bottom
    custom-style="border-radius: 24rpx 24rpx 0 0;"
  >
    <view class="max-h-[80vh] overflow-y-auto bg-white pb-32rpx">
      <view class="flex items-center justify-between border-b border-[#f5f5f5] px-24rpx py-24rpx">
        <view class="text-32rpx text-[#333] font-semibold">
          变更商机状态
        </view>
        <wd-icon name="close" size="36rpx" @click="visible = false" />
      </view>
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group border>
          <wd-form-item title="商机阶段" title-width="220rpx" prop="status" center>
            <wd-radio-group v-model="formData.status" type="button">
              <wd-radio
                v-for="item in statusList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.name }}({{ item.percent || 0 }}%)
              </wd-radio>
              <wd-radio
                v-for="item in DEFAULT_STATUSES"
                :key="item.endStatus"
                :value="-Number(item.endStatus)"
              >
                {{ item.name }}({{ item.percent || 0 }}%)
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
        </wd-cell-group>
      </wd-form>
      <view class="p-24rpx">
        <wd-button
          type="primary"
          block
          :loading="formLoading"
          @click="handleSubmit"
        >
          确定
        </wd-button>
      </view>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import type { Business } from '@/api/crm/business'
import type { BusinessStatus } from '@/api/crm/business/status'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import { updateBusinessStatus } from '@/api/crm/business'
import { DEFAULT_STATUSES, getBusinessStatusSimpleList } from '@/api/crm/business/status'
import { createFormSchema } from '@/utils/wot'

const emit = defineEmits<{
  success: []
}>()

const toast = useToast()
const visible = ref(false) // 弹窗显示状态
const formLoading = ref(false) // 表单提交状态
const formRef = ref<FormInstance>() // 表单组件引用
const statusList = ref<BusinessStatus[]>([]) // 商机阶段
const formData = ref({
  id: undefined as number | undefined,
  status: undefined as number | undefined,
}) // 表单数据
const formSchema = createFormSchema({
  status: [{ required: true, message: '商机阶段不能为空' }],
})

/** 打开弹窗 */
async function open(business: Business) {
  formData.value = {
    id: business.id,
    status: business.endStatus != null ? -business.endStatus : business.statusId,
  }
  visible.value = true
  formLoading.value = true
  try {
    statusList.value = business.statusTypeId ? await getBusinessStatusSimpleList(business.statusTypeId) : []
  } finally {
    formLoading.value = false
  }
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid || !formData.value.id || formData.value.status === undefined) {
    return
  }
  formLoading.value = true
  try {
    await updateBusinessStatus({
      id: formData.value.id,
      name: '',
      statusId: formData.value.status > 0 ? formData.value.status : undefined,
      endStatus: formData.value.status < 0 ? -formData.value.status : undefined,
    })
    toast.success('更新商机状态成功')
    visible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

defineExpose({
  open,
})
</script>
