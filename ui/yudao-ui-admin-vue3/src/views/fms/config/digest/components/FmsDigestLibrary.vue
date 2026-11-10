<template>
  <Dialog v-model="dialogVisible" title="凭证摘要库" width="620px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
      <el-form-item label="摘要内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
          placeholder="请输入摘要内容"
        />
      </el-form-item>
    </el-form>
    <div class="mb-16px flex justify-end">
      <el-button
        v-if="!formData.id && fmsStore.isAccountSetWritable"
        v-hasPermi="['fms:config:digest:create']"
        type="primary"
        :loading="formLoading"
        @click="submitForm"
      >
        新增
      </el-button>
      <el-button
        v-else-if="fmsStore.isAccountSetWritable"
        v-hasPermi="['fms:config:digest:update']"
        type="primary"
        :loading="formLoading"
        @click="submitForm"
      >
        保存
      </el-button>
      <el-button @click="resetForm">取消</el-button>
    </div>
    <el-table
      v-loading="loading"
      :data="list"
      border
      stripe
      highlight-current-row
      @row-dblclick="selectDigest"
    >
      <el-table-column label="摘要内容" prop="content" min-width="360" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="160">
        <template #default="scope">
          <el-button
            v-hasPermi="['fms:config:digest:update']"
            link
            type="primary"
            @click="editDigest(scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['fms:config:digest:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            删除
          </el-button>
          <el-button link type="primary" @click="selectDigest(scope.row)">套用</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="mt-10px text-12px text-[var(--el-text-color-secondary)]">
      双击摘要可直接套用到当前分录
    </div>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { FmsDigestApi } from '@/api/fms/config/digest'
import type { FmsDigestVO } from '@/api/fms/config/digest'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsDigestLibrary' })

const emit = defineEmits<{ select: [digest: string] }>()
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 列表的加载中
const formLoading = ref(false) // 表单的加载中
const accountSetId = ref<number>() // 当前账套编号
const list = ref<FmsDigestVO[]>([]) // 列表的数据
const formData = ref<FmsDigestVO>({
  id: undefined,
  accountSetId: 0,
  content: ''
})
const formRules = reactive<FormRules>({
  content: [{ required: true, message: '摘要内容不能为空', trigger: 'blur' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开凭证摘要库 */
async function open(id: number) {
  accountSetId.value = id
  resetForm()
  dialogVisible.value = true
  await getList()
}
defineExpose({ open })

/** 查询常用摘要精简列表 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    list.value = await FmsDigestApi.getDigestSimpleList(accountSetId.value)
  } finally {
    loading.value = false
  }
}

/** 编辑常用摘要 */
function editDigest(row: FmsDigestVO) {
  formData.value = { ...row }
  nextTick(() => formRef.value?.clearValidate())
}

/** 保存常用摘要 */
async function submitForm() {
  // 校验表单
  if (!accountSetId.value || !formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (formData.value.id) {
      await FmsDigestApi.updateDigest(formData.value)
      message.success(t('common.updateSuccess'))
    } else {
      await FmsDigestApi.createDigest(formData.value)
      message.success(t('common.createSuccess'))
    }
    resetForm()
    await getList()
  } finally {
    formLoading.value = false
  }
}

/** 删除常用摘要 */
async function handleDelete(row: FmsDigestVO) {
  if (!accountSetId.value) return
  try {
    // 删除的二次确认
    await message.confirm(`是否确认删除常用摘要“${row.content}”？`)
    // 发起删除
    await FmsDigestApi.deleteDigest(accountSetId.value, row.id!)
    message.success(t('common.delSuccess'))
    if (formData.value.id === row.id) {
      resetForm()
    }
    // 刷新列表
    await getList()
  } catch {}
}

/** 套用常用摘要 */
function selectDigest(row: FmsDigestVO) {
  emit('select', row.content)
  dialogVisible.value = false
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    accountSetId: accountSetId.value || 0,
    content: ''
  }
  formRef.value?.resetFields()
}
</script>
