<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="异动类型" prop="type" width="120">
      <template #default="{ row }">{{ formatEmployeeChangeType(row.type) }}</template>
    </el-table-column>
    <el-table-column label="原部门" prop="oldDeptName" min-width="120" />
    <el-table-column label="新部门" prop="newDeptName" min-width="120" />
    <el-table-column label="原岗位" prop="oldPostName" min-width="120" />
    <el-table-column label="新岗位" prop="newPostName" min-width="120" />
    <el-table-column label="原职级" prop="oldPostLevel" min-width="100" />
    <el-table-column label="新职级" prop="newPostLevel" min-width="100" />
    <el-table-column label="原工作地点" prop="oldWorkAddress" min-width="140" />
    <el-table-column label="新工作地点" prop="newWorkAddress" min-width="140" />
    <el-table-column label="原直属上级" prop="oldLeaderEmployeeName" min-width="120" />
    <el-table-column label="新直属上级" prop="newLeaderEmployeeName" min-width="120" />
    <el-table-column label="生效日期" prop="effectTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="备注" prop="remark" min-width="160" />
  </el-table>

  <!-- 表单弹窗：员工调岗 -->
  <EmployeeTransferForm ref="formRef" @success="handleSuccess" />
</template>

<script lang="ts" setup>
import { dateFormatter2 } from '@/utils/formatTime'
import type { HrmEmployeeVO } from '@/api/hrm/employee'
import * as ChangeRecordApi from '@/api/hrm/employee/change-record'
import { formatEmployeeChangeType } from '@/views/hrm/utils/format'
import EmployeeTransferForm from '../EmployeeTransferForm.vue'

defineOptions({ name: 'HrmEmployeeChangeRecordList' })

const props = defineProps<{
  employee: HrmEmployeeVO
  employeeId: number
}>()

const loading = ref(true) // 列表的加载中
const list = ref<ChangeRecordApi.HrmEmployeeChangeRecordVO[]>([]) // 列表的数据

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于异动生效后刷新员工详情

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await ChangeRecordApi.getEmployeeChangeRecordList(props.employeeId)
  } finally {
    loading.value = false
  }
}
defineExpose({ getList }) // 提供 getList 方法，用于外部异动成功后刷新列表

const formRef = ref<InstanceType<typeof EmployeeTransferForm>>() // 表单 Ref

/** 新增操作 */
function openForm() {
  formRef.value?.open(props.employee)
}

/** 异动保存成功 */
async function handleSuccess() {
  await getList()
  // 发送操作成功的事件
  emit('success')
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
