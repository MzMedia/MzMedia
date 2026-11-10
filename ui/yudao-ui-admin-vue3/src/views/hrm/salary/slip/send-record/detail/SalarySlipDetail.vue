<template>
  <Dialog v-model="dialogVisible" title="工资条明细" width="600px">
    <div v-loading="loading" class="min-h-320px">
      <div class="mb-20px text-center">
        <div class="text-24px font-600">{{ formatHrmMoney(detail.realPaySalary) }}</div>
        <div class="mt-8px text-14px text-[var(--el-text-color-secondary)]">实发金额（元）</div>
      </div>
      <el-table :data="detail.options || []" :row-key="getOptionRowKey" border default-expand-all>
        <el-table-column label="项目" prop="name" min-width="180" />
        <el-table-column label="金额" align="right" prop="value" width="150">
          <template #default="scope">
            {{ scope.row.children?.length ? '-' : formatHrmMoney(scope.row.value) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as SalarySlipApi from '@/api/hrm/salary/slip'
import { formatHrmMoney } from '@/views/hrm/utils/format'

defineOptions({ name: 'HrmSalarySlipDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 详情的加载中
const detail = ref<SalarySlipApi.SalarySlipVO>({}) // 工资条明细

/** 获得工资条项行键 */
function getOptionRowKey(option: SalarySlipApi.SalarySlipOptionVO) {
  return option.code !== undefined ? `option-${option.code}` : `category-${option.sort}`
}

/** 打开工资条明细 */
async function open(id?: number) {
  if (!id) {
    return
  }
  dialogVisible.value = true
  loading.value = true
  detail.value = {}
  try {
    // 获取详情数据
    detail.value = await SalarySlipApi.getSalarySlip(id)
  } finally {
    loading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件
</script>
