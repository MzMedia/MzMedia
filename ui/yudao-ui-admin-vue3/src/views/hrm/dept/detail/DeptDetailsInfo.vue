<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">基本信息</span>
        </template>
        <el-descriptions :column="3">
          <el-descriptions-item label="部门名称">{{ dept.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="上级部门">{{ parentDeptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="部门负责人">{{
            leaderUserName || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="显示排序">{{ dept.sort ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ dept.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ dept.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="dept.status" />
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ dept.createTime ? formatDate(dept.createTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as DeptApi from '@/api/system/dept'

defineOptions({ name: 'HrmDeptDetailsInfo' })

defineProps<{
  dept: DeptApi.DeptVO
  parentDeptName?: string
  leaderUserName?: string
}>()

const activeNames = ref(['basicInfo']) // 默认展开基本信息
</script>
