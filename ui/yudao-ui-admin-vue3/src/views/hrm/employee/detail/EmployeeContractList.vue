<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="合同编号" prop="no" min-width="150" />
    <el-table-column label="合同类型" prop="type" width="110">
      <template #default="{ row }">{{ formatEmployeeContractType(row.type) }}</template>
    </el-table-column>
    <el-table-column label="开始日期" prop="startTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="结束日期" prop="endTime" width="120" :formatter="dateFormatter2" />
    <el-table-column label="期限" prop="term" width="90">
      <template #default="{ row }">{{ row.term != null ? `${row.term} 年` : '-' }}</template>
    </el-table-column>
    <el-table-column label="合同状态" prop="status" width="110">
      <template #default="{ row }">{{ formatEmployeeContractStatus(row.status) }}</template>
    </el-table-column>
    <el-table-column label="签约公司" prop="signCompany" min-width="150" />
    <el-table-column label="签订日期" prop="signTime" width="120">
      <template #default="{ row }">{{ formatHrmDate(row.signTime) }}</template>
    </el-table-column>
    <el-table-column label="到期提醒" prop="expireRemind" width="100">
      <template #default="{ row }">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="row.expireRemind" />
      </template>
    </el-table-column>
    <el-table-column label="备注" prop="remark" min-width="160" />
    <el-table-column label="附件" min-width="180">
      <template #default="{ row }">
        <div v-if="row.fileUrls?.length" class="flex flex-col items-start">
          <el-link
            v-for="url in row.fileUrls"
            :key="url"
            type="primary"
            underline="never"
            @click="openSafeUrl(url)"
          >
            {{ getFileNameFromUrl(url) }}
          </el-link>
        </div>
        <span v-else>-</span>
      </template>
    </el-table-column>
    <el-table-column label="操作" fixed="right" width="120">
      <template #default="{ row }">
        <el-button v-hasPermi="['hrm:employee:update']" link type="primary" @click="openForm(row)">
          编辑
        </el-button>
        <el-button
          v-hasPermi="['hrm:employee:delete']"
          link
          type="danger"
          @click="handleDelete(row.id)"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 表单弹窗：添加/修改 -->
  <EmployeeContractForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { getFileNameFromUrl } from '@/utils/file'
import { dateFormatter2 } from '@/utils/formatTime'
import { openSafeUrl } from '@/utils/url'
import * as ContractApi from '@/api/hrm/employee/contract'
import {
  formatEmployeeContractStatus,
  formatEmployeeContractType,
  formatHrmDate
} from '@/views/hrm/utils/format'
import EmployeeContractForm from './EmployeeContractForm.vue'

defineOptions({ name: 'HrmEmployeeContractList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<ContractApi.HrmEmployeeContractVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await ContractApi.getEmployeeContractList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeContractForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: ContractApi.HrmEmployeeContractVO) {
  formRef.value?.open(props.employeeId, row)
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
    await ContractApi.deleteEmployeeContract(id)
    message.success('删除成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
