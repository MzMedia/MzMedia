<template>
  <ContentWrap v-if="visible" class="min-h-[calc(100vh-130px)]">
    <!-- 未绑定员工档案提示 -->
    <el-result icon="warning" title="当前账号尚未开通员工端" :sub-title="description">
      <template #extra>
        <el-button v-if="canCreateEmployee" type="primary" @click="goEmployee">
          前往员工档案
        </el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>
    </el-result>

    <!-- 员工端开通步骤 -->
    <div
      v-if="canCreateEmployee"
      class="mx-auto mb-48px w-full max-w-760px border border-[var(--el-border-color-light)] border-solid rounded-[var(--el-border-radius-base)] px-32px py-28px"
    >
      <div class="mb-28px text-center text-16px text-[var(--el-text-color-primary)] font-600">
        完成员工端开通
      </div>
      <el-steps :active="0" align-center>
        <el-step title="进入员工档案" description="前往员工管理的员工档案列表" />
        <el-step title="新增并绑定账号" description="新增员工时绑定当前后台账号" />
        <el-step title="保存员工档案" description="完善必填信息并保存后即可进入员工端" />
      </el-steps>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { checkPermi } from '@/utils/permission'
import { redirectBoundEmployeeFromOpeningGuide } from '@/views/hrm/utils/employee'

defineOptions({ name: 'HrmPortalOpeningGuide' })

const router = useRouter() // 路由
const visible = ref(false) // 页面是否展示
const canCreateEmployee = checkPermi(['hrm:employee:create']) // 是否拥有员工创建权限
const description = canCreateEmployee
  ? '请先在员工管理中创建员工档案，并将绑定用户设置为当前后台账号。'
  : '请联系公司管理员在员工管理中创建员工档案，并绑定当前后台账号。'

/** 前往员工档案列表 */
function goEmployee() {
  router.push({ name: 'HrmEmployee' })
}

/** 返回个人工作台 */
function goHome() {
  router.push('/')
}

/** 初始化 */
onMounted(async () => {
  visible.value = !(await redirectBoundEmployeeFromOpeningGuide(router))
})
</script>
