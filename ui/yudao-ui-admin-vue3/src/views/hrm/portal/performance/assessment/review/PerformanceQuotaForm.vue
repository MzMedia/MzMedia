<template>
  <el-drawer v-model="drawerVisible" title="制定绩效指标" size="960px" destroy-on-close>
    <div v-loading="loading">
      <div class="mb-18px flex items-center justify-between gap-16px">
        <div>
          <div class="text-20px font-600">{{ detail.employeeName || '-' }}</div>
          <div class="mt-4px text-13px text-[var(--el-text-color-secondary)]">
            {{ detail.name || '-' }}
          </div>
        </div>
        <dict-tag :type="DICT_TYPE.HRM_PERFORMANCE_STAGE_STATUS" :value="detail.stageType ?? 0" />
      </div>

      <el-alert
        v-if="detail.targetConfirmationResult === 0"
        class="mb-16px"
        :closable="false"
        type="warning"
        show-icon
        :title="`目标已退回：${detail.targetConfirmationComment || '请调整后重新提交'}`"
      />

      <section v-for="group in dimensionGroups" :key="group.key" class="mb-20px">
        <div class="min-h-42px flex items-center justify-between gap-12px">
          <div class="flex flex-col items-start font-600">
            <span>{{ group.name }}</span>
            <span class="mt-4px text-13px text-[var(--el-text-color-secondary)]">
              维度权重 {{ group.weight }}%
            </span>
          </div>
          <div class="flex items-center gap-12px">
            <span
              class="text-13px"
              :class="
                weightTotal(group) === 100
                  ? 'text-[var(--el-color-success)]'
                  : 'text-[var(--el-color-danger)]'
              "
            >
              指标权重 {{ weightTotal(group) }}%
            </span>
            <el-button
              v-if="group.allowEdit"
              :disabled="detail.stageType !== HrmPerformanceStageType.FILL_QUOTA"
              plain
              type="primary"
              @click="addQuota(group)"
            >
              <Icon icon="ep:plus" class="mr-5px" />新增指标
            </el-button>
          </div>
        </div>

        <el-table :data="group.quotas" border>
          <el-table-column label="指标名称" min-width="170">
            <template #default="scope">
              <div v-if="scope.row.preset" class="flex items-center justify-between gap-8px">
                <span>{{ scope.row.name || '-' }}</span>
                <el-tag size="small" type="info" effect="plain">预置</el-tag>
              </div>
              <el-input
                v-else
                v-model="scope.row.name"
                maxlength="255"
                placeholder="请输入指标名称"
              />
            </template>
          </el-table-column>
          <el-table-column label="指标说明" min-width="180">
            <template #default="scope">
              <span v-if="scope.row.preset">{{ scope.row.description || '-' }}</span>
              <el-input
                v-else
                v-model="scope.row.description"
                maxlength="1000"
                placeholder="请输入指标说明"
              />
            </template>
          </el-table-column>
          <el-table-column label="考核标准" min-width="210">
            <template #default="scope">
              <span v-if="scope.row.preset">{{ scope.row.standard || '-' }}</span>
              <el-input
                v-else
                v-model="scope.row.standard"
                maxlength="1000"
                placeholder="请输入考核标准"
              />
            </template>
          </el-table-column>
          <el-table-column label="指标权重" width="125">
            <template #default="scope">
              <span v-if="scope.row.preset">{{ scope.row.weight || 0 }}%</span>
              <el-input-number
                v-else
                v-model="scope.row.weight"
                :min="0.01"
                :max="100"
                :precision="2"
                :controls="false"
                aria-label="指标权重"
                class="!w-1/1"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="72">
            <template #default="scope">
              <el-button
                v-if="!scope.row.preset"
                link
                type="danger"
                title="删除指标"
                @click="removeQuota(scope.row)"
              >
                <Icon icon="ep:delete" />
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <el-empty v-if="!dimensionGroups.length" description="暂无可填写指标" />
    </div>

    <template #footer>
      <el-button @click="drawerVisible = false">取 消</el-button>
      <el-button
        :disabled="detail.stageType !== HrmPerformanceStageType.FILL_QUOTA"
        :loading="submitting"
        type="primary"
        @click="submitQuota"
      >
        提交指标
      </el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import type { PerformanceAssessmentQuotaVO } from '@/api/hrm/performance/assessment'
import * as PerformanceAssessmentApi from '@/api/hrm/portal/performance/assessment'
import { HrmPerformanceStageType } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmPortalPerformanceQuotaForm' })

interface DimensionGroup {
  key: string
  dimensionId?: number
  name: string
  weight: number
  allowEdit?: boolean
  quotas: PerformanceAssessmentQuotaVO[]
}

const message = useMessage() // 消息弹窗
const drawerVisible = ref(false) // 抽屉是否展示
const loading = ref(false) // 加载中
const submitting = ref(false) // 提交中
const detail = ref<PerformanceAssessmentApi.PortalPerformanceAssessmentVO>({}) // 详情数据
const dimensionGroups = computed<DimensionGroup[]>(() => {
  const groups = new Map<string, DimensionGroup>()
  for (const dimension of detail.value.dimensions || []) {
    const key = dimension.id !== undefined ? String(dimension.id) : dimension.name || 'default'
    groups.set(key, {
      key,
      dimensionId: dimension.id,
      name: dimension.name || '未命名维度',
      weight: Number(dimension.weight || 0),
      allowEdit: dimension.allowEdit,
      quotas: []
    })
  }
  for (const quota of detail.value.quotas || []) {
    const key = quota.dimensionId ? String(quota.dimensionId) : quota.dimensionName || 'default'
    if (!groups.has(key)) {
      groups.set(key, {
        key,
        dimensionId: quota.dimensionId,
        name: quota.dimensionName || '未命名维度',
        weight: Number(quota.dimensionWeight || 0),
        allowEdit: quota.allowEdit,
        quotas: []
      })
    }
    groups.get(key)?.quotas.push(quota)
  }
  return Array.from(groups.values())
})

/** 打开弹窗 */
async function open(assessmentId?: number) {
  if (!assessmentId) {
    return
  }
  drawerVisible.value = true
  loading.value = true
  try {
    // 获取表单数据
    detail.value = await PerformanceAssessmentApi.getPerformanceAssessment(assessmentId)
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 计算指标权重合计 */
function weightTotal(group: DimensionGroup) {
  return Number(
    group.quotas.reduce((total, quota) => total + Number(quota.weight || 0), 0).toFixed(2)
  )
}

/** 新增指标 */
function addQuota(group: DimensionGroup) {
  const remainingWeight = Math.max(0, Number((100 - weightTotal(group)).toFixed(2)))
  detail.value.quotas ||= []
  detail.value.quotas.push({
    dimensionId: group.dimensionId,
    preset: false,
    name: '',
    description: '',
    standard: '',
    weight: remainingWeight || undefined,
    scoreType: 1
  })
}

/** 删除指标 */
function removeQuota(quota: PerformanceAssessmentQuotaVO) {
  const index = detail.value.quotas?.indexOf(quota) ?? -1
  if (index >= 0) {
    detail.value.quotas?.splice(index, 1)
  }
}

/** 校验绩效指标 */
function validateQuota() {
  for (const group of dimensionGroups.value) {
    if (weightTotal(group) !== 100) {
      message.error(`${group.name}的指标权重合计必须等于 100%`)
      return false
    }
    const customQuotas = group.quotas.filter((quota) => !quota.preset)
    if (
      customQuotas.some(
        (quota) =>
          !quota.name?.trim() || !quota.standard?.trim() || !quota.weight || quota.weight <= 0
      )
    ) {
      message.error(`请完整填写${group.name}的新增指标`)
      return false
    }
    const names = group.quotas.map((quota) => quota.name?.trim()).filter(Boolean)
    if (new Set(names).size !== names.length) {
      message.error(`${group.name}存在重复指标名称`)
      return false
    }
  }
  return dimensionGroups.value.length > 0
}

/** 提交绩效指标 */
async function submitQuota() {
  if (!detail.value.id || !validateQuota()) {
    return
  }
  // 提交请求
  submitting.value = true
  try {
    await PerformanceAssessmentApi.fillPerformanceAssessmentQuota({
      assessmentId: detail.value.id,
      quotas: (detail.value.quotas || []).map(
        ({
          id,
          dimensionId,
          name,
          description,
          standard,
          weight,
          scoreType,
          targetValue,
          actualValue,
          selfScore,
          reviewerScore,
          finalScore,
          comment,
          sort
        }) => ({
          id,
          dimensionId,
          name,
          description,
          standard,
          weight,
          scoreType,
          targetValue,
          actualValue,
          selfScore,
          reviewerScore,
          finalScore,
          comment,
          sort
        })
      )
    })
    message.success('绩效指标已提交')
    drawerVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
