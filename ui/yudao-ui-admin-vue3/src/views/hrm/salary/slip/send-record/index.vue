<template>
  <doc-alert title="【薪资】月度工资、工资条" url="https://doc.iocoder.cn/hrm/salary/payroll/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="84px"
    >
      <el-form-item label="工资月份" prop="month">
        <el-date-picker
          v-model="queryParams.month"
          class="!w-180px"
          clearable
          placeholder="请选择工资月份"
          type="month"
          value-format="YYYY-MM"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 工资条发放记录 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="工资月份" width="120">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">
            {{ formatHrmYearMonth(scope.row.year, scope.row.month) }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="创建人" min-width="120" prop="creatorName" show-overflow-tooltip />
      <el-table-column
        align="center"
        label="发放时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column align="center" label="工资表总人数" prop="employeeCount" width="130" />
      <el-table-column align="center" label="发放人数" prop="sendEmployeeCount" width="110" />
      <el-table-column align="center" label="已查看人数" prop="readCount" width="110" />
      <el-table-column align="center" fixed="right" label="操作" width="140">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">详情</el-button>
          <el-button
            v-hasPermi="['hrm:salary:slip:delete']"
            link
            type="danger"
            @click="handleDeleteRecord(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
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
import { dateFormatter, formatDate } from '@/utils/formatTime'
import * as SalarySlipSendRecordApi from '@/api/hrm/salary/slip/send-record'
import { formatHrmYearMonth } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmSalarySlipSendRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter() // 路由
const loading = ref(false) // 列表加载中
const total = ref(0) // 列表总数
const list = ref<SalarySlipSendRecordApi.SalarySlipSendRecordVO[]>([]) // 发放记录列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  month: formatDate(new Date(), 'YYYY-MM')
}) // 查询参数
const queryFormRef = ref<FormInstance>() // 查询表单

/** 获得工资条发放记录 */
async function getList() {
  loading.value = true
  try {
    const [year, month] = queryParams.month ? queryParams.month.split('-').map(Number) : []
    const params = {
      pageNo: queryParams.pageNo,
      pageSize: queryParams.pageSize,
      year,
      month
    }
    const data = await SalarySlipSendRecordApi.getSalarySlipSendRecordPage(params)
    list.value = data.list
    total.value = data.total
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
  queryParams.month = formatDate(new Date(), 'YYYY-MM')
  handleQuery()
}

/** 打开工资条详情 */
function openDetail(id?: number) {
  if (!id) {
    return
  }
  push({ name: 'HrmSalarySlipSendRecordDetail', params: { id } })
}

/** 删除发放记录 */
async function handleDeleteRecord(id?: number) {
  if (!id) {
    return
  }
  try {
    await message.delConfirm('删除后，本次发放的工资条将同时删除，是否继续？')
    await SalarySlipSendRecordApi.deleteSalarySlipSendRecord(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
