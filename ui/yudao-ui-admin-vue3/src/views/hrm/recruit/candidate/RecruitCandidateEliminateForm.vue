<template>
  <Dialog v-model="dialogVisible" :title="batchMode ? '批量淘汰候选人' : '淘汰候选人'" width="560">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <el-form-item label="候选人">
        <el-input
          :model-value="batchMode ? `已选择 ${candidateIds.length} 人` : candidateName"
          disabled
        />
      </el-form-item>
      <el-form-item label="淘汰原因" prop="eliminate">
        <RecruitEliminateReasonSelect v-model="formData.eliminate" class="!w-1/1" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          :rows="3"
          maxlength="255"
          placeholder="请输入备注"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="danger" @click="submitForm">确认淘汰</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import { useBatchOperation } from '@/views/hrm/utils/batch'
import RecruitEliminateReasonSelect from '@/views/hrm/recruit/setting/eliminate/components/RecruitEliminateReasonSelect.vue'

defineOptions({ name: 'HrmRecruitCandidateEliminateForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { executeBatch } = useBatchOperation() // 批量操作执行方法

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单提交中
const batchMode = ref(false) // 是否批量淘汰
const candidateIds = ref<number[]>([]) // 候选人编号数组
const candidateName = ref('') // 单个候选人姓名
const formData = reactive({
  eliminate: '',
  remark: ''
}) // 表单数据
const formRules = reactive<FormRules>({
  eliminate: [
    { required: true, message: '淘汰原因不能为空', trigger: 'change' },
    { max: 255, message: '淘汰原因不能超过 255 个字符', trigger: 'change' }
  ],
  remark: [{ max: 255, message: '备注不能超过 255 个字符', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(ids: number | number[], name = '') {
  const isBatch = Array.isArray(ids)
  batchMode.value = isBatch
  candidateIds.value = isBatch ? [...ids] : [ids]
  candidateName.value = name
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
    if (!batchMode.value) {
      await RecruitCandidateApi.eliminateRecruitCandidate({
        id: candidateIds.value[0],
        eliminate: formData.eliminate,
        remark: formData.remark
      })
      message.success(t('common.updateSuccess'))
    } else {
      const hasSuccess = await executeBatch(
        candidateIds.value.map((id) =>
          RecruitCandidateApi.eliminateRecruitCandidate({
            id,
            eliminate: formData.eliminate,
            remark: formData.remark
          })
        )
      )
      if (!hasSuccess) {
        return
      }
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
  Object.assign(formData, { eliminate: '', remark: '' })
  formRef.value?.resetFields()
}
</script>
