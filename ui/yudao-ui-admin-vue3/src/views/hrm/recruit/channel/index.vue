<template>
  <doc-alert title="【招聘】招聘管理" url="https://doc.iocoder.cn/hrm/recruit/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      class="-mb-15px"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="渠道名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入渠道名称"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
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
          v-hasPermi="['hrm:recruit:channel:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" stripe>
      <el-table-column label="渠道编号" align="center" prop="id" width="120" />
      <el-table-column label="渠道名称" align="center" prop="name" min-width="160" />
      <el-table-column label="系统内置" align="center" prop="systemFlag" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.systemFlag ? 'success' : 'info'">
            {{ scope.row.systemFlag ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="CommonStatusEnum.ENABLE"
            :disabled="!checkPermi(['hrm:recruit:channel:update'])"
            :inactive-value="CommonStatusEnum.DISABLE"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="90" />
      <el-table-column
        label="备注"
        align="center"
        prop="remark"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['hrm:recruit:channel:update']"
          >
            编辑
          </el-button>
          <el-button
            v-if="!scope.row.systemFlag"
            link
            type="danger"
            @click="openDeleteForm(scope.row)"
            v-hasPermi="['hrm:recruit:channel:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <RecruitChannelForm ref="formRef" @success="getList" />
  <!-- 表单弹窗：删除 -->
  <RecruitChannelDeleteForm ref="deleteFormRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { CommonStatusEnum } from '@/utils/constants'
import { dateFormatter } from '@/utils/formatTime'
import { checkPermi } from '@/utils/permission'
import * as RecruitChannelApi from '@/api/hrm/recruit/channel'
import type { FormInstance } from 'element-plus'
import RecruitChannelForm from './RecruitChannelForm.vue'
import RecruitChannelDeleteForm from './RecruitChannelDeleteForm.vue'

defineOptions({ name: 'HrmRecruitChannel' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<RecruitChannelApi.HrmRecruitChannelVO[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  status: undefined
})
const queryFormRef = ref<FormInstance>() // 搜索的表单

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await RecruitChannelApi.getRecruitChannelPage(queryParams)
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

const formRef = ref() // 表单 Ref

/** 添加/修改操作 */
function openForm(type: string, id?: number) {
  formRef.value.open(type, id)
}

/** 修改招聘渠道状态 */
async function handleStatusChange(row: RecruitChannelApi.HrmRecruitChannelVO) {
  if (!row.id || row.status === undefined) {
    return
  }
  try {
    // 修改状态的二次确认
    const text = row.status === CommonStatusEnum.ENABLE ? '启用' : '停用'
    await message.confirm('确认要' + text + '招聘渠道"' + row.name + '"吗？')
    // 发起修改状态
    await RecruitChannelApi.updateRecruitChannelStatus({ id: row.id, status: row.status })
    // 刷新列表
    await getList()
  } catch {
    // 取消后，恢复状态开关
    row.status =
      row.status === CommonStatusEnum.ENABLE ? CommonStatusEnum.DISABLE : CommonStatusEnum.ENABLE
  }
}

const deleteFormRef = ref() // 删除表单 Ref

/** 删除按钮操作 */
function openDeleteForm(channel: RecruitChannelApi.HrmRecruitChannelVO) {
  deleteFormRef.value.open(channel)
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
