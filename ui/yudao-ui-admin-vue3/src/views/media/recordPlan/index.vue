<template>
  <div class="media-record-plan">
    <el-card shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="openPlanDialog()">新建录像计划</el-button>
        <el-button type="primary" :icon="Refresh" @click="loadPlans">刷新</el-button>
      </div>

      <el-table :data="planList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="计划ID" width="90" align="center" />
        <el-table-column prop="name" label="计划名称" min-width="180" />
        <el-table-column prop="channelCount" label="关联通道数" width="130" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.channelCount || 0 }} 个通道</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openLinkDialog(row)">关联通道</el-button>
            <el-button link type="primary" @click="openPlanDialog(row)">编辑计划</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 计划编辑对话框 (包含周一至周日 7x24 时间段设置) -->
    <el-dialog v-model="planDialogVisible" :title="planDialogTitle" width="750px">
      <el-form :model="planForm" label-width="90px">
        <el-form-item label="计划名称" required>
          <el-input v-model="planForm.name" placeholder="例如：全天候常态化录像" />
        </el-form-item>

        <el-form-item label="执行周期">
          <div class="weekly-schedule-editor">
            <div v-for="day in weekDays" :key="day.value" class="day-schedule-row">
              <span class="day-name">{{ day.label }}</span>
              <div class="time-range-group">
                <el-time-picker
                  v-model="day.start"
                  is-range
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  value-format="HH:mm:ss"
                  size="small"
                />
                <el-checkbox v-model="day.enabled">全天 (00:00 - 23:59)</el-checkbox>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">保存计划</el-button>
      </template>
    </el-dialog>

    <!-- 关联通道穿梭框对话框 -->
    <el-dialog v-model="linkDialogVisible" title="关联监控通道" width="650px">
      <div v-loading="linkLoading">
        <el-transfer
          v-model="selectedChannelIds"
          :data="allChannels"
          :titles="['可选通道', '已关联通道']"
          :props="{ key: 'id', label: 'name' }"
          filterable
          filter-placeholder="搜索通道名称"
        />
      </div>
      <template #footer>
        <el-button @click="linkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveChannelLink">确定关联</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRecordPlanList, addRecordPlan, updateRecordPlan, deleteRecordPlan, linkRecordPlan, getPlanChannels } from '@/api/media/record'
import { getChannelPage } from '@/api/media/channel'

const loading = ref(false)
const planList = ref<any[]>([])

const planDialogVisible = ref(false)
const planDialogTitle = ref('')
const planForm = reactive<any>({
  id: undefined,
  name: ''
})

const weekDays = ref([
  { label: '周一', value: 1, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周二', value: 2, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周三', value: 3, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周四', value: 4, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周五', value: 5, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周六', value: 6, start: ['00:00:00', '23:59:59'], enabled: true },
  { label: '周日', value: 7, start: ['00:00:00', '23:59:59'], enabled: true }
])

const linkDialogVisible = ref(false)
const linkLoading = ref(false)
const currentPlanId = ref<number>()
const allChannels = ref<any[]>([])
const selectedChannelIds = ref<number[]>([])

const loadPlans = async () => {
  loading.value = true
  try {
    const res = await getRecordPlanList({ page: 1, count: 50 })
    planList.value = res?.data?.list || res?.data || []
  } finally {
    loading.value = false
  }
}

const openPlanDialog = (row?: any) => {
  planDialogTitle.value = row?.id ? '编辑录像计划' : '新建录像计划'
  planForm.id = row?.id
  planForm.name = row?.name || ''
  planDialogVisible.value = true
}

const savePlan = async () => {
  if (!planForm.name) {
    ElMessage.warning('请输入计划名称')
    return
  }

  const items = weekDays.value
    .filter((d) => d.enabled)
    .map((d) => ({
      dayOfWeek: d.value,
      start: d.start?.[0] || '00:00:00',
      stop: d.start?.[1] || '23:59:59'
    }))

  const payload = {
    id: planForm.id,
    name: planForm.name,
    planItemList: items
  }

  try {
    if (planForm.id) {
      await updateRecordPlan(payload)
    } else {
      await addRecordPlan(payload)
    }
    ElMessage.success('保存成功')
    planDialogVisible.value = false
    loadPlans()
  } catch (err: any) {
    ElMessage.error(err.message || '保存失败')
  }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定删除该录像计划吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteRecordPlan(id)
    ElMessage.success('删除成功')
    loadPlans()
  })
}

const openLinkDialog = async (row: any) => {
  currentPlanId.value = row.id
  linkDialogVisible.value = true
  linkLoading.value = true
  try {
    const [channelRes, linkedRes] = await Promise.all([
      getChannelPage({ page: 1, count: 200 }),
      getPlanChannels(row.id)
    ])
    allChannels.value = (channelRes?.data?.list || channelRes?.data || []).map((c: any) => ({
      id: c.id,
      name: c.name || `通道 ${c.channelId}`
    }))
    selectedChannelIds.value = (linkedRes?.data || []).map((c: any) => c.id || c)
  } catch (err: any) {
    ElMessage.error('加载通道列表失败')
  } finally {
    linkLoading.value = false
  }
}

const saveChannelLink = async () => {
  if (!currentPlanId.value) return
  try {
    await linkRecordPlan(currentPlanId.value, selectedChannelIds.value)
    ElMessage.success('关联通道成功')
    linkDialogVisible.value = false
    loadPlans()
  } catch (err: any) {
    ElMessage.error(err.message || '关联失败')
  }
}

onMounted(() => {
  loadPlans()
})
</script>

<style scoped lang="scss">
.media-record-plan {
  padding: 10px;

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
  }

  .weekly-schedule-editor {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 8px;

    .day-schedule-row {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 6px 10px;
      background: #f6f8fa;
      border-radius: 4px;

      .day-name {
        font-weight: 600;
        width: 40px;
        color: #24292f;
      }

      .time-range-group {
        display: flex;
        align-items: center;
        gap: 12px;
      }
    }
  }
}
</style>
