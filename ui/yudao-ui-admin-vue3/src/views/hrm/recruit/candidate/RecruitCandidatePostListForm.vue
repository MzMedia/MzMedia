<template>
  <Dialog v-model="dialogVisible" title="批量修改应聘职位" width="520">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <el-form-item label="候选人数">
        <el-input :model-value="`${candidateIds.length} 人`" disabled />
      </el-form-item>
      <el-form-item label="应聘职位" prop="postId">
        <RecruitPostSelect
          v-model="formData.postId"
          :clearable="false"
          class="!w-1/1"
          placeholder="请选择应聘职位"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import RecruitPostSelect from '@/views/hrm/recruit/post/components/RecruitPostSelect.vue'
import { useBatchOperation } from '@/views/hrm/utils/batch'

defineOptions({ name: 'HrmRecruitCandidatePostListForm' })

const { executeBatch } = useBatchOperation() // 批量操作执行方法

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const candidateIds = ref<number[]>([]) // 候选人编号数组
const formData = reactive({
  postId: undefined as number | undefined
}) // 表单数据
const formRules = reactive<FormRules>({
  postId: [{ required: true, message: '应聘职位不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(ids: number[]) {
  candidateIds.value = [...ids]
  resetForm()
  dialogVisible.value = true
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const hasSuccess = await executeBatch(
      candidateIds.value.map((id) =>
        RecruitCandidateApi.updateRecruitCandidatePost({ id, postId: formData.postId! })
      )
    )
    if (!hasSuccess) {
      return
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.postId = undefined
  formRef.value?.resetFields()
}
</script>
