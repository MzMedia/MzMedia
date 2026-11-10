<template>
  <doc-alert title="【考勤】考勤管理" url="https://doc.iocoder.cn/hrm/attendance/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="72px"
    >
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="queryParams.date"
          type="daterange"
          value-format="YYYY-MM-DD"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          class="!w-360px"
        />
      </el-form-item>
      <el-form-item label="日期类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择日期类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_HOLIDAY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['hrm:attendance:holiday:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" width="100" />
      <el-table-column label="日期" align="center" prop="date" min-width="180">
        <template #default="scope">{{ formatHrmDate(scope.row.date) }}</template>
      </el-table-column>
      <el-table-column label="日期类型" align="center" prop="type" width="140">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.HRM_ATTENDANCE_HOLIDAY_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['hrm:attendance:holiday:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['hrm:attendance:holiday:delete']"
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

  <!-- 表单弹窗：添加/修改 -->
  <AttendanceHolidayForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter, getDateRangeFromArray } from '@/utils/formatTime'
import * as AttendanceHolidayApi from '@/api/hrm/attendance/holiday'
import { formatHrmDate } from '@/views/hrm/utils/format'
import AttendanceHolidayForm from './AttendanceHolidayForm.vue'

/** 考勤节假日列表 */
defineOptions({ name: 'HrmAttendanceHoliday' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总数
const list = ref<AttendanceHolidayApi.HrmAttendanceHolidayVO[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  date: undefined as string[] | undefined,
  type: undefined as number | undefined
})
const queryFormRef = ref() // 搜索表单 Ref

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      date: getDateRangeFromArray(queryParams.date)
    }
    const data = await AttendanceHolidayApi.getAttendanceHolidayPage(params)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const formRef = ref<InstanceType<typeof AttendanceHolidayForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

/** 删除按钮操作 */
async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await AttendanceHolidayApi.deleteAttendanceHoliday(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
