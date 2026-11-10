<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="会员配置"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 表单区域 -->
    <view>
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group border title="积分配置">
          <wd-form-item title="积分抵扣" title-width="220rpx" prop="pointTradeDeductEnable" center>
            <wd-switch v-model="formData.pointTradeDeductEnable" />
          </wd-form-item>
          <wd-form-item title="抵扣金额" title-width="220rpx" prop="pointTradeDeductUnitPrice">
            <wd-input-number v-model="deductUnitPriceYuan" :min="0" :step="0.01" :precision="2" input-type="number" />
          </wd-form-item>
          <wd-form-item title="抵扣上限" title-width="220rpx" prop="pointTradeDeductMaxPrice">
            <wd-input-number v-model="formData.pointTradeDeductMaxPrice" :min="0" />
          </wd-form-item>
          <wd-form-item title="赠送积分" title-width="220rpx" prop="pointTradeGivePoint">
            <wd-input-number v-model="formData.pointTradeGivePoint" :min="0" />
          </wd-form-item>
        </wd-cell-group>
      </wd-form>
      <view class="p-24rpx text-24rpx text-[#999] leading-40rpx">
        抵扣金额单位为元，表示 1 积分可抵扣多少金额；抵扣上限为 0 时不限制；赠送积分表示实际支付 1 元赠送多少积分。
      </view>
    </view>

    <!-- 底部保存按钮 -->
    <view class="yd-detail-footer">
      <wd-button
        v-if="hasAccessByCodes(['member:config:save'])"
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
import type { MemberConfig } from '@/api/member/config'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { getMemberConfig, saveMemberConfig } from '@/api/member/config'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { createFormSchema } from '@/utils/wot'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const formLoading = ref(false) // 表单提交状态
const formData = ref<MemberConfig>({
  id: undefined,
  pointTradeDeductEnable: true,
  pointTradeDeductUnitPrice: 0,
  pointTradeDeductMaxPrice: 0,
  pointTradeGivePoint: 0,
}) // 表单数据
const deductUnitPriceYuan = computed({
  get: () => Number((formData.value.pointTradeDeductUnitPrice / 100).toFixed(2)),
  set: (value: number) => {
    formData.value.pointTradeDeductUnitPrice = Math.round(Number(value || 0) * 100)
  },
}) // 抵扣金额元
const formSchema = createFormSchema({
  pointTradeDeductEnable: [{ required: true, message: '积分抵扣开关不能为空' }],
  pointTradeDeductUnitPrice: [{ required: true, message: '积分抵扣金额不能为空' }],
  pointTradeDeductMaxPrice: [{ required: true, message: '积分抵扣上限不能为空' }],
  pointTradeGivePoint: [{ required: true, message: '赠送积分不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 加载会员配置 */
async function getDetail() {
  const data = await getMemberConfig()
  if (data) {
    formData.value = data
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
    await saveMemberConfig(formData.value)
    toast.success('保存成功')
  } finally {
    formLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
})
</script>
