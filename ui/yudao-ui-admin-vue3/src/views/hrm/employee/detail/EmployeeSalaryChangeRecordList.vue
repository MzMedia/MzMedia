<template>
  <ContentWrap title="定薪/调薪记录">
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column label="生效日期" prop="effectTime" width="120" :formatter="dateFormatter2" />
      <el-table-column label="类型" prop="recordType" width="90">
        <template #default="scope">
          {{ scope.row.recordType === HrmSalaryRecordType.FIXED ? '定薪' : '调薪' }}
        </template>
      </el-table-column>
      <el-table-column label="原因" prop="changeReason" width="90">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_SALARY_CHANGE_REASON" :value="scope.row.changeReason" />
        </template>
      </el-table-column>
      <el-table-column label="调整前" align="right" prop="beforeTotal" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.beforeTotal) }}</template>
      </el-table-column>
      <el-table-column label="调整后" align="right" prop="afterTotal" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.afterTotal) }}</template>
      </el-table-column>
      <el-table-column label="试用调整前" align="right" prop="probationBeforeTotal" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.probationBeforeTotal) }}</template>
      </el-table-column>
      <el-table-column label="试用调整后" align="right" prop="probationAfterTotal" width="120">
        <template #default="scope">{{ formatHrmMoney(scope.row.probationAfterTotal) }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_SALARY_CHANGE_RECORD_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="160" />
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import * as SalaryChangeRecordApi from '@/api/hrm/salary/change-record'
import { HrmSalaryRecordType } from '@/views/hrm/utils/constants'
import { formatHrmMoney } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmEmployeeSalaryChangeRecordList' })

const props = defineProps<{
  employeeId: number
}>()

const loading = ref(true) // 列表的加载中
const list = ref<SalaryChangeRecordApi.SalaryChangeRecordVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await SalaryChangeRecordApi.getSalaryChangeRecordList(props.employeeId)
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
