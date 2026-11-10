<template>
  <Dialog v-model="dialogVisible" title="发送工资条" top="4vh" width="1180">
    <el-steps :active="currentStep" align-center class="mx-auto mb-24px max-w-680px">
      <el-step title="设置工资条模板" />
      <el-step title="选择发放员工" />
    </el-steps>

    <!-- 第一步：设置工资条模板 -->
    <div v-if="currentStep === 0" v-loading="templateLoading">
      <el-form label-width="100px">
        <el-form-item label="工资条模板" required>
          <div class="flex w-full items-center gap-12px">
            <el-select
              v-model="selectedTemplateId"
              class="flex-1"
              filterable
              placeholder="请选择工资条模板"
              @change="handleTemplateChange"
            >
              <el-option
                v-for="template in templateList"
                :key="template.id"
                :label="template.name"
                :value="template.id as number"
              />
            </el-select>
            <el-button
              v-hasPermi="['hrm:salary:slip:update']"
              plain
              type="primary"
              @click="templateFormRef?.open('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" />新增模板
            </el-button>
            <el-button
              v-hasPermi="['hrm:salary:slip:update']"
              :disabled="!selectedTemplate || selectedTemplate.defaultStatus"
              @click="templateFormRef?.open('update', selectedTemplateId)"
            >
              <Icon class="mr-5px" icon="ep:edit" />编辑模板
            </el-button>
            <el-button
              v-hasPermi="['hrm:salary:slip:delete']"
              :disabled="!selectedTemplate || selectedTemplate.defaultStatus"
              type="danger"
              @click="handleDeleteTemplate(selectedTemplateId)"
            >
              <Icon class="mr-5px" icon="ep:delete" />删除模板
            </el-button>
          </div>
        </el-form-item>
        <el-empty v-if="!sendTemplate" description="暂无工资条模板，请先新增模板" />
        <template v-else>
          <el-form-item label="隐藏空项">
            <el-switch
              v-model="sendTemplate.hideEmpty"
              active-text="隐藏金额为空的工资项"
              inactive-text="保留全部工资项"
            />
          </el-form-item>
          <el-form-item label="模板明细">
            <SalarySlipTemplateOptionEditor
              ref="templateEditorRef"
              v-model="sendTemplate.options"
              :max-height="320"
            >
              <template #actions>
                <el-button
                  v-hasPermi="['hrm:salary:slip:update']"
                  plain
                  @click="handleSaveAsTemplate"
                >
                  <Icon class="mr-5px" icon="ep:document-copy" />另存为模板
                </el-button>
              </template>
            </SalarySlipTemplateOptionEditor>
          </el-form-item>
        </template>
      </el-form>
    </div>

    <!-- 第二步：选择发放员工 -->
    <el-form v-else v-loading="sendLoading" label-width="100px">
      <el-form-item label="员工筛选">
        <div class="flex w-full items-center gap-12px">
          <el-input
            v-model="queryParams.search"
            class="!w-220px"
            clearable
            placeholder="请输入员工姓名"
            @keyup.enter="handleQuery"
          />
          <DeptSelect v-model="queryParams.deptId" class="!w-220px" />
          <el-select v-model="queryParams.sent" class="!w-150px" clearable placeholder="发送状态">
            <el-option label="未发送" :value="false" />
            <el-option label="已发送" :value="true" />
          </el-select>
          <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
          <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        </div>
      </el-form-item>
      <el-form-item label="发放员工">
        <div class="w-full">
          <el-table
            ref="employeeTableRef"
            v-loading="employeeLoading"
            :data="employeeList"
            border
            max-height="320"
            row-key="employeeId"
            @selection-change="handleSelectionChange"
          >
            <el-table-column :reserve-selection="true" type="selection" width="45" />
            <el-table-column
              label="员工"
              min-width="120"
              prop="employeeName"
              show-overflow-tooltip
            />
            <el-table-column label="工号" prop="jobNumber" width="110" />
            <el-table-column label="部门" min-width="130" prop="deptName" show-overflow-tooltip />
            <el-table-column label="岗位" min-width="130" prop="postName" show-overflow-tooltip />
            <el-table-column label="手机号" prop="mobile" width="130" />
            <el-table-column align="center" label="发送状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.sent ? 'success' : 'info'">
                  {{ scope.row.sent ? '已发送' : '未发送' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column align="right" label="应发工资" width="120">
              <template #default="scope">
                {{ formatHrmMoney(scope.row.expectedPaySalary) }}
              </template>
            </el-table-column>
            <el-table-column align="right" label="实发工资" width="120">
              <template #default="scope">{{ formatHrmMoney(scope.row.realPaySalary) }}</template>
            </el-table-column>
          </el-table>
          <Pagination
            v-model:limit="queryParams.pageSize"
            v-model:page="queryParams.pageNo"
            :total="employeeTotal"
            @pagination="loadEmployees"
          />
        </div>
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <template v-if="currentStep === 0">
        <el-button :disabled="templateLoading" type="primary" @click="handleNextStep">
          下一步
        </el-button>
      </template>
      <template v-else>
        <span class="mr-12px text-gray-500">已选 {{ selectedEmployeeIds.length }} 人</span>
        <el-button :disabled="sendLoading" @click="currentStep = 0">上一步</el-button>
        <el-button :disabled="sendLoading" type="primary" @click="submitForm(false)">
          发放已选员工
        </el-button>
        <el-button
          :disabled="sendLoading || !employeeTotal"
          type="primary"
          @click="submitForm(true)"
        >
          全部发放
        </el-button>
      </template>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>

  <!-- 工资条模板表单 -->
  <SalarySlipTemplateForm ref="templateFormRef" @success="handleTemplateSuccess" />
</template>

<script lang="ts" setup>
import * as SalarySlipSendRecordApi from '@/api/hrm/salary/slip/send-record'
import * as SalarySlipTemplateApi from '@/api/hrm/salary/slip/template'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import { formatHrmMoney } from '@/views/hrm/utils/format'
import { HrmSalaryOptionCategoryCode } from '@/views/hrm/utils/constants'
import SalarySlipTemplateForm from '../template/SalarySlipTemplateForm.vue'
import SalarySlipTemplateOptionEditor from '../template/SalarySlipTemplateOptionEditor.vue'

defineOptions({ name: 'HrmSalarySlipSendForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const dialogVisible = ref(false) // 弹窗是否显示
const currentStep = ref(0) // 当前步骤
const sendLoading = ref(false) // 发放中
const templateLoading = ref(false) // 模板加载中
const employeeLoading = ref(false) // 员工列表加载中
const employeeLoaded = ref(false) // 是否已加载员工列表
const employeeTotal = ref(0) // 员工总数
const monthRecordId = ref<number>() // 工资表编号
const employeeList = ref<SalarySlipSendRecordApi.SalarySlipSendEmployeeVO[]>([]) // 待发员工
const selectedEmployeeIdSet = ref<Set<number>>(new Set()) // 已选员工编号
const templateList = ref<SalarySlipTemplateApi.SalarySlipTemplateVO[]>([]) // 工资条模板
const selectedTemplateId = ref<number>() // 选中的模板编号
const sendTemplate = ref<SalarySlipTemplateApi.SalarySlipTemplateVO>() // 本次发放的模板快照
const templateFormRef = ref<InstanceType<typeof SalarySlipTemplateForm>>() // 模板表单
const templateEditorRef = ref<InstanceType<typeof SalarySlipTemplateOptionEditor>>() // 模板明细
const employeeTableRef = ref() // 员工表格
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  search: undefined as string | undefined,
  deptId: undefined as number | undefined,
  sent: false as boolean | undefined
}) // 员工筛选
const selectedEmployeeIds = computed(() => Array.from(selectedEmployeeIdSet.value)) // 选中的员工编号

/** 当前选中的工资条模板 */
const selectedTemplate = computed(() =>
  templateList.value.find((template) => template.id === selectedTemplateId.value)
)

/** 打开发放弹窗 */
async function open(recordId: number) {
  monthRecordId.value = recordId
  dialogVisible.value = true
  currentStep.value = 0
  selectedTemplateId.value = undefined
  sendTemplate.value = undefined
  employeeLoaded.value = false
  employeeList.value = []
  employeeTotal.value = 0
  selectedEmployeeIdSet.value = new Set()
  employeeTableRef.value?.clearSelection()
  resetQueryParams()
  await loadTemplates()
}
defineExpose({ open }) // 提供 open 方法，用于打开发放弹窗

/** 加载工资条模板 */
async function loadTemplates(preferredId?: number) {
  templateLoading.value = true
  try {
    const currentTemplateId = preferredId || selectedTemplateId.value
    templateList.value = await SalarySlipTemplateApi.getSalarySlipTemplateList()
    selectedTemplateId.value =
      templateList.value.find((template) => template.id === currentTemplateId)?.id ||
      templateList.value.find((template) => template.defaultStatus)?.id ||
      templateList.value[0]?.id
    handleTemplateChange(selectedTemplateId.value)
  } finally {
    templateLoading.value = false
  }
}

/** 进入员工选择步骤 */
async function handleNextStep() {
  if (!sendTemplate.value) {
    message.warning('请先选择或新增工资条模板')
    return
  }
  const validateMessage = templateEditorRef.value?.validate()
  if (validateMessage) {
    message.warning(validateMessage)
    return
  }
  currentStep.value = 1
  if (!employeeLoaded.value) {
    await loadEmployees()
  }
}

/** 加载待发员工 */
async function loadEmployees() {
  if (!monthRecordId.value) {
    return
  }
  employeeLoading.value = true
  try {
    const params = {
      monthRecordId: monthRecordId.value,
      ...queryParams
    }
    const data = await SalarySlipSendRecordApi.getSalarySlipSendEmployeePage(params)
    employeeList.value = data.list
    employeeTotal.value = data.total
    await nextTick()
    employeeList.value.forEach((row) => {
      employeeTableRef.value?.toggleRowSelection(
        row,
        selectedEmployeeIdSet.value.has(row.employeeId)
      )
    })
    employeeLoaded.value = true
  } finally {
    employeeLoading.value = false
  }
}

/** 搜索待发员工 */
function handleQuery() {
  queryParams.pageNo = 1
  loadEmployees()
}

/** 重置员工筛选 */
function resetQuery() {
  resetQueryParams()
  loadEmployees()
}

/** 重置员工筛选参数 */
function resetQueryParams() {
  queryParams.pageNo = 1
  queryParams.pageSize = 10
  queryParams.search = undefined
  queryParams.deptId = undefined
  queryParams.sent = false
}

/** 选择发放员工 */
function handleSelectionChange(rows: SalarySlipSendRecordApi.SalarySlipSendEmployeeVO[]) {
  employeeList.value.forEach((row) => selectedEmployeeIdSet.value.delete(row.employeeId))
  rows.forEach((row) => selectedEmployeeIdSet.value.add(row.employeeId))
  selectedEmployeeIdSet.value = new Set(selectedEmployeeIdSet.value)
}

const emit = defineEmits(['success']) // 定义 success 事件

/** 发放工资条 */
async function submitForm(all: boolean) {
  if (!monthRecordId.value || !sendTemplate.value || (!all && !selectedEmployeeIds.value.length)) {
    message.warning('请选择发放员工')
    return
  }
  sendLoading.value = true
  try {
    // 提交请求
    await SalarySlipSendRecordApi.sendSalarySlip({
      monthRecordId: monthRecordId.value,
      hideEmpty: Boolean(sendTemplate.value.hideEmpty),
      options: getTemplateOptions(),
      all,
      employeeIds: all ? undefined : selectedEmployeeIds.value,
      search: all ? queryParams.search : undefined,
      deptId: all ? queryParams.deptId : undefined,
      sent: all ? queryParams.sent : undefined
    })
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    sendLoading.value = false
  }
}

/** 工资条模板保存成功 */
async function handleTemplateSuccess(id: number) {
  await loadTemplates(id)
}

/** 切换工资条模板 */
function handleTemplateChange(id?: number) {
  const template = templateList.value.find((item) => item.id === id)
  sendTemplate.value = template
    ? {
        ...template,
        options: (template.options || []).map((item) => ({
          ...item,
          parentCode:
            item.parentCode === HrmSalaryOptionCategoryCode.ROOT ? undefined : item.parentCode
        }))
      }
    : undefined
}

/** 将当前快照另存为工资条模板 */
async function handleSaveAsTemplate() {
  if (!sendTemplate.value) {
    return
  }
  try {
    const result = await message.prompt('请输入新模板名称', '另存为模板')
    const name = result.value.trim()
    if (!name) {
      message.warning('模板名称不能为空')
      return
    }
    if (name.length > 64) {
      message.warning('模板名称不能超过 64 个字符')
      return
    }
    const id = await SalarySlipTemplateApi.createSalarySlipTemplate({
      name,
      hideEmpty: Boolean(sendTemplate.value.hideEmpty),
      options: getTemplateOptions()
    })
    message.success(t('common.createSuccess'))
    await loadTemplates(id)
  } catch {}
}

/** 删除工资条模板 */
async function handleDeleteTemplate(id?: number) {
  if (!id) {
    return
  }
  try {
    await message.delConfirm()
    await SalarySlipTemplateApi.deleteSalarySlipTemplate(id)
    message.success(t('common.delSuccess'))
    await loadTemplates()
  } catch {}
}

/** 获得本次发放的模板明细 */
function getTemplateOptions() {
  return templateEditorRef.value?.getNormalizedOptions() || []
}
</script>
