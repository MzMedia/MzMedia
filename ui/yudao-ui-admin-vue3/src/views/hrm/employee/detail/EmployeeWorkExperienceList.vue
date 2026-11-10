<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="工作单位" prop="workUnit" min-width="160" />
    <el-table-column label="职务" prop="postName" min-width="120" />
    <el-table-column label="开始日期" prop="startTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="结束日期" prop="endTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="离职原因" prop="reason" min-width="150" />
    <el-table-column label="证明人" prop="witnessName" min-width="100" />
    <el-table-column label="证明人电话" prop="witnessPhone" min-width="130" />
    <el-table-column label="工作备注" prop="remark" min-width="160" />
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
  <EmployeeWorkExperienceForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter2 } from '@/utils/formatTime'
import * as WorkExperienceApi from '@/api/hrm/employee/work-experience'
import EmployeeWorkExperienceForm from './EmployeeWorkExperienceForm.vue'

defineOptions({ name: 'HrmEmployeeWorkExperienceList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<WorkExperienceApi.HrmEmployeeWorkExperienceVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await WorkExperienceApi.getEmployeeWorkExperienceList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeWorkExperienceForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: WorkExperienceApi.HrmEmployeeWorkExperienceVO) {
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
    await WorkExperienceApi.deleteEmployeeWorkExperience(id)
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
