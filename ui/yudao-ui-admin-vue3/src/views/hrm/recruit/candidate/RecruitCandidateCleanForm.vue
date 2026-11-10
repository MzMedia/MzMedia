<template>
  <Dialog v-model="dialogVisible" title="一键清理候选人" width="560">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="候选人状态" prop="statuses">
        <el-select
          v-model="formData.statuses"
          class="!w-1/1"
          multiple
          placeholder="请选择候选人状态"
        >
          <el-option
            v-for="dict in cleanStatusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态持续天数" prop="days">
        <el-select v-model="formData.days" class="!w-1/1" placeholder="请选择持续天数">
          <el-option v-for="days in dayOptions" :key="days" :label="`${days} 天`" :value="days" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="danger" @click="submitForm">确认清理</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import { HrmRecruitCandidateStatus } from '@/views/hrm/utils/constants'
import { useBatchOperation } from '@/views/hrm/utils/batch'

defineOptions({ name: 'HrmRecruitCandidateCleanForm' })

const message = useMessage() // 消息弹窗
const { executeBatch } = useBatchOperation() // 批量操作执行方法

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const cleanStatuses: number[] = [
  HrmRecruitCandidateStatus.NEW,
  HrmRecruitCandidateStatus.PRIMARY_PASS,
  HrmRecruitCandidateStatus.INTERVIEW,
  HrmRecruitCandidateStatus.INTERVIEW_PASS
] // 允许一键清理的候选人状态
const dayOptions = [3, 5, 7, 15, 30, 45] // 状态持续天数选项
const cleanStatusOptions = getIntDictOptions(DICT_TYPE.HRM_RECRUIT_CANDIDATE_STATUS).filter(
  (item) => cleanStatuses.includes(item.value)
) // 可清理的候选人状态选项
const formData = reactive({
  statuses: [...cleanStatuses],
  days: 30
}) // 表单数据
const formRules = reactive<FormRules>({
  statuses: [{ required: true, message: '候选人状态不能为空', trigger: 'change' }],
  days: [{ required: true, message: '状态持续天数不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open() {
  dialogVisible.value = true
  resetForm()
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
  // 查询并确认清理
  formLoading.value = true
  try {
    const ids = await RecruitCandidateApi.getCleanRecruitCandidateIdList(
      formData.statuses,
      formData.days
    )
    if (!ids.length) {
      message.warning('暂无可清理候选人')
      return
    }
    await message.confirm(`确认将 ${ids.length} 位候选人移至已淘汰状态吗？`)
    // 提交请求
    const hasSuccess = await executeBatch(
      ids.map((id) =>
        RecruitCandidateApi.eliminateRecruitCandidate({
          id,
          eliminate: '长期未跟进',
          remark: `状态持续 ${formData.days} 天，由一键清理操作淘汰`
        })
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
  Object.assign(formData, { statuses: [...cleanStatuses], days: 30 })
  formRef.value?.resetFields()
}
</script>
