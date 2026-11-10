<template>
  <ContentWrap>
    <el-table v-loading="loading" :data="recordList" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="类型" prop="recordType" width="90">
        <template #default="scope">
          {{ scope.row.recordType === HrmSalaryRecordType.FIXED ? '定薪' : '调薪' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="调整原因" prop="changeReason" width="120">
        <template #default="scope">
          <dict-tag
            v-if="scope.row.changeReason != null"
            :type="DICT_TYPE.HRM_SALARY_CHANGE_REASON"
            :value="scope.row.changeReason"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter2"
        align="center"
        label="生效日期"
        prop="effectTime"
        width="120"
      />
      <el-table-column align="right" label="正式调整前" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.beforeTotal) }}</template>
      </el-table-column>
      <el-table-column align="right" label="正式调整后" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.afterTotal) }}</template>
      </el-table-column>
      <el-table-column align="right" label="试用调整前" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.probationBeforeTotal) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="试用调整后" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.probationAfterTotal) }}</template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" width="110">
        <template #default="scope">
          <dict-tag
            v-if="scope.row.status != null"
            :type="DICT_TYPE.HRM_SALARY_CHANGE_RECORD_STATUS"
            :value="scope.row.status"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="160" prop="remark" />
      <el-table-column align="center" fixed="right" label="操作" width="176">
        <template #default="scope">
          <el-button
            v-if="canEditRecord(scope.row)"
            v-hasPermi="['hrm:salary:employee-info:update']"
            link
            type="primary"
            @click="emit('edit', scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.status === HrmSalaryChangeRecordStatus.PENDING"
            v-hasPermi="['hrm:salary:employee-info:update']"
            link
            type="warning"
            @click="handleCancel(scope.row.id)"
          >
            取消
          </el-button>
          <el-button
            v-if="scope.row.status !== HrmSalaryChangeRecordStatus.EFFECTIVE"
            v-hasPermi="['hrm:salary:change-record:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import * as SalaryChangeRecordApi from '@/api/hrm/salary/change-record'
import { HrmSalaryChangeRecordStatus, HrmSalaryRecordType } from '@/views/hrm/utils/constants'
import { formatHrmMoney } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmSalaryChangeRecordList' })

const props = defineProps<{
  employeeId: number
}>()

const emit = defineEmits<{
  (e: 'edit', record: SalaryChangeRecordApi.SalaryChangeRecordVO): void
  (e: 'change'): void
}>() // 定义 edit/change 事件

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(false) // 列表加载中
const recordList = ref<SalaryChangeRecordApi.SalaryChangeRecordVO[]>([]) // 定薪/调薪记录

/** 查询定薪/调薪记录 */
async function getList() {
  loading.value = true
  try {
    recordList.value = await SalaryChangeRecordApi.getSalaryChangeRecordList(props.employeeId)
  } finally {
    loading.value = false
  }
}

/** 判断定薪/调薪记录是否允许编辑 */
function canEditRecord(record: SalaryChangeRecordApi.SalaryChangeRecordVO) {
  if (record.recordType !== HrmSalaryRecordType.FIXED) {
    return record.status !== HrmSalaryChangeRecordStatus.EFFECTIVE
  }
  return !recordList.value.some(
    (item) =>
      item.recordType === HrmSalaryRecordType.CHANGE &&
      item.status !== HrmSalaryChangeRecordStatus.CANCELLED
  )
}

/** 取消待生效调整 */
async function handleCancel(recordId?: number) {
  if (!recordId) {
    return
  }
  try {
    await message.confirm('确认取消该待生效的薪资调整吗？')
    await SalaryChangeRecordApi.cancelSalaryChangeRecord(recordId)
    message.success(t('common.updateSuccess'))
    await getList()
    emit('change')
  } catch {}
}

/** 删除薪资调整记录 */
async function handleDelete(recordId?: number) {
  if (!recordId) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await SalaryChangeRecordApi.deleteSalaryChangeRecord(recordId)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
    emit('change')
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})

defineExpose({ getList }) // 提供 getList 方法，用于刷新列表
</script>
