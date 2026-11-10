<template>
  <doc-alert title="【凭证】凭证管理" url="https://doc.iocoder.cn/fms/voucher/" />
  <!-- 搜索 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="会计期间">
        <el-date-picker
          v-model="monthRange"
          type="monthrange"
          value-format="YYYY-MM"
          start-placeholder="开始月份"
          end-placeholder="结束月份"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="凭证字" prop="voucherWordId">
        <FmsVoucherWordSelect
          v-model="queryParams.voucherWordId"
          :options="voucherWords"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="凭证号" prop="voucherNumber">
        <el-input-number
          v-model="queryParams.voucherNumber"
          :controls="false"
          :min="1"
          class="!w-240px"
          placeholder="请输入凭证号"
        />
      </el-form-item>
      <el-form-item label="摘要" prop="digest">
        <el-input
          v-model="queryParams.digest"
          clearable
          class="!w-240px"
          placeholder="请输入摘要"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="科目" prop="subjectId">
        <FmsSubjectSelect
          v-model="queryParams.subjectId"
          :options="subjects"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="金额">
        <div class="flex !w-240px items-center gap-6px [&_.el-input-number]:!w-106px">
          <el-input-number
            v-model="queryParams.minAmount"
            :controls="false"
            :min="0"
            :precision="2"
            placeholder="最小金额"
          />
          <span>至</span>
          <el-input-number
            v-model="queryParams.maxAmount"
            :controls="false"
            :min="0"
            :precision="2"
            placeholder="最大金额"
          />
        </div>
      </el-form-item>
      <el-form-item label="制单人" prop="creatorUserId">
        <UserSelect v-model="queryParams.creatorUserId" class="!w-240px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable class="!w-240px" placeholder="请选择状态">
          <el-option
            v-for="item in voucherStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="router.push('/fms/voucher/create')"
          v-hasPermi="['fms:voucher:create']"
          v-if="fmsStore.isAccountSetWritable"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-dropdown
          v-if="
            checkPermi([
              'fms:voucher:print',
              'fms:voucher:export',
              'fms:voucher:import',
              'fms:voucher:move',
              'fms:voucher:tidy'
            ])
          "
          class="ml-12px"
          trigger="click"
          @command="handleMoreCommand"
        >
          <el-button>更多<Icon icon="ep:arrow-down" class="ml-5px" /></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="checkPermi(['fms:voucher:print'])" command="print">
                打印凭证
              </el-dropdown-item>
              <el-dropdown-item v-if="checkPermi(['fms:voucher:print'])" command="printList">
                打印列表
              </el-dropdown-item>
              <el-dropdown-item v-if="checkPermi(['fms:voucher:export'])" command="export">
                导出
              </el-dropdown-item>
              <el-dropdown-item
                v-if="fmsStore.isAccountSetWritable && checkPermi(['fms:voucher:import'])"
                command="import"
              >
                导入凭证
              </el-dropdown-item>
              <el-dropdown-item
                v-if="fmsStore.isAccountSetWritable && checkPermi(['fms:voucher:move'])"
                command="move"
                divided
              >
                移动凭证
              </el-dropdown-item>
              <el-dropdown-item
                v-if="fmsStore.isAccountSetWritable && checkPermi(['fms:voucher:tidy'])"
                command="tidy"
              >
                整理凭证
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <div
      v-if="selectedRows.length && fmsStore.isAccountSetWritable"
      class="mb-12px flex min-h-44px items-center gap-10px border border-[var(--el-color-primary-light-7)] border-solid bg-[var(--el-color-primary-light-9)] px-14px text-[var(--el-color-primary)]"
    >
      <span>已选择 {{ selectedRows.length }} 张凭证</span>
      <el-button
        @click="handleBatchReview(FMS_VOUCHER_STATUS.APPROVED)"
        v-hasPermi="['fms:voucher:review']"
      >
        批量审核
      </el-button>
      <el-button
        @click="handleBatchReview(FMS_VOUCHER_STATUS.PENDING_REVIEW)"
        v-hasPermi="['fms:voucher:review']"
      >
        批量反审核
      </el-button>
      <el-button type="danger" @click="handleBatchDelete" v-hasPermi="['fms:voucher:delete']">
        批量删除
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="list"
      stripe
      :show-overflow-tooltip="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        v-if="fmsStore.isAccountSetWritable"
        :selectable="(row) => !row.closingGenerated"
        type="selection"
        width="46"
      />
      <el-table-column
        :formatter="dateFormatter2"
        align="center"
        label="日期"
        prop="voucherTime"
        width="110"
      />
      <el-table-column align="center" label="凭证字号" width="110">
        <template #default="scope">
          <el-button link type="primary" @click="openVoucher(scope.row)">
            {{ scope.row.voucherWordName }}-{{ scope.row.voucherNumber }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="附件" width="72">
        <template #default="scope">
          <el-button
            v-if="scope.row.attachmentUrls?.length || canEditVoucherAttachments(scope.row)"
            link
            type="primary"
            @click="openAttachmentDialog(scope.row)"
          >
            <Icon icon="ep:paperclip" /> {{ scope.row.attachmentUrls?.length || 0 }}
          </el-button>
          <span v-else
            ><Icon icon="ep:paperclip" /> {{ scope.row.attachmentUrls?.length || 0 }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="摘要" min-width="190">
        <template #default="scope">
          <div
            class="[&>div]:min-h-28px [&>div]:overflow-hidden [&>div]:border-b [&>div]:border-[var(--el-border-color-lighter)] [&>div]:border-b-dashed [&>div]:leading-28px [&>div]:text-ellipsis [&>div]:whitespace-nowrap [&>div:last-child]:border-b-0"
          >
            <div v-for="entry in scope.row.entries" :key="entry.id" :title="entry.digest">
              {{ entry.digest }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="会计科目" min-width="230">
        <template #default="scope">
          <div
            class="[&>div]:min-h-28px [&>div]:overflow-hidden [&>div]:border-b [&>div]:border-[var(--el-border-color-lighter)] [&>div]:border-b-dashed [&>div]:leading-28px [&>div]:text-ellipsis [&>div]:whitespace-nowrap [&>div:last-child]:border-b-0"
          >
            <div v-for="entry in scope.row.entries" :key="entry.id" :title="entry.subjectName">
              {{ entry.subjectCode }} {{ entry.subjectName }}
              <span v-if="entry.auxiliaries?.length" class="text-[var(--el-text-color-secondary)]">
                / {{ entry.auxiliaries.map((item: any) => item.name).join('、') }}
              </span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="right" label="借方金额" width="135">
        <template #default="scope">
          <div
            class="[font-family:Arial,sans-serif] [font-variant-numeric:tabular-nums] [&>div]:min-h-28px [&>div]:overflow-hidden [&>div]:border-b [&>div]:border-[var(--el-border-color-lighter)] [&>div]:border-b-dashed [&>div]:leading-28px [&>div]:text-ellipsis [&>div]:whitespace-nowrap [&>div:last-child]:border-b-0"
          >
            <div v-for="entry in scope.row.entries" :key="entry.id">
              {{ Number(entry.debitAmount) ? formatMoney(entry.debitAmount) : '' }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="right" label="贷方金额" width="135">
        <template #default="scope">
          <div
            class="[font-family:Arial,sans-serif] [font-variant-numeric:tabular-nums] [&>div]:min-h-28px [&>div]:overflow-hidden [&>div]:border-b [&>div]:border-[var(--el-border-color-lighter)] [&>div]:border-b-dashed [&>div]:leading-28px [&>div]:text-ellipsis [&>div]:whitespace-nowrap [&>div:last-child]:border-b-0"
          >
            <div v-for="entry in scope.row.entries" :key="entry.id">
              {{ Number(entry.creditAmount) ? formatMoney(entry.creditAmount) : '' }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="制单人" prop="creatorUserName" width="100" />
      <el-table-column align="center" label="审核人" prop="reviewerUserName" width="100" />
      <el-table-column align="center" label="状态" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.closingGenerated" type="info">结账生成</el-tag>
          <el-tag
            v-else
            :type="scope.row.status === FMS_VOUCHER_STATUS.APPROVED ? 'success' : 'warning'"
          >
            {{ scope.row.status === FMS_VOUCHER_STATUS.APPROVED ? '已审核' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="250">
        <template #default="scope">
          <el-button link type="primary" @click="openVoucher(scope.row)">
            {{ scope.row.closingGenerated || !fmsStore.isAccountSetWritable ? '查看' : '编辑' }}
          </el-button>
          <el-button
            v-if="
              fmsStore.isAccountSetWritable &&
              !scope.row.closingGenerated &&
              scope.row.status === FMS_VOUCHER_STATUS.PENDING_REVIEW
            "
            link
            type="primary"
            @click="handleReview(scope.row, FMS_VOUCHER_STATUS.APPROVED)"
            v-hasPermi="['fms:voucher:review']"
          >
            审核
          </el-button>
          <el-button
            v-else-if="fmsStore.isAccountSetWritable && !scope.row.closingGenerated"
            link
            type="primary"
            @click="handleReview(scope.row, FMS_VOUCHER_STATUS.PENDING_REVIEW)"
            v-hasPermi="['fms:voucher:review']"
          >
            反审核
          </el-button>
          <el-button
            link
            type="primary"
            @click="handlePrintVoucher(scope.row)"
            v-hasPermi="['fms:voucher:print']"
          >
            打印
          </el-button>
          <el-button
            v-if="
              fmsStore.isAccountSetWritable &&
              !scope.row.closingGenerated &&
              scope.row.status !== FMS_VOUCHER_STATUS.APPROVED
            "
            link
            type="danger"
            @click="handleDelete(scope.row)"
            v-hasPermi="['fms:voucher:delete']"
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

  <!-- 移动、导入、打印和整理凭证弹窗 -->
  <FmsVoucherMoveForm ref="moveFormRef" @success="getList" />
  <FmsVoucherImportForm ref="importFormRef" @success="getList" />
  <FmsVoucherPrintForm ref="printFormRef" />
  <FmsVoucherTidyForm ref="tidyFormRef" @success="getList" />
  <FmsVoucherAttachmentForm ref="attachmentFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { FmsVoucherWordApi } from '@/api/fms/config/voucher-word'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import { FmsVoucherApi } from '@/api/fms/voucher'
import type { FmsVoucherPageReqVO, FmsVoucherVO } from '@/api/fms/voucher'
import { useFmsStore } from '@/views/fms/store/fms'
import download from '@/utils/download'
import { checkPermi } from '@/utils/permission'
import { dateFormatter2, getMonthRange } from '@/utils/formatTime'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import FmsVoucherWordSelect from '@/views/fms/config/voucher-word/components/FmsVoucherWordSelect.vue'
import FmsVoucherPrintForm from '../components/FmsVoucherPrintForm.vue'
import { buildVoucherListPrintHtml } from '../components/print'
import FmsVoucherAttachmentForm from './FmsVoucherAttachmentForm.vue'
import FmsVoucherMoveForm from './FmsVoucherMoveForm.vue'
import FmsVoucherImportForm from './FmsVoucherImportForm.vue'
import FmsVoucherTidyForm from './FmsVoucherTidyForm.vue'
import UserSelect from '@/views/system/user/components/UserSelect.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { FMS_VOUCHER_STATUS } from '@/views/fms/utils/constants'
import { formatMoney } from '@/views/fms/utils/format'

defineOptions({ name: 'FmsVoucherList' })

const message = useMessage() // 消息弹窗
const router = useRouter()
const fmsStore = useFmsStore()
const voucherStatusOptions = getIntDictOptions(DICT_TYPE.FMS_VOUCHER_STATUS)

const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总条数
const list = ref<FmsVoucherVO[]>([]) // 凭证列表
const queryParams = reactive<FmsVoucherPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  accountSetId: 0
})
const queryFormRef = ref() // 搜索的表单 Ref
const accountSetId = computed(() => fmsStore.getAccountSetId) // 当前账套编号
const selectedRows = ref<FmsVoucherVO[]>([]) // 选中的凭证列表
const voucherWords = ref<FmsVoucherWordVO[]>([]) // 凭证字列表
const subjects = ref<FmsSubjectVO[]>([]) // 会计科目树
const monthRange = ref<string[]>([dayjs().format('YYYY-MM'), dayjs().format('YYYY-MM')]) // 凭证期间范围

watch(accountSetId, () => init())

/** 初始化凭证列表页面 */
async function init() {
  if (!accountSetId.value) {
    list.value = []
    return
  }
  queryParams.accountSetId = accountSetId.value
  const [wordList, subjectList, accountingMonth] = await Promise.all([
    FmsVoucherWordApi.getVoucherWordSimpleList(accountSetId.value),
    FmsSubjectApi.getSubjectSimpleList(accountSetId.value),
    fmsStore.loadCurrentMonth()
  ])
  voucherWords.value = wordList
  subjects.value = subjectList
  if (accountingMonth) {
    monthRange.value = [accountingMonth, accountingMonth]
  }
  await getList()
}

/** 查询凭证列表 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    const data = await FmsVoucherApi.getVoucherPage(buildQueryParams())
    list.value = data.list
    total.value = data.total
    selectedRows.value = []
  } finally {
    loading.value = false
  }
}

/** 搜索凭证 */
function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

/** 重置搜索条件 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  const accountingMonth = fmsStore.getCurrentMonth || dayjs().format('YYYY-MM')
  monthRange.value = [accountingMonth, accountingMonth]
  queryParams.minAmount = undefined
  queryParams.maxAmount = undefined
  handleQuery()
}

/** 处理更多操作 */
const moveFormRef = ref<InstanceType<typeof FmsVoucherMoveForm>>() // 凭证移动表单 Ref
const importFormRef = ref<InstanceType<typeof FmsVoucherImportForm>>() // 凭证导入表单 Ref
const tidyFormRef = ref<InstanceType<typeof FmsVoucherTidyForm>>() // 凭证整理表单 Ref
async function handleMoreCommand(
  command: 'print' | 'printList' | 'export' | 'import' | 'move' | 'tidy'
) {
  if (!accountSetId.value) return
  switch (command) {
    case 'print':
      await handlePrintVoucher()
      return
    case 'printList':
      await handlePrintList()
      return
    case 'export':
      await handleExport()
      return
    case 'import':
      importFormRef.value?.open(accountSetId.value)
      return
    default:
      break
  }
  if (!voucherWords.value.length) {
    message.warning('请先设置凭证字')
    return
  }
  const defaultMonth = monthRange.value[1] || fmsStore.getCurrentMonth || dayjs().format('YYYY-MM')
  const formRef = command === 'move' ? moveFormRef : tidyFormRef
  formRef.value?.open(accountSetId.value, defaultMonth, voucherWords.value)
}

/** 打印凭证 */
const printFormRef = ref<InstanceType<typeof FmsVoucherPrintForm>>() // 凭证打印表单 Ref
async function handlePrintVoucher(row?: FmsVoucherVO) {
  if (!accountSetId.value) return
  let vouchers = row ? [row] : selectedRows.value
  if (!vouchers.length) {
    vouchers = await getAllVoucherList()
  }
  if (!vouchers.length) {
    message.warning('暂无可打印的凭证')
    return
  }
  printFormRef.value?.open(accountSetId.value, fmsStore.getAccountSet?.companyName || '', vouchers)
}

/** 打印凭证列表 */
async function handlePrintList() {
  const vouchers = await getAllVoucherList()
  if (!vouchers.length) {
    message.warning('暂无可打印的凭证')
    return
  }
  const startPeriod = dayjs(`${monthRange.value[0]}-01`).format('YYYY年第MM期')
  const endPeriod = dayjs(`${monthRange.value[1]}-01`).format('YYYY年第MM期')
  printFormRef.value?.previewHtml(
    buildVoucherListPrintHtml(
      fmsStore.getAccountSet?.companyName || '',
      startPeriod === endPeriod ? startPeriod : `${startPeriod} 至 ${endPeriod}`,
      vouchers
    )
  )
}

/** 导出凭证 */
async function handleExport() {
  try {
    await message.exportConfirm()
    loading.value = true
    const ids = selectedRows.value.length ? selectedRows.value.map((row) => row.id) : undefined
    const data = await FmsVoucherApi.exportVoucher(buildQueryParams(ids))
    download.excel(data, '凭证列表.xls')
  } catch {
  } finally {
    loading.value = false
  }
}

/** 查询全部凭证 */
async function getAllVoucherList() {
  loading.value = true
  try {
    return await FmsVoucherApi.getVoucherPrintList(buildQueryParams())
  } finally {
    loading.value = false
  }
}

/** 构建凭证查询参数 */
function buildQueryParams(ids?: number[]): FmsVoucherPageReqVO {
  const params: FmsVoucherPageReqVO = { ...queryParams }
  if (ids?.length) {
    params.ids = ids
  }
  if (monthRange.value.length === 2) {
    const [beginTime] = getMonthRange(monthRange.value[0])
    const [, endTime] = getMonthRange(monthRange.value[1])
    params.voucherTime = [beginTime, endTime]
  }
  return params
}

/** 打开凭证详情 */
function openVoucher(row: FmsVoucherVO) {
  router.push({
    path: '/fms/voucher/create',
    query: {
      id: row.id,
      ids: list.value.map((item) => item.id).join(',')
    }
  })
}

/** 打开凭证附件弹窗 */
const attachmentFormRef = ref<InstanceType<typeof FmsVoucherAttachmentForm>>() // 凭证附件表单 Ref
function openAttachmentDialog(row: FmsVoucherVO) {
  if (!accountSetId.value) return
  attachmentFormRef.value?.open(accountSetId.value, row)
}

/** 判断凭证附件是否可编辑 */
function canEditVoucherAttachments(row: FmsVoucherVO) {
  return (
    fmsStore.isAccountSetWritable &&
    row.status === FMS_VOUCHER_STATUS.PENDING_REVIEW &&
    !row.closingGenerated &&
    checkPermi(['fms:voucher:update'])
  )
}

/** 审核或反审核凭证 */
async function handleReview(row: FmsVoucherVO, status: number) {
  if (!accountSetId.value) return
  try {
    await message.confirm(
      status === FMS_VOUCHER_STATUS.APPROVED ? '确认审核该凭证吗？' : '确认反审核该凭证吗？'
    )
    await FmsVoucherApi.updateVoucherReviewStatus(accountSetId.value, [row.id], status)
    message.success('操作成功')
    await getList()
  } catch {}
}

/** 批量审核或反审核凭证 */
async function handleBatchReview(status: number) {
  if (!accountSetId.value || !selectedRows.value.length) return
  const rows = selectedRows.value.filter((row) =>
    status === FMS_VOUCHER_STATUS.APPROVED
      ? row.status === FMS_VOUCHER_STATUS.PENDING_REVIEW
      : row.status === FMS_VOUCHER_STATUS.APPROVED
  )
  if (!rows.length) {
    message.warning('所选凭证不符合当前审核操作')
    return
  }
  try {
    await message.confirm(
      status === FMS_VOUCHER_STATUS.APPROVED
        ? `确认审核选中的 ${rows.length} 张凭证吗？`
        : `确认反审核选中的 ${rows.length} 张凭证吗？`
    )
    await FmsVoucherApi.updateVoucherReviewStatus(
      accountSetId.value,
      rows.map((row) => row.id),
      status
    )
    message.success('操作成功')
    await getList()
  } catch {}
}

/** 删除凭证 */
async function handleDelete(row: FmsVoucherVO) {
  if (!accountSetId.value) return
  try {
    await message.delConfirm(
      `确认删除凭证“${row.voucherWordName}-${row.voucherNumber}”吗？删除后会产生断号`
    )
    await FmsVoucherApi.deleteVoucherList(accountSetId.value, [row.id])
    message.success('删除成功')
    await getList()
  } catch {}
}

/** 批量删除凭证 */
async function handleBatchDelete() {
  if (!accountSetId.value) return
  const rows = selectedRows.value
  if (rows.some((row) => row.status === FMS_VOUCHER_STATUS.APPROVED)) {
    message.warning('批量删除不能包含已审核凭证')
    return
  }
  try {
    await message.delConfirm(`确认删除选中的 ${rows.length} 张凭证吗？删除后会产生断号`)
    await FmsVoucherApi.deleteVoucherList(
      accountSetId.value,
      rows.map((row) => row.id)
    )
    message.success('删除成功')
    await getList()
  } catch {}
}

/** 处理凭证选择变化 */
function handleSelectionChange(rows: FmsVoucherVO[]) {
  selectedRows.value = rows
}

onMounted(() => {
  init()
})
</script>
