<template>
  <div
    class="overflow-hidden rounded-6px border border-[var(--el-border-color-lighter)] border-solid bg-[var(--el-fill-color-light)] px-10px pb-10px"
  >
    <!-- 方案选择 -->
    <div class="h-44px flex items-center justify-between">
      <el-checkbox
        v-if="fmsStore.isAccountSetWritable"
        :model-value="checked"
        @change="emit('update:checked', $event)"
      >
        {{ name }}
      </el-checkbox>
      <span v-else>{{ name }}</span>
      <el-button
        type="primary"
        link
        @click="emit('settings')"
        v-hasPermi="['fms:closing:update']"
        v-if="fmsStore.isAccountSetWritable"
      >
        <Icon icon="ep:setting" />
      </el-button>
    </div>
    <!-- 待结转金额 -->
    <div
      class="min-h-82px rounded-5px bg-[var(--el-bg-color)] p-16px shadow-[var(--el-box-shadow-lighter)]"
    >
      <div class="text-18px font-600">{{ formatMoney(balance) }}</div>
      <div
        class="mt-8px min-h-24px flex items-center justify-between text-12px text-[var(--el-text-color-secondary)]"
      >
        <span>金额</span>
        <span
          v-if="voucherIds.length"
          class="flex max-w-150px gap-6px overflow-auto whitespace-nowrap"
        >
          <el-link
            v-for="voucherId in voucherIds"
            :key="voucherId"
            type="primary"
            @click="emit('open-voucher', voucherId)"
          >
            凭证 #{{ voucherId }}
          </el-link>
        </span>
      </div>
    </div>
    <!-- 生成凭证 -->
    <div class="h-36px flex items-center justify-end">
      <el-button
        type="primary"
        link
        :disabled="generateDisabled"
        @click="emit('generate')"
        v-hasPermi="['fms:closing:profit-loss']"
        v-if="fmsStore.isAccountSetWritable"
      >
        {{ voucherIds.length ? '重新生成' : '生成凭证' }}
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { formatMoney } from '@/views/fms/utils/format'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsClosingSchemeCard' })

const fmsStore = useFmsStore() // FMS 状态

defineProps<{
  name: string // 方案名称
  checked: boolean // 是否选中
  balance: number // 待结转金额
  voucherIds: number[] // 当前期间已生成凭证编号数组
  generateDisabled: boolean // 是否禁止生成凭证
}>()

const emit = defineEmits<{
  'update:checked': [checked: boolean | string | number]
  settings: []
  generate: []
  'open-voucher': [voucherId: number]
}>()
</script>
