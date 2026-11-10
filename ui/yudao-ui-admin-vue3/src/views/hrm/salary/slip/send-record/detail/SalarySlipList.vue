<template>
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="84px"
    >
      <el-form-item label="员工" prop="search">
        <el-input
          v-model="queryParams.search"
          class="!w-220px"
          clearable
          placeholder="请输入员工姓名或工号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <DeptSelect v-model="queryParams.deptId" class="!w-220px" />
      </el-form-item>
      <el-form-item label="查看状态" prop="readStatus">
        <el-select
          v-model="queryParams.readStatus"
          class="!w-160px"
          clearable
          placeholder="请选择查看状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_SALARY_SLIP_READ_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          class="!w-200px"
          clearable
          placeholder="请输入备注"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 工资条列表 -->
  <ContentWrap>
    <div v-if="selectedRows.length" class="mb-12px flex flex-wrap items-center gap-8px">
      <span class="text-14px text-gray-500">已选择 {{ selectedRows.length }} 项</span>
      <el-button
        v-hasPermi="['hrm:salary:slip:update']"
        type="primary"
        plain
        @click="handleBatchRemark(false)"
      >
        <Icon icon="ep:edit" class="mr-5px" />编辑备注
      </el-button>
      <el-button v-hasPermi="['hrm:salary:slip:update']" plain @click="handleBatchRemark(true)">
        <Icon icon="ep:delete" class="mr-5px" />清除备注
      </el-button>
    </div>
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="list"
      row-key="id"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="45" />
      <el-table-column label="员工姓名" prop="employeeName" min-width="130" show-overflow-tooltip />
      <el-table-column label="工号" prop="jobNumber" min-width="120" show-overflow-tooltip />
      <el-table-column label="部门" prop="deptName" min-width="140" show-overflow-tooltip />
      <el-table-column label="岗位" prop="postName" min-width="140" show-overflow-tooltip />
      <el-table-column label="手机号" prop="mobile" width="130" />
      <el-table-column label="查看状态" align="center" prop="readStatus" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_SALARY_SLIP_READ_STATUS" :value="scope.row.readStatus" />
        </template>
      </el-table-column>
      <el-table-column label="实发工资" align="right" prop="realPaySalary" width="130">
        <template #default="scope">{{ formatHrmMoney(scope.row.realPaySalary) }}</template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">查看明细</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 工资条明细 -->
  <SalarySlipDetail ref="detailRef" />
</template>

<script lang="ts" setup>
import type { FormInstance, TableInstance } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as SalarySlipApi from '@/api/hrm/salary/slip'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import { useBatchOperation } from '@/views/hrm/utils/batch'
import { formatHrmMoney } from '@/views/hrm/utils/format'
import SalarySlipDetail from './SalarySlipDetail.vue'

defineOptions({ name: 'HrmSalarySlipList' })

const props = defineProps<{
  sendRecordId: number
}>()

const message = useMessage() // 消息弹窗
const { executeBatch } = useBatchOperation() // 批量操作
const loading = ref(false) // 列表加载中
const total = ref(0) // 列表总数
const list = ref<SalarySlipApi.SalarySlipVO[]>([]) // 工资条列表
const selectedRows = ref<SalarySlipApi.SalarySlipVO[]>([]) // 已选工资条
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sendRecordId: props.sendRecordId,
  search: undefined as string | undefined,
  deptId: undefined as number | undefined,
  readStatus: undefined as number | undefined,
  remark: undefined as string | undefined
}) // 查询参数
const queryFormRef = ref<FormInstance>() // 查询表单
const tableRef = ref<TableInstance>() // 工资条表格
const detailRef = ref<InstanceType<typeof SalarySlipDetail>>() // 工资条明细

/** 查询工资条列表 */
async function getList() {
  loading.value = true
  try {
    const data = await SalarySlipApi.getSalarySlipPage(queryParams)
    list.value = data.list
    total.value = data.total
    selectedRows.value = []
    tableRef.value?.clearSelection()
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置搜索 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 选择工资条 */
function handleSelectionChange(rows: SalarySlipApi.SalarySlipVO[]) {
  selectedRows.value = rows
}

/** 批量修改备注 */
async function handleBatchRemark(clear: boolean) {
  let remark = ''
  try {
    if (clear) {
      await message.confirm('确认清除所选工资条的备注？')
    } else {
      const result = await message.prompt('请输入备注', '编辑备注')
      remark = result.value
      if (remark.length > 500) {
        message.warning('备注不能超过 500 个字符')
        return
      }
    }
    const success = await executeBatch(
      selectedRows.value
        .filter((item): item is SalarySlipApi.SalarySlipVO & { id: number } => !!item.id)
        .map((item) => SalarySlipApi.updateSalarySlipRemark({ id: item.id, remark }))
    )
    if (success) {
      await getList()
    }
  } catch {}
}

/** 打开工资条明细 */
function openDetail(id?: number) {
  detailRef.value?.open(id)
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
