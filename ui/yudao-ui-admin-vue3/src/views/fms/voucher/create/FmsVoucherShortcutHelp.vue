<template>
  <el-popover placement="bottom-end" trigger="hover" :width="360">
    <template #reference>
      <el-button link type="primary">快捷键</el-button>
    </template>
    <div class="mb-8px font-600">凭证录入快捷键</div>
    <div
      v-for="item in shortcuts"
      :key="item.name"
      class="flex min-h-34px items-center justify-between border-b border-[var(--el-border-color-lighter)] border-b-solid last:border-b-0"
    >
      <span>{{ item.name }}</span>
      <kbd
        class="min-w-78px rounded-4px border border-[var(--el-border-color)] border-solid bg-[var(--el-fill-color-light)] px-7px py-3px text-center text-[var(--el-text-color-regular)] shadow-[0_1px_0_var(--el-border-color)]"
      >
        {{ item.key }}
      </kbd>
    </div>
  </el-popover>
</template>

<script lang="ts" setup>
defineOptions({ name: 'FmsVoucherShortcutHelp' })

const shortcuts = [
  { name: '保存并新增', key: 'F12' },
  { name: '新增', key: 'Alt + N' },
  { name: '下一输入项', key: 'Enter' },
  { name: '切换输入项', key: '方向键' },
  { name: '自动平衡借贷方金额', key: '=' },
  { name: '保存', key: 'Ctrl / ⌘ + S' }
] as const
</script>
