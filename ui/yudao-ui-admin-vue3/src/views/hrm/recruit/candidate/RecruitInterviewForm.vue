<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="760">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <el-form-item v-if="formType === 'batch'" label="候选人数">
        <el-input :model-value="`${candidateIds.length} 人`" disabled />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="面试方式" prop="type">
            <el-select v-model="formData.type" class="!w-1/1" placeholder="请选择面试方式">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_INTERVIEW_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="面试时间" prop="interviewTime">
            <el-date-picker
              v-model="formData.interviewTime"
              class="!w-1/1"
              placeholder="请选择面试时间"
              type="datetime"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="主面试官" prop="interviewEmployeeId">
            <HrmEmployeeSelect
              v-model="formData.interviewEmployeeId"
              class="!w-1/1"
              :entry-status="HrmEmployeeEntryStatus.ACTIVE"
              placeholder="请选择主面试官"
              title="选择主面试官"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他面试官" prop="otherInterviewEmployeeIds">
            <HrmEmployeeSelect
              v-model="formData.otherInterviewEmployeeIds"
              class="!w-1/1"
              :entry-status="HrmEmployeeEntryStatus.ACTIVE"
              multiple
              placeholder="请选择其他面试官"
              title="选择其他面试官"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="面试地址" prop="address">
        <el-input v-model="formData.address" maxlength="255" placeholder="请输入面试地址" />
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
      <el-button :disabled="formLoading" type="primary" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as RecruitInterviewApi from '@/api/hrm/recruit/interview'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import { HrmEmployeeEntryStatus, HrmRecruitInterviewType } from '@/views/hrm/utils/constants'
import { useBatchOperation } from '@/views/hrm/utils/batch'

defineOptions({ name: 'HrmRecruitInterviewForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { executeBatch } = useBatchOperation() // 批量操作执行方法

type FormType = 'create' | 'update' | 'batch'

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref<FormType>('create') // 表单的类型：create - 新增；update - 修改；batch - 批量新增
const candidateIds = ref<number[]>([]) // 候选人编号数组
const formData = ref<RecruitInterviewApi.HrmRecruitInterviewVO>({
  id: undefined,
  candidateId: 0,
  type: HrmRecruitInterviewType.VIDEO,
  interviewEmployeeId: undefined,
  otherInterviewEmployeeIds: [],
  interviewTime: undefined,
  address: '',
  remark: ''
}) // 表单数据
const formRules = reactive<FormRules>({
  type: [{ required: true, message: '面试方式不能为空', trigger: 'change' }],
  interviewEmployeeId: [{ required: true, message: '主面试官不能为空', trigger: 'change' }],
  interviewTime: [{ required: true, message: '面试时间不能为空', trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(
  type: FormType,
  candidateIdOrIds: number | number[],
  interview?: RecruitInterviewApi.HrmRecruitInterviewVO,
  createTitle = '安排面试'
) {
  const isBatch = Array.isArray(candidateIdOrIds)
  formType.value = type
  candidateIds.value = isBatch ? [...candidateIdOrIds] : [candidateIdOrIds]
  dialogTitle.value =
    type === 'update' ? '更改面试安排' : type === 'batch' ? '批量安排面试' : createTitle
  dialogVisible.value = true
  resetForm(candidateIds.value[0])
  if (interview) {
    formData.value = {
      ...interview,
      otherInterviewEmployeeIds: interview.otherInterviewEmployeeIds ?? []
    }
  }
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
    if (formType.value === 'update') {
      await RecruitInterviewApi.updateRecruitInterview(formData.value)
      message.success(t('common.updateSuccess'))
    } else if (formType.value === 'batch') {
      const hasSuccess = await executeBatch(
        candidateIds.value.map((candidateId) =>
          RecruitInterviewApi.createRecruitInterview({ ...formData.value, candidateId })
        )
      )
      if (!hasSuccess) {
        return
      }
    } else {
      await RecruitInterviewApi.createRecruitInterview({
        ...formData.value,
        candidateId: candidateIds.value[0]
      })
      message.success(t('common.createSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(candidateId: number) {
  formData.value = {
    id: undefined,
    candidateId,
    type: HrmRecruitInterviewType.VIDEO,
    interviewEmployeeId: undefined,
    otherInterviewEmployeeIds: [],
    interviewTime: undefined,
    address: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
