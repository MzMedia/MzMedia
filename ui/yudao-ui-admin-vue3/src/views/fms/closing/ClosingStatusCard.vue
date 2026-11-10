<template>
  <el-card shadow="never" class="mb-16px min-h-132px">
    <div class="text-[var(--el-text-color-secondary)]">{{ title }}</div>
    <div class="my-12px text-22px font-600">{{ value }}</div>
    <el-tag :type="tagType">{{ tagLabel }}</el-tag>
  </el-card>
</template>

<script lang="ts" setup>
defineOptions({ name: 'FmsClosingStatusCard' })

defineProps<{
  title: string // 检查项名称
  value: string // 检查项结果文本
  tagType: 'success' | 'warning' | 'danger' | 'info' // 状态标签类型
  tagLabel: string // 状态标签文本
}>()
</script>
