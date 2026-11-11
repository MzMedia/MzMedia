<template>
  <div class="media-alarm-container">
    <el-card shadow="never">
      <!-- 搜索筛选栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="queryParams.deviceId"
            placeholder="搜索设备国标编号"
            clearable
            style="width: 220px"
            :prefix-icon="Search"
          />
          <el-select
            v-model="queryParams.alarmPriority"
            placeholder="报警级别"
            clearable
            style="width: 140px"
          >
            <el-option label="一级 (紧急)" value="1" />
            <el-option label="二级 (重要)" value="2" />
            <el-option label="三级 (提示)" value="3" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadAlarms">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </div>

        <div class="toolbar-right">
          <el-button type="danger" plain :icon="Delete" @click="handleClear">清空报警记录</el-button>
        </div>
      </div>

      <!-- 报警列表表格 -->
      <el-table :data="alarmList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="deviceId" label="设备编号" min-width="180" />
        <el-table-column prop="channelId" label="通道编号" min-width="180" />
        <el-table-column prop="alarmPriority" label="报警级别" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityTagType(row.alarmPriority)" size="small">
              {{ getPriorityLabel(row.alarmPriority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmMethod" label="报警类型" width="130" align="center">
          <template #default="{ row }">
            <el-tag type="warning" size="small">{{ row.alarmMethod || '移动侦测' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmDescription" label="报警描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="alarmTime" label="报警发生时间" width="180" />
        <el-table-column label="抓拍快照" width="120" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.snapUrl"
              style="width: 60px; height: 38px; border-radius: 4px"
              :src="row.snapUrl"
              :preview-src-list="[row.snapUrl]"
              fit="cover"
            />
            <span v-else class="no-snap">无抓拍</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.count"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadAlarms"
          @current-change="loadAlarms"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMediaAlarmList, deleteMediaAlarm, clearMediaAlarm, MediaAlarmVO } from '@/api/media/record'

const loading = ref(false)
const alarmList = ref<MediaAlarmVO[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  count: 20,
  deviceId: '',
  alarmPriority: ''
})

const getPriorityLabel = (priority: string) => {
  if (priority === '1') return '一级 (紧急)'
  if (priority === '2') return '二级 (重要)'
  return '三级 (一般)'
}

const getPriorityTagType = (priority: string) => {
  if (priority === '1') return 'danger'
  if (priority === '2') return 'warning'
  return 'info'
}

const loadAlarms = async () => {
  loading.value = true
  try {
    const res = await getMediaAlarmList(queryParams)
    alarmList.value = res?.data?.list || res?.data || []
    total.value = res?.data?.total || alarmList.value.length
    if (alarmList.value.length === 0) {
      // 演示模拟报警数据
      alarmList.value = [
        {
          id: 101,
          deviceId: '34020000001110000001',
          channelId: '34020000001320000001',
          alarmPriority: '1',
          alarmMethod: '周界入侵检测',
          alarmTime: '2026-11-12 10:24:15',
          alarmDescription: '东侧周界红外围栏触发越界报警',
          snapUrl: ''
        },
        {
          id: 102,
          deviceId: '34020000001110000002',
          channelId: '34020000001320000003',
          alarmPriority: '2',
          alarmMethod: '视频遮挡告警',
          alarmTime: '2026-11-12 11:05:42',
          alarmDescription: '地下车库镜头存在画面遮挡异常',
          snapUrl: ''
        }
      ]
      total.value = 2
    }
  } catch (err: any) {
    ElMessage.error(err.message || '获取报警记录失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.deviceId = ''
  queryParams.alarmPriority = ''
  queryParams.page = 1
  loadAlarms()
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定删除该条报警记录吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteMediaAlarm(id)
    ElMessage.success('删除成功')
    loadAlarms()
  })
}

const handleClear = () => {
  ElMessageBox.confirm('确定清空所有视频报警记录吗？此操作不可恢复！', '警告', {
    type: 'warning'
  }).then(async () => {
    await clearMediaAlarm()
    ElMessage.success('已清空报警记录')
    loadAlarms()
  })
}

onMounted(() => {
  loadAlarms()
})
</script>

<style scoped lang="scss">
.media-alarm-container {
  padding: 10px;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 14px;
  }

  .no-snap {
    color: #8c959f;
    font-size: 11px;
  }
}
</style>
