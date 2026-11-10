<template>
  <el-drawer v-model="drawerVisible" title="确认绩效指标" size="920px" destroy-on-close>
    <div v-loading="loading">
      <div class="mb-16px flex items-start justify-between gap-16px">
        <div>
          <div class="text-20px font-600">{{ detail.employeeName || '-' }}</div>
          <div class="mt-4px text-13px text-[var(--el-text-color-secondary)]">
            {{ detail.name || '-' }}
          </div>
        </div>
        <el-tag type="warning" effect="plain">待指标确认</el-tag>
      </div>

      <el-descriptions :column="3" border class="mb-16px">
        <el-descriptions-item label="工号">{{ detail.jobNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="确认人">
          {{ detail.targetConfirmationEmployeeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="指标数">
          {{ detail.quotas?.length || 0 }}
        </el-descriptions-item>
      </el-descriptions>

      <el-table :data="detail.quotas || []" border>
        <el-table-column label="维度" prop="dimensionName" min-width="120" show-overflow-tooltip />
        <el-table-column label="指标" prop="name" min-width="160" show-overflow-tooltip />
        <el-table-column
          label="指标说明"
          prop="description"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column label="考核标准" prop="standard" min-width="210" show-overflow-tooltip />
        <el-table-column label="权重" width="130" align="center">
          <template #default="scope">
            {{ scope.row.dimensionWeight || 0 }}% / {{ scope.row.weight || 0 }}%
          </template>
        </el-table-column>
      </el-table>

      <el-input
        v-model="comment"
        class="mt-16px"
        type="textarea"
        :rows="3"
        maxlength="1000"
        show-word-limit
        placeholder="填写确认意见；退回时必填"
      />
    </div>

    <template #footer>
      <el-button @click="drawerVisible = false">取 消</el-button>
      <el-button :loading="submitting" plain type="danger" @click="submitConfirm(0)">
        退回指标
      </el-button>
      <el-button :loading="submitting" type="primary" @click="submitConfirm(1)">
        确认通过
      </el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import * as PerformanceAssessmentApi from '@/api/hrm/portal/performance/assessment'

defineOptions({ name: 'HrmPortalPerformanceTargetConfirmForm' })

const message = useMessage() // 消息弹窗
const drawerVisible = ref(false) // 抽屉是否展示
const loading = ref(false) // 加载中
const submitting = ref(false) // 提交中
const detail = ref<PerformanceAssessmentApi.PortalPerformanceAssessmentVO>({}) // 详情数据
const comment = ref('') // 处理意见

/** 打开弹窗 */
async function open(assessmentId?: number, stageId?: number) {
  if (!assessmentId || !stageId) {
    return
  }
  drawerVisible.value = true
  loading.value = true
  comment.value = ''
  try {
    // 获取表单数据
    detail.value = await PerformanceAssessmentApi.getPerformanceAssessment(assessmentId, stageId)
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 提交目标确认 */
async function submitConfirm(pass: number) {
  if (!detail.value.id) {
    return
  }
  if (pass === 0 && !comment.value.trim()) {
    message.error('退回指标时请填写原因')
    return
  }
  // 提交请求
  submitting.value = true
  try {
    await PerformanceAssessmentApi.confirmPerformanceAssessmentTarget({
      assessmentId: detail.value.id,
      pass,
      comment: comment.value.trim() || (pass === 1 ? '指标确认通过' : undefined)
    })
    message.success(pass === 1 ? '指标已确认' : '指标已退回')
    drawerVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
