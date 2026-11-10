<!-- CRM 业绩目标设置 -->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="年份" prop="year">
        <el-date-picker
          v-model="queryParams.year"
          class="!w-240px"
          type="year"
          value-format="YYYY"
          @change="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标类型" prop="bizType">
        <el-select
          v-model="queryParams.bizType"
          clearable
          class="!w-240px"
          placeholder="请选择目标类型"
          @change="handleQuery"
        >
          <el-option
            v-for="item in bizTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="对象类型" prop="objectType">
        <el-select
          v-model="queryParams.objectType"
          clearable
          class="!w-240px"
          placeholder="请选择对象类型"
          @change="handleObjectTypeChange"
        >
          <el-option
            v-for="item in objectTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="queryParams.objectType === PerformanceConfigObjectTypeEnum.DEPT"
        label="部门"
        prop="objectId"
      >
        <el-tree-select
          v-model="queryParams.objectId"
          :data="deptList"
          :props="defaultProps"
          check-strictly
          clearable
          class="!w-240px"
          node-key="id"
          placeholder="请选择部门"
          @change="handleQuery"
        />
      </el-form-item>
      <el-form-item
        v-if="queryParams.objectType === PerformanceConfigObjectTypeEnum.USER"
        label="员工"
        prop="objectId"
      >
        <el-select
          v-model="queryParams.objectId"
          clearable
          filterable
          class="!w-240px"
          placeholder="请选择员工"
          @change="handleQuery"
        >
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
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
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['crm:performance-config:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="年份" align="center" prop="year" width="90" fixed />
      <el-table-column label="对象类型" align="center" prop="objectType" width="100" fixed>
        <template #default="{ row }">{{ getObjectTypeLabel(row.objectType) }}</template>
      </el-table-column>
      <el-table-column label="目标对象" align="center" prop="objectName" min-width="140" fixed />
      <el-table-column label="目标类型" align="center" prop="bizType" width="100">
        <template #default="{ row }">{{ getBizTypeLabel(row.bizType) }}</template>
      </el-table-column>
      <el-table-column
        v-for="item in monthFields"
        :key="item.prop"
        :label="item.label"
        align="right"
        :prop="item.prop"
        :formatter="erpPriceTableColumnFormatter"
        width="120"
      />
      <el-table-column
        label="年度目标"
        align="right"
        prop="yearTargetPrice"
        :formatter="erpPriceTableColumnFormatter"
        width="140"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column label="操作" align="center" fixed="right" width="120">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            @click="openForm('update', row.id)"
            v-hasPermi="['crm:performance-config:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row.id)"
            v-hasPermi="['crm:performance-config:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <PerformanceConfigForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import {
  PerformanceConfigObjectTypeEnum,
  PerformanceConfigApi,
  CrmPerformanceConfigVO
} from '@/api/crm/performance/config'
import { BizTypeEnum } from '@/api/crm/permission'
import PerformanceConfigForm from './PerformanceConfigForm.vue'
import { dateFormatter } from '@/utils/formatTime'
import { defaultProps, handleTree } from '@/utils/tree'
import { erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmPerformanceConfig' })

const message = useMessage()
const { t } = useI18n()

// 目标类型选项
const bizTypeOptions = [
  { label: '销售目标', value: BizTypeEnum.CRM_CONTRACT },
  { label: '回款目标', value: BizTypeEnum.CRM_RECEIVABLE }
]
// 目标对象类型选项
const objectTypeOptions = [
  { label: '部门', value: PerformanceConfigObjectTypeEnum.DEPT },
  { label: '员工', value: PerformanceConfigObjectTypeEnum.USER }
]
// 月度目标字段
const monthFields = [
  { label: '一月', prop: 'januaryTargetPrice' },
  { label: '二月', prop: 'februaryTargetPrice' },
  { label: '三月', prop: 'marchTargetPrice' },
  { label: '四月', prop: 'aprilTargetPrice' },
  { label: '五月', prop: 'mayTargetPrice' },
  { label: '六月', prop: 'juneTargetPrice' },
  { label: '七月', prop: 'julyTargetPrice' },
  { label: '八月', prop: 'augustTargetPrice' },
  { label: '九月', prop: 'septemberTargetPrice' },
  { label: '十月', prop: 'octoberTargetPrice' },
  { label: '十一月', prop: 'novemberTargetPrice' },
  { label: '十二月', prop: 'decemberTargetPrice' }
] as const

const loading = ref(false) // 加载中
const total = ref(0) // 列表总数
const list = ref<CrmPerformanceConfigVO[]>([]) // 列表的数据
const queryFormRef = ref() // 搜索的表单
const deptList = ref<Tree[]>([]) // 部门树形结构
const userList = ref<UserApi.UserVO[]>([]) // 全量用户清单
const queryParams = reactive<any>({
  pageNo: 1,
  pageSize: 10,
  year: String(new Date().getFullYear()),
  bizType: undefined,
  objectType: undefined,
  objectId: undefined
})

/** 获取接口参数 */
const getApiParams = () => ({
  ...queryParams,
  year: queryParams.year ? Number(queryParams.year) : undefined
})

/** 查询业绩目标列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PerformanceConfigApi.getPerformanceConfigPage(getApiParams())
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 对象类型变化时重置目标对象 */
const handleObjectTypeChange = () => {
  queryParams.objectId = undefined
  handleQuery()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref() // 表单 Ref
/** 打开表单 */
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除业绩目标 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await PerformanceConfigApi.deletePerformanceConfig(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 获取目标类型名称 */
const getBizTypeLabel = (value: number) =>
  bizTypeOptions.find((item) => item.value === value)?.label || ''

/** 获取对象类型名称 */
const getObjectTypeLabel = (value: number) =>
  objectTypeOptions.find((item) => item.value === value)?.label || ''

/** 初始化 */
onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
  userList.value = await UserApi.getSimpleUserList()
  await getList()
})
</script>
