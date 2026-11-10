<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between gap-16px">
      <div class="min-w-0">
        <div class="flex flex-wrap items-center gap-10px">
          <span class="break-all text-xl font-bold">{{ dept.name || '-' }}</span>
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="dept.status" />
        </div>
        <div class="mt-6px text-sm text-[var(--el-text-color-secondary)]">
          部门编号：{{ dept.id || '-' }}
        </div>
      </div>
      <div><slot></slot></div>
    </div>
    <ContentWrap class="mt-10px">
      <el-descriptions :column="5" direction="vertical">
        <el-descriptions-item label="上级部门">{{ parentDeptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门负责人">{{ leaderUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="在职员工">
          {{ statistics.activeCount }}
        </el-descriptions-item>
        <el-descriptions-item label="全职员工">
          {{ statistics.fullTimeCount }}
        </el-descriptions-item>
        <el-descriptions-item label="非全职人数">
          {{ statistics.nonFullTimeCount }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>
  </div>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as DeptApi from '@/api/system/dept'

defineOptions({ name: 'HrmDeptDetailsHeader' })

defineProps<{
  dept: DeptApi.DeptVO
  parentDeptName?: string
  leaderUserName?: string
  statistics: {
    activeCount: number
    fullTimeCount: number
    nonFullTimeCount: number
  }
  loading: boolean
}>()
</script>
