<template>
  <!-- 搜索 -->
  <el-form class="-mb-15px" :inline="true" label-width="68px">
    <el-form-item label="会计期间">
      <FmsLedgerMonthRangePicker v-model="monthRange" @change="handleAutoQuery" />
    </el-form-item>
    <el-form-item v-if="showSubject" label="科目">
      <FmsSubjectSelect
        v-model="query.subjectId"
        :options="subjects"
        placeholder="请选择科目"
        class="!w-240px"
        @change="handleAutoQuery"
      />
    </el-form-item>
    <el-form-item>
      <el-popover v-if="!showSubject" :width="360" placement="bottom-start" trigger="click">
        <template #reference>
          <el-button><Icon icon="ep:filter" /> 更多条件</el-button>
        </template>
        <el-form label-position="top">
          <el-form-item label="起始科目">
            <FmsSubjectSelect
              v-model="query.startSubjectId"
              :options="subjects"
              clearable
              class="!w-full"
            />
          </el-form-item>
          <el-form-item label="结束科目">
            <FmsSubjectSelect
              v-model="query.endSubjectId"
              :options="subjects"
              clearable
              class="!w-full"
            />
          </el-form-item>
          <el-form-item label="科目级次">
            <div class="flex items-center gap-8px">
              <el-input-number
                v-model="query.minLevel"
                :controls="false"
                :min="1"
                :max="FMS_SUBJECT_LEVEL_MAX"
                class="!w-135px"
              />
              <span>至</span>
              <el-input-number
                v-model="query.maxLevel"
                :controls="false"
                :min="1"
                :max="FMS_SUBJECT_LEVEL_MAX"
                class="!w-135px"
              />
            </div>
          </el-form-item>
          <div class="flex items-center justify-end gap-8px">
            <el-button @click="resetAdvanced">重置</el-button>
            <el-button type="primary" @click="handleQuery">查询</el-button>
          </div>
        </el-form>
      </el-popover>
      <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      <FmsLedgerPrintButton
        v-if="showPrint && printTarget"
        :before-print="beforePrint"
        :center-text="printCenterText"
        :end-month="monthRange[1]"
        :permission-prefix="permissionPrefix"
        :start-month="monthRange[0]"
        :target="printTarget"
        :title="printTitle"
      />
      <el-button
        v-if="showExport"
        v-hasPermi="[`${permissionPrefix}:export`]"
        :loading="exportLoading"
        class="!ml-12px"
        type="success"
        plain
        @click="emit('export')"
      >
        <Icon icon="ep:download" /> 导出
      </el-button>
      <slot name="actions"></slot>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import type { FmsLedgerListReqVO } from '@/api/fms/ledger/types'
import { formatDate } from '@/utils/formatTime'
import { findNode } from '@/utils/tree'
import FmsSubjectSelect from '@/views/fms/config/subject/components/FmsSubjectSelect.vue'
import { useFmsStore } from '@/views/fms/store/fms'
import FmsLedgerMonthRangePicker from '@/views/fms/ledger/components/FmsLedgerMonthRangePicker.vue'
import FmsLedgerPrintButton from '@/views/fms/ledger/components/FmsLedgerPrintButton.vue'
import { FMS_SUBJECT_LEVEL_MAX } from '@/views/fms/utils/constants'

defineOptions({ name: 'FmsLedgerSearchBar' })

const props = withDefaults(
  defineProps<{
    subjects?: FmsSubjectVO[]
    showSubject?: boolean
    subjectId?: number
    startMonth?: string
    endMonth?: string
    minLevel?: number
    maxLevel?: number
    printTarget?: string
    printTitle?: string
    beforePrint?: () => void | Promise<void>
    showExport?: boolean
    showPrint?: boolean
    exportLoading?: boolean
    permissionPrefix?: string
    autoQuery?: boolean
  }>(),
  {
    permissionPrefix: 'fms:ledger:general',
    showSubject: false,
    subjectId: undefined,
    startMonth: undefined,
    endMonth: undefined,
    minLevel: 1,
    maxLevel: 1,
    printTarget: '',
    printTitle: '',
    showExport: true,
    showPrint: true,
    exportLoading: false,
    autoQuery: false
  }
)
const emit = defineEmits<{
  search: [value: Omit<FmsLedgerListReqVO, 'accountSetId'>]
  export: []
}>()

const fmsStore = useFmsStore() // FMS 状态
const currentMonth = formatDate(new Date(), 'YYYY-MM') // 当前月份
const monthRange = ref<string[]>([props.startMonth || currentMonth, props.endMonth || currentMonth]) // 会计期间范围
const query = reactive<Partial<FmsLedgerListReqVO>>({
  subjectId: props.subjectId,
  minLevel: props.minLevel,
  maxLevel: props.maxLevel
})
const printCenterText = computed(() => {
  const subject = findNode<FmsSubjectVO>(
    props.subjects || [],
    (item) => item.id === query.subjectId
  )
  return subject ? `科目：${subject.code} ${subject.name}` : ''
}) // 打印科目文本

watch(
  () => props.subjectId,
  (value) => (query.subjectId = value)
)
watch(
  () => [props.startMonth, props.endMonth],
  ([startMonth, endMonth]) => {
    if (startMonth && endMonth) {
      monthRange.value = [startMonth, endMonth]
    }
  }
)

/** 搜索按钮操作 */
function handleQuery() {
  if (monthRange.value.length !== 2) return
  emit('search', {
    startMonth: monthRange.value[0],
    endMonth: monthRange.value[1],
    subjectId: query.subjectId,
    startSubjectId: query.startSubjectId,
    endSubjectId: query.endSubjectId,
    minLevel: query.minLevel,
    maxLevel: query.maxLevel
  })
}

/** 按条件变化自动查询，供需要即时刷新的账簿页面启用 */
function handleAutoQuery() {
  if (props.autoQuery) handleQuery()
}

/** 重置更多查询条件 */
function resetAdvanced() {
  query.startSubjectId = undefined
  query.endSubjectId = undefined
  query.minLevel = props.minLevel
  query.maxLevel = props.maxLevel
}

/** 重置按钮操作 */
async function resetQuery() {
  const accountingMonth = await fmsStore.loadCurrentMonth()
  monthRange.value = [accountingMonth || currentMonth, accountingMonth || currentMonth]
  query.subjectId = props.showSubject ? props.subjects?.[0]?.id : undefined
  resetAdvanced()
  handleQuery()
}
</script>
