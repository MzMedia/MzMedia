<template>
  <doc-alert
    title="【设置】币别、科目、辅助核算、初始余额"
    url="https://doc.iocoder.cn/fms/config/accounting/"
  />
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form class="-mb-15px" :inline="true" label-width="68px">
      <el-form-item label="科目类别">
        <el-select v-model="subjectType" class="!w-240px">
          <el-option
            v-for="item in subjectTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          v-hasPermi="['fms:config:subject:create']"
          plain
          type="primary"
          @click="openForm('create')"
          v-if="fmsStore.isAccountSetWritable"
        >
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
        <el-button
          v-hasPermi="['fms:config:subject:import']"
          plain
          type="warning"
          @click="handleImport"
          v-if="fmsStore.isAccountSetWritable"
        >
          <Icon class="mr-5px" icon="ep:upload" />导入
        </el-button>
        <el-button
          v-hasPermi="['fms:config:subject:export']"
          :loading="exportLoading"
          plain
          type="success"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" />导出
        </el-button>
        <el-dropdown
          v-if="
            fmsStore.isAccountSetWritable &&
            (checkPermi(['fms:config:subject:update']) || checkPermi(['fms:config:subject:delete']))
          "
          :disabled="selectedRows.length === 0"
          class="ml-12px"
          @command="handleBatchCommand"
        >
          <el-button :disabled="selectedRows.length === 0" :loading="batchLoading" plain>
            <Icon class="mr-5px" icon="ep:operation" />批量操作
            <Icon class="ml-5px" icon="ep:arrow-down" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="checkPermi(['fms:config:subject:update'])" command="enable">
                批量启用
              </el-dropdown-item>
              <el-dropdown-item v-if="checkPermi(['fms:config:subject:update'])" command="disable">
                批量禁用
              </el-dropdown-item>
              <el-dropdown-item
                v-if="checkPermi(['fms:config:subject:delete'])"
                command="delete"
                divided
              >
                批量删除
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :tree-props="{ children: 'children' }"
      border
      default-expand-all
      row-key="id"
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="fmsStore.isAccountSetWritable" type="selection" width="48" />
      <el-table-column label="编码" min-width="130" prop="code" />
      <el-table-column label="名称" min-width="190" prop="name" show-overflow-tooltip />
      <el-table-column label="类别" min-width="120">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.FMS_SUBJECT_CATEGORY"
            :value="`${scope.row.type}-${scope.row.category}`"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="余额方向" width="90">
        <template #default="scope">
          <dict-tag
            :type="DICT_TYPE.FMS_DEBIT_CREDIT_DIRECTION"
            :value="scope.row.balanceDirection"
          />
        </template>
      </el-table-column>
      <el-table-column label="辅助核算" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.auxiliaryTypeNames?.join('、') }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="数量" width="90">
        <template #default="scope">
          {{ scope.row.quantityAccounting ? scope.row.quantityUnit : '' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="现金项" width="90">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.cash" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="FMS_SUBJECT_STATUS.ENABLED"
            :disabled="!fmsStore.isAccountSetWritable || !checkPermi(['fms:config:subject:update'])"
            :inactive-value="FMS_SUBJECT_STATUS.DISABLED"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="190">
        <template #default="scope">
          <el-button
            v-hasPermi="['fms:config:subject:update']"
            link
            type="primary"
            @click="openForm('update', scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['fms:config:subject:create']"
            link
            type="primary"
            @click="openForm('create', undefined, scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            新建下级
          </el-button>
          <el-button
            v-hasPermi="['fms:config:subject:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 添加或修改科目 -->
  <FmsSubjectForm ref="formRef" @success="getList" />
  <!-- 科目导入 -->
  <FmsSubjectImportForm ref="importFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import download from '@/utils/download'
import { checkPermi } from '@/utils/permission'
import { handleTree } from '@/utils/tree'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_SUBJECT_STATUS, FMS_SUBJECT_TYPE } from '@/views/fms/utils/constants'
import FmsSubjectForm from './FmsSubjectForm.vue'
import FmsSubjectImportForm from './FmsSubjectImportForm.vue'

defineOptions({ name: 'FmsSubject' })

const subjectTypeOptions = getIntDictOptions(DICT_TYPE.FMS_SUBJECT_TYPE)

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const fmsStore = useFmsStore() // FMS 状态

const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const subjectType = ref<number>(FMS_SUBJECT_TYPE.ASSET) // 当前科目类型
const loading = ref(false) // 列表的加载中
const list = ref<FmsSubjectVO[]>([]) // 科目树列表
const selectedRows = ref<FmsSubjectVO[]>([]) // 选中的科目
const batchLoading = ref(false) // 批量操作的加载中

watch(subjectType, () => getList())
watch(accountSetId, () => getList())

/** 查询科目列表 */
async function getList() {
  if (!accountSetId.value) {
    list.value = []
    return
  }
  loading.value = true
  try {
    list.value = handleTree(
      await FmsSubjectApi.getSubjectList(accountSetId.value, subjectType.value)
    )
    selectedRows.value = []
  } finally {
    loading.value = false
  }
}

/** 添加/修改操作 */
const formRef = ref<InstanceType<typeof FmsSubjectForm>>() // 科目表单 Ref
function openForm(type: string, row?: FmsSubjectVO, parent?: FmsSubjectVO) {
  const parentSubject = type === 'update' ? findSubjectById(list.value, row?.parentId) : parent
  formRef.value?.open(type, subjectType.value, row, parentSubject)
}

/** 科目导入 */
const importFormRef = ref<InstanceType<typeof FmsSubjectImportForm>>() // 科目导入表单 Ref
function handleImport() {
  if (!accountSetId.value) return
  importFormRef.value?.open(accountSetId.value)
}

/** 修改科目状态 */
async function handleStatusChange(row: FmsSubjectVO) {
  if (!accountSetId.value || row.status === undefined) return
  try {
    const text = row.status === FMS_SUBJECT_STATUS.ENABLED ? '启用' : '禁用'
    await message.confirm(`确认要${text}“${row.code} ${row.name}”科目吗？`)
    await FmsSubjectApi.updateSubjectStatus({
      accountSetId: accountSetId.value,
      ids: [row.id],
      status: row.status
    })
    await getList()
  } catch {
    row.status =
      row.status === FMS_SUBJECT_STATUS.ENABLED
        ? FMS_SUBJECT_STATUS.DISABLED
        : FMS_SUBJECT_STATUS.ENABLED
  }
}

/** 批量修改科目状态 */
async function handleBatchStatus(status: number) {
  if (!accountSetId.value || selectedRows.value.length === 0) return
  const text = status === FMS_SUBJECT_STATUS.ENABLED ? '启用' : '禁用'
  try {
    await message.confirm(`确认要${text}选中的 ${selectedRows.value.length} 个科目吗？`)
    batchLoading.value = true
    await FmsSubjectApi.updateSubjectStatus({
      accountSetId: accountSetId.value,
      ids: selectedRows.value.map((item) => item.id),
      status
    })
    message.success(t('common.updateSuccess'))
    await getList()
  } catch {
  } finally {
    batchLoading.value = false
  }
}

/** 删除科目 */
async function handleDelete(row: FmsSubjectVO) {
  if (!accountSetId.value) return
  try {
    await message.delConfirm(`确认删除科目“${row.code} ${row.name}”吗？`)
    await FmsSubjectApi.deleteSubjectList(accountSetId.value, [row.id])
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 批量删除科目 */
async function handleBatchDelete() {
  if (!accountSetId.value || selectedRows.value.length === 0) return
  try {
    await message.delConfirm(`确认删除选中的 ${selectedRows.value.length} 个科目吗？`)
    batchLoading.value = true
    await FmsSubjectApi.deleteSubjectList(
      accountSetId.value,
      selectedRows.value.map((item) => item.id)
    )
    message.success(t('common.delSuccess'))
    await getList()
  } catch {
  } finally {
    batchLoading.value = false
  }
}

/** 记录表格选中项 */
function handleSelectionChange(rows: FmsSubjectVO[]) {
  selectedRows.value = rows
}

/** 批量操作 */
async function handleBatchCommand(command: string) {
  if (command === 'enable') {
    await handleBatchStatus(FMS_SUBJECT_STATUS.ENABLED)
    return
  }
  if (command === 'disable') {
    await handleBatchStatus(FMS_SUBJECT_STATUS.DISABLED)
    return
  }
  if (command === 'delete') {
    await handleBatchDelete()
  }
}

/** 导出科目 */
const exportLoading = ref(false) // 导出的加载中
async function handleExport() {
  if (!accountSetId.value || exportLoading.value) return
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await FmsSubjectApi.exportSubject(accountSetId.value, subjectType.value)
    download.excel(data, '科目设置.xlsx')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 根据编号查找科目树节点 */
function findSubjectById(subjects: FmsSubjectVO[], id?: number): FmsSubjectVO | undefined {
  if (!id) return undefined
  for (const subject of subjects) {
    if (subject.id === id) return subject
    const child = findSubjectById(subject.children || [], id)
    if (child) return child
  }
  return undefined
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
