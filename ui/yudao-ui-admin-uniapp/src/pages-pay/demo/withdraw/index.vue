<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="示例提现"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 分页列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无示例提现数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1">
              <view class="truncate text-32rpx text-[#333] font-semibold">
                {{ item.subject || `示例提现 #${item.id}` }}
              </view>
              <view class="mt-6rpx text-24rpx text-[#999]">
                提现单编号：{{ item.id || '-' }}
              </view>
            </view>
            <wd-tag :type="getStatusTagType(item)">
              {{ getStatusText(item) }}
            </wd-tag>
          </view>

          <view class="mb-16rpx text-36rpx text-[#fa8c16] font-semibold">
            {{ formatDisplayMoney(item.price) }}
          </view>

          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">提现类型：</text>
            <text>{{ getWithdrawTypeText(item.type) }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">收款人：</text>
            <text>{{ item.userName || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">收款账号：</text>
            <text class="min-w-0 flex-1 break-all">{{ item.userAccount || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">转账单号：</text>
            <text>{{ item.payTransferId || '-' }}</text>
          </view>
          <view class="mb-12rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">转账渠道：</text>
            <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="item.transferChannelCode" />
          </view>
          <view class="mb-16rpx flex items-center text-28rpx text-[#666]">
            <text class="mr-8rpx shrink-0 text-[#999]">转账时间：</text>
            <text>{{ formatDateTime(item.transferTime) || '-' }}</text>
          </view>
          <view v-if="item.transferErrorMsg" class="mb-16rpx text-28rpx text-[#f5222d]">
            {{ item.transferErrorMsg }}
          </view>

          <wd-button
            v-if="canTransfer(item)"
            size="small"
            type="primary"
            block
            :loading="transferringId === item.id"
            @click="handleTransfer(item)"
          >
            {{ item.status === 20 ? '重新转账' : '发起转账' }}
          </wd-button>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="openCreatePopup"
    />

    <!-- 创建提现弹窗 -->
    <wd-popup
      v-model="createVisible"
      position="bottom"
      custom-style="border-radius: 24rpx 24rpx 0 0;"
      safe-area-inset-bottom
      @close="resetForm"
    >
      <view class="p-32rpx">
        <view class="mb-24rpx flex items-center justify-between">
          <text class="text-32rpx text-[#333] font-semibold">创建示例提现单</text>
          <wd-icon name="close" size="20px" @click="createVisible = false" />
        </view>

        <wd-form ref="formRef" :model="formData" :schema="formSchema">
          <wd-form-item title="提现标题" title-width="210rpx" prop="subject">
            <wd-input v-model="formData.subject" clearable placeholder="请输入提现标题" />
          </wd-form-item>
          <wd-form-item title="提现类型" title-width="210rpx" prop="type" center>
            <wd-radio-group v-model="formData.type" type="button">
              <wd-radio :value="1">
                支付宝
              </wd-radio>
              <wd-radio :value="2">
                微信余额
              </wd-radio>
              <wd-radio :value="3">
                钱包
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="提现金额(元)" title-width="210rpx" prop="price" center>
            <wd-input-number v-model="formData.price" :min="0.01" :step="0.01" :precision="2" />
          </wd-form-item>
          <wd-form-item title="收款账号" title-width="210rpx" prop="userAccount">
            <wd-input v-model="formData.userAccount" clearable :placeholder="accountPlaceholder" />
          </wd-form-item>
          <wd-form-item title="收款人姓名" title-width="210rpx" prop="userName">
            <wd-input v-model="formData.userName" clearable placeholder="请输入收款人姓名" />
          </wd-form-item>
        </wd-form>

        <view class="mt-32rpx">
          <wd-button type="primary" block :loading="formLoading" @click="handleSubmit">
            确定
          </wd-button>
        </view>
      </view>
    </wd-popup>
  </view>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import type { PayDemoWithdraw } from '@/api/pay/demo/withdraw'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, ref } from 'vue'
import {
  createPayDemoWithdraw,
  getPayDemoWithdrawPage,
  transferPayDemoWithdraw,
} from '@/api/pay/demo/withdraw'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import { createFormSchema } from '@/utils/wot'
import { formatDisplayMoney, yuanToFen } from '@/utils/format'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const dialog = useDialog()
const list = ref<PayDemoWithdraw[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const formRef = ref<FormInstance>() // 表单组件引用
const createVisible = ref(false) // 创建弹窗状态
const formLoading = ref(false) // 表单提交状态
const transferringId = ref<number>() // 转账中的提现单编号
const formData = ref({
  subject: '',
  price: 0.01,
  type: 1,
  userName: '',
  userAccount: '',
}) // 表单数据
const formSchema = createFormSchema({
  subject: [{ required: true, message: '提现标题不能为空' }],
  price: [
    { required: true, message: '提现金额不能为空' },
    { validator: value => Number(value) > 0 || '提现金额必须大于 0' },
  ],
  type: [{ required: true, message: '提现类型不能为空' }],
  userAccount: [{ required: true, message: '收款账号不能为空' }],
})
const accountPlaceholder = computed(() => {
  if (formData.value.type === 1) {
    return '请输入支付宝账号'
  }
  if (formData.value.type === 2) {
    return '请输入微信 openid'
  }
  if (formData.value.type === 3) {
    return '请输入钱包编号'
  }
  return '请输入收款人账号'
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPayDemoWithdrawPage({ pageNo, pageSize })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 提现类型展示 */
function getWithdrawTypeText(type?: number) {
  if (type === 1) {
    return '支付宝'
  }
  if (type === 2) {
    return '微信余额'
  }
  if (type === 3) {
    return '钱包'
  }
  return '-'
}

/** 提现状态展示 */
function getStatusText(item: PayDemoWithdraw) {
  if (item.status === 0 && !item.payTransferId) {
    return '等待转账'
  }
  if (item.status === 0 && item.payTransferId) {
    return '转账中'
  }
  if (item.status === 10) {
    return '转账成功'
  }
  if (item.status === 20) {
    return '转账失败'
  }
  return '-'
}

/** 提现状态标签 */
function getStatusTagType(item: PayDemoWithdraw) {
  if (item.status === 10) {
    return 'success'
  }
  if (item.status === 20) {
    return 'danger'
  }
  if (item.status === 0 && item.payTransferId) {
    return 'default'
  }
  return 'warning'
}

/** 是否可转账 */
function canTransfer(item: PayDemoWithdraw) {
  return (item.status === 0 && !item.payTransferId) || item.status === 20
}

/** 打开创建弹窗 */
function openCreatePopup() {
  resetForm()
  createVisible.value = true
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    subject: '',
    price: 0.01,
    type: 1,
    userName: '',
    userAccount: '',
  }
  formRef.value?.reset()
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  formLoading.value = true
  try {
    await createPayDemoWithdraw({
      ...formData.value,
      price: yuanToFen(formData.value.price),
    })
    toast.success('创建成功')
    createVisible.value = false
    pagingRef.value?.reload()
  } finally {
    formLoading.value = false
  }
}

/** 发起转账 */
async function handleTransfer(item: PayDemoWithdraw) {
  if (!item.id) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确认要执行转账操作吗？',
    })
  } catch {
    return
  }

  transferringId.value = item.id
  try {
    const payTransferId = await transferPayDemoWithdraw(item.id)
    toast.success(`转账提交成功，转账单号：${payTransferId}`)
    pagingRef.value?.reload()
  } finally {
    transferringId.value = undefined
  }
}
</script>
