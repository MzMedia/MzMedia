<template>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" border>
    <el-table-column label="字段分组" prop="groupName" width="180" />
    <el-table-column label="字段名称" prop="title" min-width="220" />
    <el-table-column label="员工是否可见" align="center" width="160">
      <template #default="scope">
        <el-switch
          v-model="scope.row.visible"
          :disabled="scope.row.visibleLocked"
          @change="handleVisibleChange(scope.row)"
        />
      </template>
    </el-table-column>
    <el-table-column label="员工是否可编辑" align="center" width="160">
      <template #default="scope">
        <el-switch
          v-model="scope.row.editable"
          :disabled="!scope.row.visible || scope.row.editableLocked"
          @change="handleEditableChange(scope.row)"
        />
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import * as EmployeeConfigApi from '@/api/hrm/employee/config'

defineOptions({ name: 'HrmEmployeeArchiveFieldConfig' })

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<EmployeeConfigApi.HrmEmployeeFieldConfigVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await EmployeeConfigApi.getEmployeeArchiveFieldConfigList()
  } finally {
    loading.value = false
  }
}

/** 切换显示状态 */
function handleVisibleChange(field: EmployeeConfigApi.HrmEmployeeFieldConfigVO) {
  if (!field.visible) {
    field.editable = false
  }
}

/** 切换编辑状态 */
function handleEditableChange(field: EmployeeConfigApi.HrmEmployeeFieldConfigVO) {
  if (field.editable) {
    field.visible = true
  }
}

/** 保存字段配置 */
async function submitForm() {
  // 提交请求
  await EmployeeConfigApi.saveEmployeeArchiveFieldConfig(list.value)
  message.success('保存成功')
  await getList()
}

defineExpose({ submitForm }) // 提供 submitForm 方法，用于保存字段配置

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
