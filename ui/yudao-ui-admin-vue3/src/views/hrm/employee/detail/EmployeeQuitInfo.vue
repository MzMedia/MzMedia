<template>
  <ContentWrap v-if="quitInfo" v-loading="loading" title="离职信息">
    <!-- 操作栏 -->
    <div class="tab-toolbar">
      <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="emit('edit')">
        <Icon icon="ep:edit" class="mr-5px" />编辑离职信息
      </el-button>
    </div>
    <el-descriptions :column="3" border>
      <el-descriptions-item label="计划离职时间">
        {{ formatHrmDateTime(quitInfo.planQuitTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="申请离职">
        {{ formatHrmDate(quitInfo.applyQuitTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="薪资结算">
        {{ formatHrmDate(quitInfo.salarySettlementTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="离职类型">
        {{ formatEmployeeQuitType(quitInfo.type) }}
      </el-descriptions-item>
      <el-descriptions-item label="离职原因">
        {{ formatEmployeeQuitReason(quitInfo.reason) }}
      </el-descriptions-item>
      <el-descriptions-item label="原员工状态">
        <dict-tag
          v-if="quitInfo.oldEmployeeStatus != null"
          :type="DICT_TYPE.HRM_EMPLOYEE_STATUS"
          :value="quitInfo.oldEmployeeStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="3">
        {{ quitInfo.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as QuitInfoApi from '@/api/hrm/employee/quit-info'
import {
  formatEmployeeQuitReason,
  formatEmployeeQuitType,
  formatHrmDate,
  formatHrmDateTime
} from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmEmployeeQuitInfo' })

const props = defineProps<{
  employeeId: number
}>()
const emit = defineEmits<{
  edit: []
}>() // 定义 edit 事件，用于编辑离职信息

const loading = ref(false) // 详情的加载中
const quitInfo = ref<QuitInfoApi.HrmEmployeeQuitInfoVO>() // 离职信息

/** 获取离职信息 */
async function getQuitInfo() {
  loading.value = true
  try {
    quitInfo.value = await QuitInfoApi.getEmployeeQuitInfo(props.employeeId)
  } finally {
    loading.value = false
  }
}
defineExpose({ getQuitInfo }) // 提供 getQuitInfo 方法，用于离职信息变更后刷新

/** 初始化 */
onMounted(() => {
  getQuitInfo()
})
</script>

<style scoped>
.tab-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
</style>
