<template>
  <view>
    <view
      v-for="(rule, index) in rules"
      :key="index"
      class="mb-16rpx rounded-8rpx bg-[#f7f8fa] p-16rpx"
    >
      <view class="mb-12rpx flex items-center justify-between">
        <text class="text-28rpx text-[#333] font-medium">区域 {{ index + 1 }}</text>
        <text class="text-26rpx text-[#fa4350]" @click="handleRemove(index)">删除</text>
      </view>

      <!-- 配送区域（多选地区树） -->
      <yd-tree-select
        v-model="rule.areaIds"
        multiple
        filterable
        label="配送区域"
        label-width="160rpx"
        :data="areaTree"
        :props="{ value: 'id', label: 'name', children: 'children' }"
        placeholder="请选择配送区域"
        @change="emitChange"
      />

      <!-- 计费区域字段 -->
      <template v-if="mode === 'charge'">
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">首{{ unitLabel }}</text>
          <wd-input-number v-model="rule.startCount" :min="0" :step="countStep" :precision="countPrecision" @change="emitChange" />
        </view>
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">运费(元)</text>
          <wd-input-number v-model="rule.startPrice" :min="0" :step="0.01" :precision="2" @change="emitChange" />
        </view>
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">续{{ unitLabel }}</text>
          <wd-input-number v-model="rule.extraCount" :min="0" :step="countStep" :precision="countPrecision" @change="emitChange" />
        </view>
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">续费(元)</text>
          <wd-input-number v-model="rule.extraPrice" :min="0" :step="0.01" :precision="2" @change="emitChange" />
        </view>
      </template>

      <!-- 包邮区域字段 -->
      <template v-else>
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">包邮{{ unitLabel }}</text>
          <wd-input-number v-model="rule.freeCount" :min="0" :step="countStep" :precision="countPrecision" @change="emitChange" />
        </view>
        <view class="flex items-center gap-12rpx py-6rpx">
          <text class="w-180rpx shrink-0 text-26rpx text-[#666]">包邮金额(元)</text>
          <wd-input-number v-model="rule.freePrice" :min="0" :step="0.01" :precision="2" @change="emitChange" />
        </view>
      </template>
    </view>

    <wd-button size="small" variant="plain" @click="handleAdd">
      添加区域
    </wd-button>
  </view>
</template>

<script lang="ts" setup>
import type { Area } from '@/api/system/area'
import { computed } from 'vue'
import { DeliveryExpressChargeModeEnum } from '@/utils/constants'

const props = defineProps<{
  modelValue: Record<string, any>[] // 区域规则（金额单位元）
  mode: 'charge' | 'free' // 计费区域 / 包邮区域
  areaTree: Area[] // 地区树
  chargeMode?: number // 计费方式：1 件数、2 重量、3 体积
}>()

const emit = defineEmits<{
  'update:modelValue': [rules: Record<string, any>[]]
}>()

const rules = computed(() => props.modelValue || []) // 区域规则（直接编辑元素）
const unitLabel = computed(() => ({ [DeliveryExpressChargeModeEnum.COUNT]: '件', [DeliveryExpressChargeModeEnum.WEIGHT]: '重量(kg)', [DeliveryExpressChargeModeEnum.VOLUME]: '体积(m³)' } as Record<number, string>)[props.chargeMode ?? DeliveryExpressChargeModeEnum.COUNT] || '件') // 计量单位文案
const isCountMode = computed(() => (props.chargeMode ?? DeliveryExpressChargeModeEnum.COUNT) === DeliveryExpressChargeModeEnum.COUNT) // 是否按件数计费
const countPrecision = computed(() => isCountMode.value ? 0 : 2) // 件数整数，重量/体积保留 2 位小数
const countStep = computed(() => isCountMode.value ? 1 : 0.01) // 计量步长

/** 通知父级变化 */
function emitChange() {
  emit('update:modelValue', [...rules.value])
}

/** 新增区域规则 */
function handleAdd() {
  const rule = props.mode === 'charge'
    ? { areaIds: [], startCount: 0, startPrice: 0, extraCount: 0, extraPrice: 0 }
    : { areaIds: [], freeCount: 0, freePrice: 0 }
  emit('update:modelValue', [...rules.value, rule])
}

/** 删除区域规则 */
function handleRemove(index: number) {
  const next = [...rules.value]
  next.splice(index, 1)
  emit('update:modelValue', next)
}
</script>
