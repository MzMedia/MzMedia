<template>
  <Dialog v-model="dialogVisible" title="批量流转候选人" width="520">
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
      <el-form-item label="目标状态" prop="status">
        <el-select v-model="formData.status" class="!w-1/1" placeholder="请选择目标状态">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import {
  HrmRecruitCandidateStatus,
  type HrmRecruitCandidateStatusValue
} from '@/views/hrm/utils/constants'
import { useBatchOperation } from '@/views/hrm/utils/batch'

defineOptions({ name: 'HrmRecruitCandidateStatusListForm' })

const { executeBatch } = useBatchOperation() // 批量操作执行方法

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const candidateIds = ref<number[]>([]) // 候选人编号数组
const sourceStatus = ref<HrmRecruitCandidateStatusValue>() // 当前候选人状态
const formData = reactive({
  status: undefined as number | undefined
}) // 表单数据
const formRules = reactive<FormRules>({
  status: [{ required: true, message: '目标状态不能为空', trigger: 'change' }]
}) // 表单校验规则

/** 可直接批量流转的候选人状态 */
const statusTransitionMap: Partial<
  Record<HrmRecruitCandidateStatusValue, HrmRecruitCandidateStatusValue[]>
> = {
  [HrmRecruitCandidateStatus.NEW]: [
    HrmRecruitCandidateStatus.PRIMARY_PASS,
    HrmRecruitCandidateStatus.INTERVIEW_PASS
  ],
  [HrmRecruitCandidateStatus.PRIMARY_PASS]: [
    HrmRecruitCandidateStatus.NEW,
    HrmRecruitCandidateStatus.INTERVIEW_PASS
  ],
  [HrmRecruitCandidateStatus.INTERVIEW_PASS]: [
    HrmRecruitCandidateStatus.OFFER_SENT,
    HrmRecruitCandidateStatus.NEW,
    HrmRecruitCandidateStatus.PRIMARY_PASS
  ],
  [HrmRecruitCandidateStatus.ELIMINATED]: [HrmRecruitCandidateStatus.NEW]
}

/** 可流转的候选人状态选项 */
const statusOptions = computed(() => {
  const statusValues = sourceStatus.value ? statusTransitionMap[sourceStatus.value] || [] : []
  return getIntDictOptions(DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS).filter((item) =>
    statusValues.includes(item.value as HrmRecruitCandidateStatusValue)
  )
})

const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(ids: number[], status: HrmRecruitCandidateStatusValue) {
  candidateIds.value = [...ids]
  sourceStatus.value = status
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
        RecruitCandidateApi.updateRecruitCandidateStatus({ id, status: formData.status! })
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
  formData.status = undefined
  formRef.value?.resetFields()
}
</script>
