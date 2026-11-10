<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="联系人" prop="name" min-width="110" />
    <el-table-column label="关系" prop="relation" min-width="100" />
    <el-table-column label="电话" prop="phone" min-width="130" />
    <el-table-column label="工作单位" prop="workUnit" min-width="150" />
    <el-table-column label="职务" prop="postName" min-width="120" />
    <el-table-column label="地址" prop="address" min-width="180" />
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
  <EmployeeContactForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import * as ContactApi from '@/api/hrm/employee/contact'
import EmployeeContactForm from './EmployeeContactForm.vue'

defineOptions({ name: 'HrmEmployeeContactList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<ContactApi.HrmEmployeeContactVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await ContactApi.getEmployeeContactList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeContactForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: ContactApi.HrmEmployeeContactVO) {
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
    await ContactApi.deleteEmployeeContact(id)
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
