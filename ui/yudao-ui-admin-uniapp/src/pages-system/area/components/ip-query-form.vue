<template>
  <wd-popup v-model="visible" position="bottom" closable safe-area-inset-bottom>
    <view class="p-32rpx">
      <view class="mb-24rpx text-32rpx font-semibold">
        IP 查询
      </view>
      <wd-cell-group border>
        <wd-form-item title="IP 地址" title-width="160rpx">
          <wd-input
            v-model="ipAddress"
            placeholder="请输入 IP 地址"
            clearable
          />
        </wd-form-item>
        <wd-form-item title="地址" title-width="160rpx">
          <wd-input
            v-model="ipResult"
            placeholder="展示查询 IP 结果"
            readonly
          />
        </wd-form-item>
      </wd-cell-group>
      <wd-button type="primary" block class="mt-32rpx" @click="handleQueryIp">
        查询
      </wd-button>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref, watch } from 'vue'
import { getAreaByIp } from '@/api/system/area'
import { isIp } from '@/utils/validator'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const visible = ref(false) // IP 查询弹窗显示状态
const ipAddress = ref('') // IP 地址
const ipResult = ref('') // 查询结果
const toast = useToast()

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    ipAddress.value = ''
    ipResult.value = ''
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

/** 查询 IP */
async function handleQueryIp() {
  if (!ipAddress.value) {
    toast.show('请输入 IP 地址')
    return
  }
  if (!isIp(ipAddress.value)) {
    toast.show('请输入正确的 IP 地址')
    return
  }
  ipResult.value = await getAreaByIp(ipAddress.value)
  toast.success('查询成功')
}
</script>
