<template>
  <doc-alert title="【社保】社保管理" url="https://doc.iocoder.cn/hrm/insurance/" />

  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <el-date-picker
        v-model="queryYear"
        :clearable="false"
        class="!w-140px"
        format="YYYY 年"
        type="year"
        value-format="YYYY"
        @change="getList()"
      />
      <el-button
        v-hasPermi="['hrm:insurance:month-record:create']"
        :loading="createLoading"
        plain
        type="primary"
        @click="handleCreate"
      >
        <Icon icon="ep:plus" class="mr-5px" />
        {{ latestRecord ? '新建次月社保表' : '新建首月社保表' }}
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column fixed="left" label="社保表" min-width="190" prop="title">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.title }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="参保人数" prop="insuredEmployeeCount" width="100" />
      <el-table-column align="center" label="停保人数" prop="stoppedEmployeeCount" width="100" />
      <el-table-column align="right" label="个人社保" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.personalInsuranceAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="公司社保" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.corporateInsuranceAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="个人公积金" width="130">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.personalProvidentFundAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="公司公积金" width="130">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.corporateProvidentFundAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="80">
        <template #default="scope">
          <el-button
            v-if="isLatestEditableRecord(scope.row)"
            v-hasPermi="['hrm:insurance:month-record:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <InsuranceFirstMonthForm ref="firstMonthFormRef" @success="handleCreateFirstSuccess" />
</template>

<script lang="ts" setup>
import * as InsuranceMonthRecordApi from '@/api/hrm/insurance/month-record'
import { HrmInsuranceMonthStatus } from '@/views/hrm/utils/constants'
import { formatHrmMoney } from '@/views/hrm/utils/format'
import InsuranceFirstMonthForm from './InsuranceFirstMonthForm.vue'

defineOptions({ name: 'HrmInsuranceMonthRecord' })

const message = useMessage() // 消息弹窗
const { push } = useRouter() // 路由操作

const loading = ref(true) // 列表的加载中
const createLoading = ref(false) // 新建次月社保表的加载中
const queryYear = ref(String(new Date().getFullYear())) // 查询年份
const list = ref<InsuranceMonthRecordApi.InsuranceMonthRecordVO[]>([]) // 月度社保表列表
const latestRecord = ref<InsuranceMonthRecordApi.InsuranceMonthRecordVO>() // 最近月度社保表
const firstMonthFormRef = ref<InstanceType<typeof InsuranceFirstMonthForm>>() // 首月社保表单 Ref

/** 获得月度社保表列表 */
async function getList(useLatestYear = false) {
  loading.value = true
  try {
    latestRecord.value = await InsuranceMonthRecordApi.getLastInsuranceMonthRecord()
    if (useLatestYear && latestRecord.value?.year) {
      queryYear.value = String(latestRecord.value.year)
    }
    list.value = await InsuranceMonthRecordApi.getInsuranceMonthRecordList(Number(queryYear.value))
  } finally {
    loading.value = false
  }
}

/** 打开月度社保表详情 */
function openDetail(id?: number) {
  if (!id) {
    return
  }
  push({
    name: 'HrmInsuranceMonthRecordDetail',
    params: {
      id
    }
  })
}

/** 新建月度社保表 */
function handleCreate() {
  if (!latestRecord.value) {
    firstMonthFormRef.value?.open()
    return
  }
  handleCreateNext()
}

/** 首月社保表创建成功 */
function handleCreateFirstSuccess(year: number) {
  queryYear.value = String(year)
  getList()
}

/** 新建次月社保表 */
async function handleCreateNext() {
  try {
    await message.confirm('新建次月社保后，本月数据将不可修改。请确认要新建次月社保吗？')
    createLoading.value = true
    const id = await InsuranceMonthRecordApi.createNextInsuranceMonthRecord()
    message.success('新建成功')
    openDetail(id)
  } finally {
    createLoading.value = false
  }
}

/** 删除最新月度社保表 */
async function handleDelete(row: InsuranceMonthRecordApi.InsuranceMonthRecordVO) {
  if (!row.id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm(`确认删除“${row.title}”吗？`)
    // 发起删除
    await InsuranceMonthRecordApi.deleteInsuranceMonthRecord(row.id)
    message.success('删除成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 是否为可删除的最近社保表 */
function isLatestEditableRecord(row: InsuranceMonthRecordApi.InsuranceMonthRecordVO) {
  return row.id === latestRecord.value?.id && row.status === HrmInsuranceMonthStatus.UNARCHIVED
}

/** 初始化 */
onMounted(() => {
  getList(true)
})
</script>
