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
          <wd-form-item title="套餐名" title-width="220rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入套餐名"
            />
          </wd-form-item>
          <wd-form-item title="支付金额(元)" title-width="220rpx" prop="payPrice" center>
            <wd-input-number v-model="formData.payPrice" :min="0" :step="0.01" :precision="2" />
          </wd-form-item>
          <wd-form-item title="赠送金额(元)" title-width="220rpx" prop="bonusPrice" center>
            <wd-input-number v-model="formData.bonusPrice" :min="0" :step="0.01" :precision="2" />
          </wd-form-item>
          <wd-form-item title="开启状态" title-width="220rpx" prop="status" center>
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
import type { PayWalletRechargePackage } from '@/api/pay/wallet/rechargePackage'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import {
  createPayWalletRechargePackage,
  getPayWalletRechargePackage,
  updatePayWalletRechargePackage,
} from '@/api/pay/wallet/rechargePackage'
import { getIntDictOptions } from '@/hooks/useDict'
import { delay, navigateBackPlus } from '@/utils'
import { CommonStatusEnum, DICT_TYPE } from '@/utils/constants'
import { createFormSchema } from '@/utils/wot'
import { fenToYuan, yuanToFen } from '@/utils/format'

interface RechargePackageFormData extends Omit<PayWalletRechargePackage, 'bonusPrice' | 'payPrice'> {
  bonusPrice?: number
  payPrice?: number
}

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
const getTitle = computed(() => props.id ? '编辑充值套餐' : '新增充值套餐')
const formLoading = ref(false) // 表单提交状态
const formData = ref<RechargePackageFormData>({
  id: undefined,
  name: '',
  payPrice: 0,
  bonusPrice: 0,
  status: CommonStatusEnum.ENABLE,
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '套餐名不能为空' }],
  payPrice: [{ required: true, message: '支付金额不能为空' }],
  bonusPrice: [{ required: true, message: '赠送金额不能为空' }],
  status: [{ required: true, message: '开启状态不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-pay/wallet/recharge-package/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  const data = await getPayWalletRechargePackage(props.id)
  formData.value = {
    ...data,
    payPrice: fenToYuan(data.payPrice),
    bonusPrice: fenToYuan(data.bonusPrice),
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
    const data = {
      ...formData.value,
      payPrice: yuanToFen(formData.value.payPrice),
      bonusPrice: yuanToFen(formData.value.bonusPrice),
    }
    if (props.id) {
      await updatePayWalletRechargePackage(data)
      toast.success('修改成功')
    } else {
      await createPayWalletRechargePackage(data)
      toast.success('新增成功')
    }
    uni.$emit('pay:recharge-package:reload')
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
