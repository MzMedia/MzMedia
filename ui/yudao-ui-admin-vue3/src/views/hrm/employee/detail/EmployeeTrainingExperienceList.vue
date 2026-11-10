<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="培训课程" prop="course" min-width="150" />
    <el-table-column label="培训机构" prop="organizationName" min-width="150" />
    <el-table-column label="开始日期" prop="startTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="结束日期" prop="endTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="培训时长" prop="duration" min-width="100" />
    <el-table-column label="培训成绩" prop="result" min-width="100" />
    <el-table-column label="证书名称" prop="certificateName" min-width="140" />
    <el-table-column label="备注" prop="remark" min-width="160" />
    <el-table-column label="操作" fixed="right" width="120">
      <template #default="{ row }">
        <el-button v-hasPermi="['hrm:employee:update']" link type="primary" @click="openForm(row)">
          编辑
        </el-button>
        <el-button
          v-hasPermi="['hrm:employee:delete']"
          link
          type="danger"
          @click="handleDelete(row.id)"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 表单弹窗：添加/修改 -->
  <EmployeeTrainingExperienceForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter2 } from '@/utils/formatTime'
import * as TrainingExperienceApi from '@/api/hrm/employee/training-experience'
import EmployeeTrainingExperienceForm from './EmployeeTrainingExperienceForm.vue'

defineOptions({ name: 'HrmEmployeeTrainingExperienceList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<TrainingExperienceApi.HrmEmployeeTrainingExperienceVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await TrainingExperienceApi.getEmployeeTrainingExperienceList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeTrainingExperienceForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: TrainingExperienceApi.HrmEmployeeTrainingExperienceVO) {
  formRef.value?.open(props.employeeId, row)
}

/** 删除按钮操作 */
async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await TrainingExperienceApi.deleteEmployeeTrainingExperience(id)
    message.success('删除成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
