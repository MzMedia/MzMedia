<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="证书名称" prop="name" min-width="150" />
    <el-table-column label="证书级别" prop="level" min-width="110" />
    <el-table-column label="证书编号" prop="no" min-width="140" />
    <el-table-column
      label="有效开始日期"
      prop="startTime"
      width="120"
      :formatter="dateFormatter2"
    />
    <el-table-column label="有效结束日期" prop="endTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="发证机构" prop="issuingAuthority" min-width="150" />
    <el-table-column label="发证日期" prop="issuingTime" width="120" :formatter="dateFormatter2" />
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
  <EmployeeCertificateForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter2 } from '@/utils/formatTime'
import * as CertificateApi from '@/api/hrm/employee/certificate'
import EmployeeCertificateForm from './EmployeeCertificateForm.vue'

defineOptions({ name: 'HrmEmployeeCertificateList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<CertificateApi.HrmEmployeeCertificateVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await CertificateApi.getEmployeeCertificateList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeCertificateForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: CertificateApi.HrmEmployeeCertificateVO) {
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
    await CertificateApi.deleteEmployeeCertificate(id)
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
