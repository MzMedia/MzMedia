<template>
  <doc-alert title="【社保】社保管理" url="https://doc.iocoder.cn/hrm/insurance/" />

  <ContentWrap>
    <div class="mb-16px flex justify-end">
      <el-button
        v-hasPermi="['hrm:insurance:scheme:create']"
        plain
        type="primary"
        @click="openForm('create')"
      >
        <Icon icon="ep:plus" class="mr-5px" />新建参保方案
      </el-button>
    </div>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column label="方案名称" prop="name" min-width="180" />
      <el-table-column label="参保城市" prop="areaName" min-width="180" />
      <el-table-column align="right" label="个人社保" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.personalInsuranceAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="公司社保" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.corporateInsuranceAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="个人公积金" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.personalProvidentFundAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="公司公积金" width="120">
        <template #default="scope">
          {{ formatHrmMoney(scope.row.corporateProvidentFundAmount) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="使用人数" prop="useCount" width="100" />
      <el-table-column align="center" label="历史月记录" prop="monthRecordCount" width="110" />
      <el-table-column align="center" fixed="right" label="操作" width="140">
        <template #default="scope">
          <el-button
            v-hasPermi="['hrm:insurance:scheme:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['hrm:insurance:scheme:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <InsuranceSchemeForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import * as InsuranceSchemeApi from '@/api/hrm/insurance/scheme'
import { formatHrmMoney } from '@/views/hrm/utils/format'
import InsuranceSchemeForm from './InsuranceSchemeForm.vue'

defineOptions({ name: 'HrmInsuranceScheme' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<InsuranceSchemeApi.InsuranceSchemeVO[]>([]) // 参保方案列表
const formRef = ref<InstanceType<typeof InsuranceSchemeForm>>() // 参保方案表单 Ref

/** 获得参保方案列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await InsuranceSchemeApi.getInsuranceSchemeList()
  } finally {
    loading.value = false
  }
}

/** 打开参保方案表单 */
function openForm(type: string, id?: number) {
  formRef.value?.open(type, id)
}

/** 删除参保方案 */
async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await InsuranceSchemeApi.deleteInsuranceScheme(id)
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
