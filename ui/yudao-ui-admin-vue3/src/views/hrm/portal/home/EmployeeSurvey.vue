<template>
  <ContentWrap class="min-h-130px">
    <template v-if="employee">
      <div class="flex items-start gap-34px px-20px py-18px">
        <el-avatar :size="64" :src="employee.avatar">
          {{ employee.name?.slice(0, 1) }}
        </el-avatar>
        <div class="min-w-0 pt-4px">
          <div class="text-20px text-[var(--el-text-color-primary)] font-700">
            Hi，{{ employee.name }}
          </div>
          <div class="mt-12px text-[var(--el-text-color-regular)]">
            这是你在{{ employee.deptName || '部门' }}的第
            <b class="text-[var(--el-text-color-primary)] font-700">
              {{ employee.entryDay || 0 }}
            </b>
            天
          </div>
          <div class="mt-22px text-[var(--el-text-color-regular)] leading-30px">
            <span>
              部门
              <b class="text-[var(--el-text-color-primary)] font-700">
                {{ employee.deptName || '未设置' }}
              </b>
              ，
            </span>
            <span>
              岗位
              <b class="text-[var(--el-text-color-primary)] font-700">
                {{ employee.postName || '未设置' }}
              </b>
              ，
            </span>
            <span>
              工号
              <b class="text-[var(--el-text-color-primary)] font-700">
                {{ employee.jobNumber || '未设置' }}
              </b>
              ，
            </span>
            <span v-if="employee.entryTime">
              <b class="text-[var(--el-text-color-primary)] font-700">
                {{ formatHrmDate(employee.entryTime) }}
              </b>
              入职
            </span>
            <span v-if="showRegularDate">
              ，将于
              <b class="text-[var(--el-text-color-primary)] font-700">
                {{ formatHrmDate(employee.regularTime) }}
              </b>
              转正
            </span>
          </div>
          <el-button
            v-if="salarySlipSummary?.reminder"
            class="mt-10px h-auto whitespace-normal !p-0"
            link
            type="primary"
            @click="goSalarySlip"
          >
            {{ salarySlipSummary.reminder }} &gt;&gt;
          </el-button>
        </div>
      </div>
    </template>
    <el-empty v-else :image-size="100" description="当前账号未绑定员工档案" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import type { HrmPortalEmployeeVO } from '@/api/hrm/portal/employee'
import type { SalarySlipUnreadSummaryVO } from '@/api/hrm/portal/salary/slip'
import { formatHrmDate } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalEmployeeSurvey' })

const props = defineProps<{
  employee?: HrmPortalEmployeeVO
  salarySlipSummary?: SalarySlipUnreadSummaryVO
}>()

const router = useRouter() // 路由
const showRegularDate = computed(() => {
  const regularTime = props.employee?.regularTime
  return regularTime ? dayjs().isBefore(dayjs(regularTime)) : false
})

/** 前往我的工资条 */
function goSalarySlip() {
  router.push({ name: 'HrmPortalSalarySlip' })
}
</script>
