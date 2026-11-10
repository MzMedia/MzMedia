<template>
  <ContentWrap>
    <div class="mb-18px flex items-center justify-between">
      <div>
        <div class="text-16px font-600">期末结转方案</div>
        <div class="mt-6px text-[var(--el-text-color-secondary)]">
          {{ monthLabel }} 共录入凭证 {{ voucherCount || 0 }} 张
        </div>
      </div>
      <div class="flex items-center gap-12px">
        <el-checkbox
          v-if="fmsStore.isAccountSetWritable"
          :model-value="allSchemesChecked"
          :indeterminate="someSchemesChecked"
          @change="changeAllSchemes"
        >
          全选
        </el-checkbox>
        <el-button
          type="primary"
          :loading="generating"
          :disabled="closed || !currentPeriod"
          @click="generateSelectedSchemes"
          v-hasPermi="['fms:closing:profit-loss']"
          v-if="fmsStore.isAccountSetWritable"
        >
          生成凭证
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="grid grid-cols-[repeat(auto-fill,minmax(230px,1fr))] gap-16px">
      <ClosingSchemeCard
        name="结转损益"
        :checked="selectedSchemeIds.includes(PROFIT_LOSS_SCHEME_ID)"
        :balance="profitLossScheme?.balance ?? profitLossBalance"
        :voucher-ids="profitLossScheme?.voucherIds || []"
        :generate-disabled="
          closed ||
          !currentPeriod ||
          (profitLossBalance === 0 && !profitLossScheme?.voucherIds.length)
        "
        @update:checked="changeSchemeChecked(PROFIT_LOSS_SCHEME_ID, $event)"
        @settings="profitLossSettingsFormRef?.open(profitLossScheme)"
        @generate="generateProfitLoss"
        @open-voucher="openVoucher"
      />
      <ClosingSchemeCard
        v-for="scheme in otherSchemes"
        :key="scheme.id"
        :name="scheme.name"
        :checked="selectedSchemeIds.includes(scheme.id)"
        :balance="scheme.balance"
        :voucher-ids="scheme.voucherIds"
        :generate-disabled="
          closed || !currentPeriod || (scheme.balance === 0 && !scheme.voucherIds.length)
        "
        @update:checked="changeSchemeChecked(scheme.id, $event)"
        @settings="openSchemeSettings(scheme)"
        @generate="generateScheme(scheme)"
        @open-voucher="openVoucher"
      />

      <!-- 新增结账方案 -->
      <button
        type="button"
        class="min-h-174px flex cursor-pointer flex-col items-center justify-center gap-12px rounded-6px border border-[var(--el-border-color)] border-dashed bg-transparent text-[var(--el-text-color-secondary)] transition-colors hover:border-[var(--el-color-primary)] hover:text-[var(--el-color-primary)]"
        @click="closingTemplateSelectRef?.open()"
        v-hasPermi="['fms:closing:update']"
        v-if="fmsStore.isAccountSetWritable"
      >
        <Icon icon="ep:plus" :size="34" />
        <span>期末结转凭证方案</span>
      </button>
    </div>
  </ContentWrap>

  <!-- 方案设置弹窗 -->
  <ClosingSchemeForm
    ref="schemeFormRef"
    :account-set-id="accountSetId"
    :subjects="leafSubjects"
    :voucher-words="voucherWords"
    @success="refresh"
  />
  <ClosingTemplateSelect
    ref="closingTemplateSelectRef"
    :account-set-id="accountSetId"
    :subjects="leafSubjects"
    @select="openSchemeFromTemplate"
  />
  <ProfitLossSettingsForm
    ref="profitLossSettingsFormRef"
    :account-set-id="accountSetId"
    :month="month"
    :subjects="leafSubjects"
    :voucher-words="voucherWords"
    @success="refresh"
  />
  <SpecialClosingSettingsForm
    ref="specialClosingSettingsFormRef"
    :account-set-id="accountSetId"
    :subjects="leafSubjects"
    :voucher-words="voucherWords"
    @success="refresh"
  />
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { FmsClosingSchemeApi } from '@/api/fms/closing/scheme'
import type { FmsClosingSchemeVO } from '@/api/fms/closing/scheme'
import { FmsClosingVoucherApi } from '@/api/fms/closing/voucher'
import * as FmsSubjectApi from '@/api/fms/config/subject'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { FmsVoucherWordApi } from '@/api/fms/config/voucher-word'
import type { FmsVoucherWordVO } from '@/api/fms/config/voucher-word'
import { FMS_CLOSING_TYPE } from '@/views/fms/utils/constants'
import { useFmsStore } from '@/views/fms/store/fms'
import ClosingSchemeCard from './ClosingSchemeCard.vue'
import ClosingSchemeForm from './ClosingSchemeForm.vue'
import ClosingTemplateSelect from './ClosingTemplateSelect.vue'
import type { FmsClosingTemplateVO } from '@/api/fms/closing/template'
import ProfitLossSettingsForm from './ProfitLossSettingsForm.vue'
import SpecialClosingSettingsForm from './SpecialClosingSettingsForm.vue'

defineOptions({ name: 'FmsClosingSchemeList' })

const fmsStore = useFmsStore() // FMS 状态

const props = defineProps<{
  accountSetId: number // 账套编号
  month: string // 会计期间
  currentPeriod: boolean // 是否当前会计期间
  closed: boolean // 是否已结账
  voucherCount: number // 凭证数量
  profitLossBalance: number // 损益余额
}>()
const emit = defineEmits<{ success: [] }>()
const message = useMessage() // 消息弹窗
const router = useRouter() // 路由
const PROFIT_LOSS_SCHEME_ID = -1 // 结转损益方案的页面选择标识
const loading = ref(false) // 方案数据的加载中
const generating = ref(false) // 结转凭证的生成中
const voucherWords = ref<FmsVoucherWordVO[]>([]) // 凭证字列表
const leafSubjects = ref<FmsSubjectVO[]>([]) // 末级科目列表
const closingSchemes = ref<FmsClosingSchemeVO[]>([]) // 结账方案列表
const selectedSchemeIds = ref<number[]>([]) // 选中的方案编号数组
const schemeFormRef = ref<InstanceType<typeof ClosingSchemeForm>>() // 结账方案表单
const closingTemplateSelectRef = ref<InstanceType<typeof ClosingTemplateSelect>>() // 结账模板选择弹窗
const profitLossSettingsFormRef = ref<InstanceType<typeof ProfitLossSettingsForm>>() // 结转损益设置表单
const specialClosingSettingsFormRef = ref<InstanceType<typeof SpecialClosingSettingsForm>>() // 专用结转设置表单
const monthLabel = computed(() => dayjs(`${props.month}-01`).format('YYYY年MM月')) // 会计期间文本
const otherSchemes = computed(() =>
  closingSchemes.value.filter((item) => item.type !== FMS_CLOSING_TYPE.PROFIT_LOSS)
) // 非结转损益方案列表
const profitLossScheme = computed(() =>
  closingSchemes.value.find((item) => item.type === FMS_CLOSING_TYPE.PROFIT_LOSS)
) // 结转损益方案
const allSchemeIds = computed(() => [
  PROFIT_LOSS_SCHEME_ID,
  ...otherSchemes.value.map((item) => item.id)
]) // 全部可选择的方案编号数组
const allSchemesChecked = computed(
  () =>
    allSchemeIds.value.length > 0 &&
    allSchemeIds.value.every((id) => selectedSchemeIds.value.includes(id))
) // 是否选中全部方案
const someSchemesChecked = computed(
  () => selectedSchemeIds.value.length > 0 && !allSchemesChecked.value
) // 是否只选中部分方案

/** 初始化方案数据 */
async function init() {
  const [words, subjectList] = await Promise.all([
    FmsVoucherWordApi.getVoucherWordSimpleList(props.accountSetId),
    FmsSubjectApi.getSubjectSimpleList(props.accountSetId)
  ])
  voucherWords.value = words
  const parentSubjectIds = new Set(subjectList.map((item) => item.parentId))
  leafSubjects.value = subjectList.filter((item) => !parentSubjectIds.has(item.id))
  await getSchemeList()
}

/** 获得结账方案列表 */
async function getSchemeList() {
  loading.value = true
  try {
    closingSchemes.value = await FmsClosingSchemeApi.getClosingSchemeList({
      accountSetId: props.accountSetId,
      month: props.month
    })
    selectedSchemeIds.value = selectedSchemeIds.value.filter((id) =>
      [PROFIT_LOSS_SCHEME_ID, ...closingSchemes.value.map((item) => item.id)].includes(id)
    )
  } finally {
    loading.value = false
  }
}

/** 刷新方案和结账概况 */
async function refresh() {
  await getSchemeList()
  emit('success')
}

/** 生成结转损益凭证 */
async function generateProfitLoss() {
  if (!props.currentPeriod) {
    message.warning('只能生成当前会计期间的结转凭证')
    return
  }
  if (!profitLossScheme.value) {
    message.warning('请先完成结转损益参数设置')
    profitLossSettingsFormRef.value?.open()
    return
  }
  try {
    await message.confirm(`确认生成 ${monthLabel.value} 的结转损益凭证吗？`)
  } catch {
    return
  }
  generating.value = true
  try {
    const voucherId = await FmsClosingVoucherApi.generateProfitLossVoucher({
      accountSetId: props.accountSetId,
      month: props.month
    })
    message.success('结转损益凭证已生成')
    await refresh()
    openVoucher(voucherId)
  } finally {
    generating.value = false
  }
}

/** 生成指定结账方案凭证 */
async function generateScheme(scheme: FmsClosingSchemeVO) {
  if (!props.currentPeriod) return
  try {
    await message.confirm(`确认生成 ${monthLabel.value} 的“${scheme.name}”凭证吗？`)
  } catch {
    return
  }
  generating.value = true
  try {
    const voucherId = await FmsClosingVoucherApi.generateClosingSchemeVoucher({
      accountSetId: props.accountSetId,
      month: props.month,
      id: scheme.id
    })
    message.success('结转凭证已生成')
    await refresh()
    openVoucher(voucherId)
  } finally {
    generating.value = false
  }
}

/** 批量生成选中的结账方案凭证 */
async function generateSelectedSchemes() {
  if (!props.currentPeriod) return
  const ids = selectedSchemeIds.value.length ? selectedSchemeIds.value : allSchemeIds.value
  const availableIds = ids.filter((id) => {
    if (id === PROFIT_LOSS_SCHEME_ID) {
      return props.profitLossBalance !== 0 || Boolean(profitLossScheme.value?.voucherIds.length)
    }
    const scheme = otherSchemes.value.find((item) => item.id === id)
    return scheme?.balance !== 0 || Boolean(scheme?.voucherIds.length)
  })
  if (!availableIds.length) {
    message.warning('当前没有需要生成凭证的结账方案')
    return
  }
  if (availableIds.includes(PROFIT_LOSS_SCHEME_ID) && !profitLossScheme.value) {
    message.warning('请先完成结转损益参数设置')
    profitLossSettingsFormRef.value?.open()
    return
  }
  try {
    await message.confirm(`确认生成已选择的 ${availableIds.length} 个结账方案凭证吗？`)
  } catch {
    return
  }
  generating.value = true
  try {
    const voucherIds = await FmsClosingVoucherApi.generateClosingVoucherList({
      accountSetId: props.accountSetId,
      month: props.month,
      ids: availableIds.map((id) =>
        id === PROFIT_LOSS_SCHEME_ID ? profitLossScheme.value!.id : id
      )
    })
    const skippedCount = availableIds.length - voucherIds.length
    message.success(
      skippedCount
        ? `已生成 ${voucherIds.length} 个结账方案凭证，${skippedCount} 个方案无需生成`
        : `已生成 ${voucherIds.length} 个结账方案凭证`
    )
    await refresh()
  } finally {
    generating.value = false
  }
}

/** 处理全部方案选择 */
function changeAllSchemes(checked: boolean | string | number) {
  selectedSchemeIds.value = checked ? [...allSchemeIds.value] : []
}

/** 处理单个方案选择 */
function changeSchemeChecked(id: number, checked: boolean | string | number) {
  if (checked) {
    if (!selectedSchemeIds.value.includes(id)) selectedSchemeIds.value.push(id)
    return
  }
  selectedSchemeIds.value = selectedSchemeIds.value.filter((item) => item !== id)
}

/** 打开结账方案设置 */
function openSchemeSettings(scheme: FmsClosingSchemeVO) {
  if (scheme.type === FMS_CLOSING_TYPE.REGULAR) {
    schemeFormRef.value?.open(scheme)
    return
  }
  specialClosingSettingsFormRef.value?.open(scheme)
}

/** 从模板新增结账方案 */
function openSchemeFromTemplate(template?: FmsClosingTemplateVO) {
  schemeFormRef.value?.open(undefined, template)
}

/** 打开凭证 */
function openVoucher(voucherId: number) {
  router.push({ path: '/fms/voucher/create', query: { id: voucherId } })
}

watch(() => props.accountSetId, init, { immediate: true })
watch(() => props.month, getSchemeList)
</script>
