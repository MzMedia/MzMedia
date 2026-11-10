<template>
  <ContentWrap v-if="visibleShortcuts.length > 0">
    <div class="flex items-center justify-between">
      <div>
        <div class="text-16px font-600 text-[var(--el-text-color-primary)]">常用功能</div>
        <div class="mt-6px text-13px text-[var(--el-text-color-secondary)]">
          快速进入日常财务工作
        </div>
      </div>
    </div>

    <div class="mt-20px grid grid-cols-1 gap-16px md:grid-cols-2 xl:grid-cols-4">
      <button
        v-for="shortcut in visibleShortcuts"
        :key="shortcut.path"
        class="group flex items-center gap-14px rounded-8px border border-transparent bg-[var(--el-color-primary-light-9)] p-18px text-left text-[var(--el-text-color-primary)] transition-all hover:-translate-y-2px hover:border-[var(--el-color-primary-light-5)]"
        type="button"
        @click="goTo(shortcut.path)"
      >
        <span
          class="h-42px w-42px flex flex-none items-center justify-center rounded-8px bg-[var(--el-color-primary)] text-22px text-white shadow"
        >
          <Icon :icon="shortcut.icon" />
        </span>
        <span>
          <strong class="block text-15px font-500">{{ shortcut.name }}</strong>
          <small class="mt-5px block text-12px text-[var(--el-text-color-secondary)]">
            {{ shortcut.description }}
          </small>
        </span>
      </button>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { checkPermi } from '@/utils/permission'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsHomeShortcuts' })

const SHORTCUTS = [
  {
    name: '录凭证',
    description: '新增会计凭证',
    icon: 'ep:edit-pen',
    path: '/fms/voucher/create',
    permission: 'fms:voucher:create',
    writeRequired: true
  },
  {
    name: '查凭证',
    description: '查询会计凭证',
    icon: 'ep:search',
    path: '/fms/voucher/list',
    permission: 'fms:voucher:query',
    writeRequired: false
  },
  {
    name: '科目余额表',
    description: '查看科目余额',
    icon: 'ep:data-analysis',
    path: '/fms/ledger/subject-balance',
    permission: 'fms:ledger:subject-balance:query',
    writeRequired: false
  },
  {
    name: '明细账',
    description: '查看科目明细',
    icon: 'ep:document',
    path: '/fms/ledger/detail',
    permission: 'fms:ledger:detail:query',
    writeRequired: false
  }
] // 常用功能

const router = useRouter() // 路由实例
const fmsStore = useFmsStore() // FMS Store
const visibleShortcuts = computed(() =>
  SHORTCUTS.filter(
    (shortcut) =>
      checkPermi([shortcut.permission]) &&
      (!shortcut.writeRequired || fmsStore.isAccountSetWritable)
  )
) // 可见的常用功能

/** 跳转常用功能 */
function goTo(path: string) {
  router.push(path)
}
</script>
