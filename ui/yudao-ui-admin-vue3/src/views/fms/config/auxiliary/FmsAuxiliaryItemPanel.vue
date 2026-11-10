<template>
  <!-- 搜索工作栏 -->
  <el-form ref="queryFormRef" class="-mb-15px" :inline="true" :model="queryParams">
    <el-form-item label="关键词" prop="search">
      <el-input
        v-model="queryParams.search"
        class="!w-240px"
        clearable
        placeholder="请输入编码或名称"
        @keyup.enter="handleQuery"
      />
    </el-form-item>
    <el-form-item>
      <el-button @click="handleQuery"> <Icon class="mr-5px" icon="ep:search" /> 搜索 </el-button>
      <el-button @click="resetQuery"> <Icon class="mr-5px" icon="ep:refresh" /> 重置 </el-button>
      <el-button
        v-hasPermi="['fms:config:auxiliary:create']"
        :disabled="!auxiliaryType"
        plain
        type="primary"
        @click="openForm()"
        v-if="fmsStore.isAccountSetWritable"
      >
        <Icon class="mr-5px" icon="ep:plus" /> 新增项目
      </el-button>
      <el-button
        v-hasPermi="['fms:config:auxiliary:import']"
        :disabled="!auxiliaryType"
        plain
        type="warning"
        @click="handleImport"
        v-if="fmsStore.isAccountSetWritable"
      >
        <Icon class="mr-5px" icon="ep:upload" /> 导入
      </el-button>
      <el-button
        v-hasPermi="['fms:config:auxiliary:export']"
        :disabled="!auxiliaryType"
        :loading="exportLoading"
        plain
        type="success"
        @click="handleExport"
      >
        <Icon class="mr-5px" icon="ep:download" /> 导出
      </el-button>
      <el-button
        v-hasPermi="['fms:config:auxiliary:delete']"
        :disabled="checkedIds.length === 0"
        plain
        type="danger"
        @click="handleDeleteBatch"
        v-if="fmsStore.isAccountSetWritable"
      >
        <Icon class="mr-5px" icon="ep:delete" /> 批量删除
      </el-button>
    </el-form-item>
  </el-form>

  <!-- 列表 -->
  <el-table
    v-loading="loading"
    :data="list"
    :show-overflow-tooltip="true"
    stripe
    @selection-change="handleRowCheckboxChange"
  >
    <el-table-column
      v-if="fmsStore.isAccountSetWritable && checkPermi(['fms:config:auxiliary:delete'])"
      type="selection"
      width="55"
    />
    <el-table-column label="编码" min-width="130" prop="code" />
    <el-table-column label="名称" min-width="180" prop="name" />
    <el-table-column label="备注" min-width="180" prop="remark" />
    <template v-if="isInventory">
      <el-table-column label="规格" min-width="130" prop="specification" />
      <el-table-column label="单位" min-width="100" prop="unit" />
    </template>
    <el-table-column label="状态" width="90">
      <template #default="scope">
        <el-switch
          v-model="scope.row.status"
          :active-value="CommonStatusEnum.ENABLE"
          :disabled="!fmsStore.isAccountSetWritable || !checkPermi(['fms:config:auxiliary:update'])"
          :inactive-value="CommonStatusEnum.DISABLE"
          @change="handleStatusChange(scope.row)"
        />
      </template>
    </el-table-column>
    <el-table-column
      v-if="
        fmsStore.isAccountSetWritable &&
        (checkPermi(['fms:config:auxiliary:update']) || checkPermi(['fms:config:auxiliary:delete']))
      "
      fixed="right"
      label="操作"
      width="120"
    >
      <template #default="scope">
        <el-button
          v-hasPermi="['fms:config:auxiliary:update']"
          link
          type="primary"
          @click="openForm(scope.row)"
          v-if="fmsStore.isAccountSetWritable"
        >
          编辑
        </el-button>
        <el-button
          v-hasPermi="['fms:config:auxiliary:delete']"
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

  <Pagination
    v-model:limit="queryParams.pageSize"
    v-model:page="queryParams.pageNo"
    :total="total"
    @pagination="getList"
  />

  <!-- 表单弹窗：添加/修改 -->
  <FmsAuxiliaryItemForm ref="formRef" @success="getList" />
  <!-- 导入弹窗 -->
  <FmsAuxiliaryItemImportForm ref="importFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { FmsAuxiliaryItemApi } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryItemPageReqVO, FmsAuxiliaryItemVO } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { CommonStatusEnum } from '@/utils/constants'
import download from '@/utils/download'
import { checkPermi } from '@/utils/permission'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_AUXILIARY_TYPE } from '@/views/fms/utils/constants'
import FmsAuxiliaryItemForm from './item/FmsAuxiliaryItemForm.vue'
import FmsAuxiliaryItemImportForm from './item/FmsAuxiliaryItemImportForm.vue'

defineOptions({ name: 'FmsAuxiliaryItemPanel' })

const fmsStore = useFmsStore() // FMS 状态

const props = defineProps<{
  accountSetId?: number
  auxiliaryType?: FmsAuxiliaryTypeVO
}>()

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<FmsAuxiliaryItemVO[]>([]) // 辅助核算项目列表
const queryParams = reactive<FmsAuxiliaryItemPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  accountSetId: 0,
  auxiliaryTypeId: 0,
  search: ''
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const checkedIds = ref<number[]>([]) // 选中的编号数组
const formRef = ref<InstanceType<typeof FmsAuxiliaryItemForm>>() // 表单 Ref
const importFormRef = ref<InstanceType<typeof FmsAuxiliaryItemImportForm>>() // 导入表单 Ref

const isInventory = computed(() => props.auxiliaryType?.type === FMS_AUXILIARY_TYPE.INVENTORY) // 是否存货类别

/** 查询辅助核算项目列表 */
async function getList() {
  if (!queryParams.accountSetId || !queryParams.auxiliaryTypeId) {
    list.value = []
    total.value = 0
    loading.value = false
    return
  }
  loading.value = true
  try {
    const data = await FmsAuxiliaryItemApi.getAuxiliaryItemPage(queryParams)
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

/** 打开项目表单 */
function openForm(row?: FmsAuxiliaryItemVO) {
  if (!props.auxiliaryType) return
  formRef.value?.open(props.auxiliaryType, row)
}

/** 修改辅助核算项目状态 */
async function handleStatusChange(row: FmsAuxiliaryItemVO) {
  try {
    // 修改状态的二次确认
    const text = row.status === CommonStatusEnum.ENABLE ? '启用' : '停用'
    await message.confirm(`确认要“${text}”“${row.name}”辅助核算项目吗？`)
    // 发起修改状态
    await FmsAuxiliaryItemApi.updateAuxiliaryItemStatus(row.accountSetId, row.id!, row.status!)
    // 刷新列表
    await getList()
  } catch {
    // 取消后，进行恢复按钮
    row.status =
      row.status === CommonStatusEnum.ENABLE ? CommonStatusEnum.DISABLE : CommonStatusEnum.ENABLE
  }
}

/** 删除项目 */
async function handleDelete(row: FmsAuxiliaryItemVO) {
  try {
    await message.delConfirm(`确认删除辅助核算项目“${row.code} ${row.name}”吗？`)
    await FmsAuxiliaryItemApi.deleteAuxiliaryItemList(row.accountSetId, [row.id!])
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 批量删除项目 */
async function handleDeleteBatch() {
  try {
    await message.delConfirm(`确认删除选中的 ${checkedIds.value.length} 个辅助核算项目吗？`)
    await FmsAuxiliaryItemApi.deleteAuxiliaryItemList(queryParams.accountSetId, checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 打开导入弹窗 */
function handleImport() {
  if (!props.auxiliaryType) return
  importFormRef.value?.open(queryParams.accountSetId, props.auxiliaryType)
}

/** 导出项目 */
async function handleExport() {
  if (!props.auxiliaryType) return
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await FmsAuxiliaryItemApi.exportAuxiliaryItem(queryParams)
    download.excel(data, `${props.auxiliaryType.name}.xlsx`)
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 选中项目 */
function handleRowCheckboxChange(rows: FmsAuxiliaryItemVO[]) {
  checkedIds.value = rows.map((item) => item.id!)
}

/** 监听账套和辅助核算类别切换 */
watch(
  [() => props.accountSetId, () => props.auxiliaryType?.id],
  async ([accountSetId, auxiliaryTypeId]) => {
    queryParams.accountSetId = accountSetId || 0
    queryParams.auxiliaryTypeId = auxiliaryTypeId || 0
    queryParams.pageNo = 1
    queryParams.search = ''
    checkedIds.value = []
    await getList()
  },
  { immediate: true }
)
</script>
