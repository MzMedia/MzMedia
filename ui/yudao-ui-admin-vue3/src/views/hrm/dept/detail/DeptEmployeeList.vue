<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="员工搜索" prop="search">
        <el-input
          v-model="queryParams.search"
          clearable
          placeholder="请输入员工姓名、工号或手机号"
          class="!w-280px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column label="员工姓名" align="center" prop="name" min-width="120">
        <template #default="{ row }">
          <el-link type="primary" underline="never" @click="openEmployeeDetail(row.id)">
            {{ row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="工号" align="center" prop="jobNumber" min-width="120">
        <template #default="{ row }">{{ row.jobNumber || '-' }}</template>
      </el-table-column>
      <el-table-column label="部门" align="center" prop="deptName" min-width="140">
        <template #default="{ row }">{{ row.deptName || '-' }}</template>
      </el-table-column>
      <el-table-column label="岗位" align="center" prop="postName" min-width="140">
        <template #default="{ row }">{{ row.postName || '-' }}</template>
      </el-table-column>
      <el-table-column label="聘用形式" align="center" prop="type" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.type != null" :type="DICT_TYPE.HRM_EMPLOYEE_TYPE" :value="row.type" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        label="入职时间"
        align="center"
        prop="entryTime"
        width="180"
        :formatter="dateFormatter"
      />
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as EmployeeApi from '@/api/hrm/employee'
import { HrmEmployeeStatusTab } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmDeptEmployeeList' })

const props = defineProps<{
  deptId: number
}>()

const router = useRouter() // 路由
const loading = ref(true) // 员工列表加载中
const total = ref(0) // 员工总数
const list = ref<EmployeeApi.HrmEmployeeVO[]>([]) // 员工列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  search: undefined as string | undefined,
  deptId: props.deptId,
  statusCategory: HrmEmployeeStatusTab.ACTIVE
})
const queryFormRef = ref<FormInstance>() // 搜索表单 Ref

/** 查询员工列表 */
async function getList() {
  loading.value = true
  try {
    const data = await EmployeeApi.getEmployeePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索员工 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置员工搜索 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 打开员工档案详情 */
function openEmployeeDetail(id?: number) {
  if (id === undefined) {
    return
  }
  router.push({ name: 'HrmEmployeeDetail', params: { id } })
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
