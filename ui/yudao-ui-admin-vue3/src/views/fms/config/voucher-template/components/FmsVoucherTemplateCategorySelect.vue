<template>
  <div class="flex w-full gap-8px [&_.el-select]:flex-1">
    <el-select
      :model-value="modelValue"
      :disabled="disabled"
      :placeholder="placeholder"
      @update:model-value="emit('update:modelValue', $event)"
    >
      <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id!" />
    </el-select>
    <el-button :disabled="disabled" @click="categoryManageRef?.open()">管理分类</el-button>
  </div>
  <FmsVoucherTemplateCategoryManage
    ref="categoryManageRef"
    :account-set-id="accountSetId"
    @change="emit('change', $event)"
    @select="emit('update:modelValue', $event)"
  />
</template>

<script lang="ts" setup>
import type { FmsVoucherTemplateCategoryVO } from '@/api/fms/config/voucher-template-category'

import FmsVoucherTemplateCategoryManage from './FmsVoucherTemplateCategoryManage.vue'

defineOptions({ name: 'FmsVoucherTemplateCategorySelect' })

withDefaults(
  defineProps<{
    accountSetId?: number
    categories: FmsVoucherTemplateCategoryVO[]
    modelValue?: number
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    accountSetId: undefined,
    modelValue: undefined,
    disabled: false,
    placeholder: '请选择模板分类'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [categories: FmsVoucherTemplateCategoryVO[]]
}>()

const categoryManageRef = ref<InstanceType<typeof FmsVoucherTemplateCategoryManage>>()
</script>
