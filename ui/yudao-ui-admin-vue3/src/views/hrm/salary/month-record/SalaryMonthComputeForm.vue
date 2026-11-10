<template>
  <Dialog v-model="dialogVisible" title="核算工资表" width="760">
    <el-form v-loading="formLoading" label-width="112px">
      <!-- 核算配置 -->
      <el-form-item label="工资表">
        <span>{{ currentRecord?.title || '-' }}</span>
      </el-form-item>
      <el-form-item label="计薪人员">
        <span>{{ payrollEmployeeCount }} 人</span>
      </el-form-item>
      <el-form-item label="社保数据">
        <el-switch
          v-model="syncInsuranceData"
          active-text="从社保表同步"
          inactive-text="本次不带入"
        />
      </el-form-item>
      <el-form-item label="同步考勤">
        <el-switch
          v-model="syncAttendanceData"
          active-text="从考勤统计同步"
          inactive-text="使用导入文件"
        />
      </el-form-item>
      <!-- 考勤数据导入 -->
      <el-form-item label="考勤数据">
        <div class="flex flex-wrap items-start gap-8px">
          <el-upload
            v-model:file-list="attendanceFiles"
            :auto-upload="false"
            :disabled="syncAttendanceData"
            :limit="1"
            accept=".xls,.xlsx"
          >
            <el-button :disabled="syncAttendanceData">
              <Icon icon="ep:upload" class="mr-5px" /> 选择文件
            </el-button>
          </el-upload>
          <el-button plain @click="downloadTemplate('attendance')">
            <Icon icon="ep:download" class="mr-5px" /> 下载模板
          </el-button>
        </div>
      </el-form-item>
      <!-- 上月个税累计导入 -->
      <el-form-item label="上月个税累计">
        <div class="flex flex-wrap items-start gap-8px">
          <el-upload
            v-model:file-list="cumulativeTaxFiles"
            :auto-upload="false"
            :limit="1"
            accept=".xls,.xlsx"
          >
            <el-button><Icon icon="ep:upload" class="mr-5px" /> 选择文件</el-button>
          </el-upload>
          <el-button plain @click="downloadTemplate('cumulativeTax')">
            <Icon icon="ep:download" class="mr-5px" /> 下载模板
          </el-button>
        </div>
      </el-form-item>
      <!-- 专项附加扣除导入 -->
      <el-form-item label="专项附加扣除">
        <div class="flex flex-wrap items-start gap-8px">
          <el-upload
            v-model:file-list="additionalDeductionFiles"
            :auto-upload="false"
            :limit="1"
            accept=".xls,.xlsx"
          >
            <el-button><Icon icon="ep:upload" class="mr-5px" /> 选择文件</el-button>
          </el-upload>
          <el-button plain @click="downloadTemplate('additionalDeduction')">
            <Icon icon="ep:download" class="mr-5px" /> 下载模板
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { UploadUserFile } from 'element-plus'
import * as SalaryMonthRecordApi from '@/api/hrm/salary/month-record'
import download from '@/utils/download'

defineOptions({ name: 'HrmSalaryMonthComputeForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const dialogVisible = ref(false) // 弹窗是否显示
const formLoading = ref(false) // 表单加载中
const currentRecord = ref<SalaryMonthRecordApi.SalaryMonthRecordVO>() // 当前工资表
const payrollEmployeeCount = ref(0) // 当前可计薪人数
const syncInsuranceData = ref(true) // 是否同步社保数据
const syncAttendanceData = ref(false) // 是否同步考勤数据
const attendanceFiles = ref<UploadUserFile[]>([]) // 考勤数据文件
const cumulativeTaxFiles = ref<UploadUserFile[]>([]) // 上月个税累计文件
const additionalDeductionFiles = ref<UploadUserFile[]>([]) // 专项附加扣除文件

/** 打开弹窗 */
function open(record: SalaryMonthRecordApi.SalaryMonthRecordVO) {
  currentRecord.value = record
  payrollEmployeeCount.value = record.employeeCount || 0
  attendanceFiles.value = []
  cumulativeTaxFiles.value = []
  additionalDeductionFiles.value = []
  syncInsuranceData.value = true
  syncAttendanceData.value = false
  dialogVisible.value = true
  getPayrollEmployeeCount()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件

/** 查询当前可计薪人数 */
async function getPayrollEmployeeCount() {
  const readiness = await SalaryMonthRecordApi.getSalaryPayrollReadiness(currentRecord.value?.id)
  payrollEmployeeCount.value = readiness.payrollEmployeeCount || 0
}

/** 下载导入模板 */
async function downloadTemplate(type: 'attendance' | 'cumulativeTax' | 'additionalDeduction') {
  const monthRecordId = currentRecord.value?.id
  if (type === 'attendance') {
    const data = await SalaryMonthRecordApi.getSalaryAttendanceImportTemplate(monthRecordId)
    download.excel(data, '月度工资考勤导入模板.xls')
    return
  }
  if (type === 'cumulativeTax') {
    const data = await SalaryMonthRecordApi.getSalaryCumulativeTaxImportTemplate(monthRecordId)
    download.excel(data, '月度工资上月个税累计导入模板.xls')
    return
  }
  const data = await SalaryMonthRecordApi.getSalaryAdditionalDeductionImportTemplate(monthRecordId)
  download.excel(data, '月度工资专项附加扣除导入模板.xls')
}

/** 添加导入文件到表单数据 */
function appendFile(formData: FormData, field: string, files: UploadUserFile[]) {
  const raw = files[0]?.raw
  if (raw) {
    formData.append(field, raw)
  }
}

/** 提交核算 */
async function submitForm() {
  if (!currentRecord.value?.id) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    const formData = new FormData()
    formData.append('id', String(currentRecord.value.id))
    formData.append('syncInsuranceData', String(syncInsuranceData.value))
    formData.append('syncAttendanceData', String(syncAttendanceData.value))
    appendFile(formData, 'attendanceFile', attendanceFiles.value)
    appendFile(formData, 'cumulativeTaxFile', cumulativeTaxFiles.value)
    appendFile(formData, 'additionalDeductionFile', additionalDeductionFiles.value)
    await SalaryMonthRecordApi.computeSalaryMonthRecordWithImport(formData)
    message.success(t('common.updateSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
