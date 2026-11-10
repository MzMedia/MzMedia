<template>
  <Dialog v-model="dialogVisible" :title="title" width="960" align-center append-to-body>
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form :inline="true" :model="queryParams" class="-mb-15px" label-width="72px">
        <el-form-item label="员工姓名">
          <el-input
            v-model="queryParams.name"
            class="!w-200px"
            clearable
            placeholder="请输入员工姓名"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="工号">
          <el-input
            v-model="queryParams.jobNumber"
            class="!w-180px"
            clearable
            placeholder="请输入工号"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="部门">
          <DeptSelect v-model="queryParams.deptId" class="!w-200px" />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery">
            <Icon class="mr-5px" icon="ep:search" />
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <Icon class="mr-5px" icon="ep:refresh" />
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <!-- 列表 -->
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="list"
        :highlight-current-row="!multiple"
        :show-overflow-tooltip="true"
        :stripe="true"
        row-key="id"
        @row-click="handleRowClick"
        @row-dblclick="handleRowDblClick"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-if="multiple"
          :reserve-selection="true"
          :selectable="rowSelectable"
          align="center"
          type="selection"
          width="50"
        />
        <el-table-column v-else align="center" width="50">
          <template #default="{ row }">
            <el-radio
              v-model="selectedRadioId"
              class="radio-no-label"
              :disabled="row.disabled"
              :value="row.id"
              @change="selectedRadioRow = row"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="员工姓名" min-width="120" prop="name" />
        <el-table-column align="center" label="工号" min-width="110" prop="jobNumber" />
        <el-table-column align="center" label="部门" min-width="120" prop="deptName">
          <template #default="{ row }">{{ row.deptName || '-' }}</template>
        </el-table-column>
        <el-table-column align="center" label="手机号" min-width="130" prop="mobile" />
        <el-table-column align="center" label="入职状态" min-width="100" prop="entryStatus">
          <template #default="{ row }">
            <dict-tag :type="DICT_TYPE.HRM_EMPLOYEE_ENTRY_STATUS" :value="row.entryStatus" />
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>

    <template #footer>
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as EmployeeApi from '@/api/hrm/employee'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'

defineOptions({ name: 'HrmEmployeeSelectDialog' })

type EmployeeSelectRow = EmployeeApi.HrmEmployeeVO & {
  id: number
  disabled?: boolean
}

const props = withDefaults(
  defineProps<{
    title?: string
    multiple?: boolean
    entryStatus?: number
    selectable?: (employee: EmployeeApi.HrmEmployeeVO) => boolean
  }>(),
  {
    title: '选择员工',
    multiple: false
  }
)

const emit = defineEmits<{
  selected: [rows: EmployeeApi.HrmEmployeeVO[]]
}>() // 定义 selected 事件，用于返回选中的员工

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<EmployeeSelectRow[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  jobNumber: undefined,
  deptId: undefined,
  entryStatus: props.entryStatus
})

// ==================== 选中状态 ====================

const tableRef = ref() // 表格 Ref
const selectedRowMap = ref(new Map<number, EmployeeSelectRow>()) // 多选模式：选中行
const selectedRadioId = ref<number>() // 单选模式：选中 ID
const selectedRadioRow = ref<EmployeeSelectRow>() // 单选模式：选中行
const disabledIds = ref<number[]>([]) // 禁选员工 ID

// ==================== 员工查询 ====================

/** 转换员工选择行数据 */
function convertEmployeeSelectRow(
  employee?: EmployeeApi.HrmEmployeeVO
): EmployeeSelectRow | undefined {
  if (!employee || employee.id == null) {
    return undefined
  }
  return {
    id: employee.id,
    name: employee.name,
    jobNumber: employee.jobNumber,
    mobile: employee.mobile,
    deptId: employee.deptId,
    deptName: employee.deptName,
    postName: employee.postName,
    postLevel: employee.postLevel,
    leaderEmployeeId: employee.leaderEmployeeId,
    leaderEmployeeName: employee.leaderEmployeeName,
    entryStatus: employee.entryStatus,
    status: employee.status,
    disabled:
      disabledIds.value.includes(employee.id) ||
      (props.entryStatus != null && employee.entryStatus !== props.entryStatus) ||
      (props.selectable ? !props.selectable(employee) : false)
  }
}

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await EmployeeApi.getEmployeeSimplePage(queryParams)
    list.value = data.list
      .map(convertEmployeeSelectRow)
      .filter((item): item is EmployeeSelectRow => !!item)
    total.value = data.total
    await nextTick()
    applyCurrentPageSelection()
  } finally {
    loading.value = false
  }
}

// ==================== 选中操作 ====================

/** 回显当前页的选中状态 */
function applyCurrentPageSelection() {
  if (!props.multiple) {
    const selectedRow = list.value.find((row) => row.id === selectedRadioId.value)
    if (selectedRow) {
      selectedRadioRow.value = selectedRow
    }
    return
  }
  list.value.forEach((row) => {
    tableRef.value?.toggleRowSelection(row, selectedRowMap.value.has(row.id))
  })
}

/** 处理多选变化 */
function handleSelectionChange(rows: EmployeeSelectRow[]) {
  if (!props.multiple) {
    return
  }
  list.value.forEach((row) => selectedRowMap.value.delete(row.id))
  rows.forEach((row) => selectedRowMap.value.set(row.id, row))
}

/** 判断员工是否允许选择 */
function rowSelectable(row: EmployeeSelectRow) {
  return !row.disabled
}

/** 处理单击行 */
function handleRowClick(row: EmployeeSelectRow) {
  if (props.multiple || row.disabled) {
    return
  }
  selectedRadioId.value = row.id
  selectedRadioRow.value = row
}

/** 处理双击行 */
function handleRowDblClick(row: EmployeeSelectRow) {
  if (row.disabled) {
    return
  }
  if (props.multiple) {
    tableRef.value?.toggleRowSelection(row)
    return
  }
  selectedRadioId.value = row.id
  selectedRadioRow.value = row
  confirmSelect()
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.name = undefined
  queryParams.jobNumber = undefined
  queryParams.deptId = undefined
  handleQuery()
}

/** 确认选择 */
function confirmSelect() {
  const rows = props.multiple ? Array.from(selectedRowMap.value.values()) : [selectedRadioRow.value]
  const selectedRows = rows.filter((row): row is EmployeeSelectRow => !!row)
  if (!selectedRows.length) {
    message.warning('请选择员工')
    return
  }
  emit('selected', selectedRows)
  dialogVisible.value = false
}

/** 打开弹窗 */
async function open(selectedIds: number[] = [], selectedDisabledIds: number[] = []) {
  dialogVisible.value = true
  disabledIds.value = selectedDisabledIds
  list.value = []
  total.value = 0
  selectedRowMap.value = new Map()
  const enabledSelectedIds = selectedIds.filter((id) => !selectedDisabledIds.includes(id))
  selectedRadioId.value = props.multiple ? undefined : enabledSelectedIds[0]
  selectedRadioRow.value = undefined
  tableRef.value?.clearSelection()
  resetQueryParams()
  loading.value = true
  try {
    const selectedRows = enabledSelectedIds.length
      ? await EmployeeApi.getEmployeeSimpleList(enabledSelectedIds)
      : []
    selectedRows.map(convertEmployeeSelectRow).forEach((row) => {
      if (row) {
        selectedRowMap.value.set(row.id, row)
      }
    })
    if (!props.multiple) {
      selectedRadioRow.value = convertEmployeeSelectRow(selectedRows[0])
    }
    await getList()
  } finally {
    loading.value = false
  }
}

/** 重置查询参数 */
function resetQueryParams() {
  queryParams.pageNo = 1
  queryParams.pageSize = 10
  queryParams.name = undefined
  queryParams.jobNumber = undefined
  queryParams.deptId = undefined
  queryParams.entryStatus = props.entryStatus
}

defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>

<style lang="scss" scoped>
.radio-no-label {
  :deep(.el-radio__label) {
    display: none;
  }
}
</style>
