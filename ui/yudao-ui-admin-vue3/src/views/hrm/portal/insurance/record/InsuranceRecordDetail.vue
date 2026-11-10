<template>
  <Dialog v-model="dialogVisible" :title="`${record?.month || ''} 月社保表`" width="1060px">
    <div v-loading="loading">
      <el-descriptions :column="2" border class="mb-16px">
        <el-descriptions-item label="参保方案">
          {{ record?.schemeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="方案类型">
          <dict-tag
            v-if="record?.schemeType"
            :type="DICT_TYPE.HRM_INSURANCE_SCHEME_TYPE"
            :value="record.schemeType"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="个人缴纳">
          ¥ {{ formatHrmMoney(personalTotal) }}
        </el-descriptions-item>
        <el-descriptions-item label="公司缴纳">
          ¥ {{ formatHrmMoney(corporateTotal) }}
        </el-descriptions-item>
        <el-descriptions-item label="本月合计" :span="2">
          <b class="text-16px text-[var(--el-color-primary)]">
            ¥ {{ formatHrmMoney(personalTotal + corporateTotal) }}
          </b>
        </el-descriptions-item>
      </el-descriptions>
      <el-table :data="record?.projects || []" border show-summary :summary-method="projectSummary">
        <el-table-column label="缴纳项目" prop="name" min-width="150" />
        <el-table-column label="缴纳基数" align="right" width="130">
          <template #default="scope">¥ {{ formatHrmMoney(scope.row.baseAmount) }}</template>
        </el-table-column>
        <el-table-column
          v-if="record?.schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="个人比例"
          align="right"
          width="110"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.personalRate) }}</template>
        </el-table-column>
        <el-table-column label="个人金额" prop="personalAmount" align="right" width="130">
          <template #default="scope">¥ {{ formatHrmMoney(scope.row.personalAmount) }}</template>
        </el-table-column>
        <el-table-column
          v-if="record?.schemeType === HrmInsuranceSchemeType.PROPORTION"
          label="公司比例"
          align="right"
          width="110"
        >
          <template #default="scope">{{ formatHrmRate(scope.row.corporateRate) }}</template>
        </el-table-column>
        <el-table-column label="公司金额" prop="corporateAmount" align="right" width="130">
          <template #default="scope">¥ {{ formatHrmMoney(scope.row.corporateAmount) }}</template>
        </el-table-column>
        <el-table-column label="合计" prop="totalAmount" align="right" width="130">
          <template #default="scope">
            ¥
            {{ formatHrmMoney((scope.row.personalAmount || 0) + (scope.row.corporateAmount || 0)) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </Dialog>
</template>

<script lang="ts" setup>
import type { TableColumnCtx } from 'element-plus'
import { DICT_TYPE } from '@/utils/dict'
import * as InsuranceRecordApi from '@/api/hrm/portal/insurance/record'
import { HrmInsuranceSchemeType } from '@/views/hrm/utils/constants'
import { formatHrmMoney, formatHrmRate } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmPortalInsuranceRecordDetail' })

const dialogVisible = ref(false) // 弹窗是否展示
const loading = ref(false) // 加载中
const record = ref<InsuranceRecordApi.InsuranceRecordVO>() // 记录详情
const personalTotal = computed(
  () =>
    (record.value?.personalInsuranceAmount || 0) + (record.value?.personalProvidentFundAmount || 0)
)
const corporateTotal = computed(
  () =>
    (record.value?.corporateInsuranceAmount || 0) +
    (record.value?.corporateProvidentFundAmount || 0)
)

/** 打开社保记录详情 */
async function open(summary: InsuranceRecordApi.InsuranceRecordVO) {
  record.value = { ...summary, projects: [] }
  dialogVisible.value = true
  loading.value = true
  try {
    // 获取详情数据
    record.value = await InsuranceRecordApi.getInsuranceRecord(summary.id)
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

/** 构建参保项目合计行 */
function projectSummary({
  columns,
  data
}: {
  columns: TableColumnCtx<InsuranceRecordApi.InsuranceSchemeProjectVO>[]
  data: InsuranceRecordApi.InsuranceSchemeProjectVO[]
}) {
  return columns.map((column, index) => {
    if (index === 0) {
      return '合计'
    }
    const property = String(column.property || '')
    if (!['personalAmount', 'corporateAmount'].includes(property) && index !== columns.length - 1) {
      return ''
    }
    return `¥ ${formatHrmMoney(
      data.reduce(
        (sum, item) =>
          sum +
          Number(
            property === 'personalAmount'
              ? item.personalAmount || 0
              : property === 'corporateAmount'
                ? item.corporateAmount || 0
                : (item.personalAmount || 0) + (item.corporateAmount || 0)
          ),
        0
      )
    )}`
  })
}
</script>
