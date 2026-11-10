<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="640">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <el-form-item v-if="!cancelMode" label="面试结果" prop="result">
        <el-select v-model="formData.result" class="!w-1/1" placeholder="请选择面试结果">
          <el-option
            v-for="dict in resultOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="formData.result === HrmRecruitInterviewResult.CANCELED"
        label="取消原因"
        prop="cancelReason"
      >
        <el-input
          v-model="formData.cancelReason"
          :rows="3"
          maxlength="255"
          placeholder="请输入取消原因"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item v-else label="面试评价" prop="evaluate">
        <el-input
          v-model="formData.evaluate"
          :rows="4"
          maxlength="255"
          placeholder="请输入面试评价"
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
import { HrmRecruitInterviewResult } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmRecruitInterviewResultForm' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const cancelMode = ref(false) // 是否取消面试
const formData = ref<RecruitInterviewApi.HrmRecruitInterviewResultReqVO>({
  id: 0,
  result: HrmRecruitInterviewResult.PASS,
  evaluate: '',
  cancelReason: ''
}) // 表单数据
const formRules = reactive<FormRules>({
  result: [{ required: true, message: '面试结果不能为空', trigger: 'change' }],
  cancelReason: [
    {
      validator: (_rule, value, callback) => {
        if (formData.value.result === HrmRecruitInterviewResult.CANCELED && !value?.trim()) {
          callback(new Error('取消原因不能为空'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}) // 表单校验规则
const resultOptions = getIntDictOptions(DICT_TYPE.HRM_RECRUIT_INTERVIEW_RESULT).filter(
  (item) =>
    item.value === HrmRecruitInterviewResult.PASS ||
    item.value === HrmRecruitInterviewResult.NOT_PASS
) // 可登记的面试结果选项
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(
  interview: RecruitInterviewApi.HrmRecruitInterviewVO,
  result: number = HrmRecruitInterviewResult.PASS
) {
  cancelMode.value = result === HrmRecruitInterviewResult.CANCELED
  dialogTitle.value = cancelMode.value ? '取消面试' : '登记面试结果'
  dialogVisible.value = true
  resetForm(interview, result)
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
    const canceled = formData.value.result === HrmRecruitInterviewResult.CANCELED
    await RecruitInterviewApi.updateRecruitInterviewResult({
      id: formData.value.id,
      result: formData.value.result,
      evaluate: canceled ? '' : formData.value.evaluate,
      cancelReason: canceled ? formData.value.cancelReason : ''
    })
    message.success(t('common.updateSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(interview: RecruitInterviewApi.HrmRecruitInterviewVO, result: number) {
  formData.value = {
    id: interview.id!,
    result,
    evaluate: interview.evaluate ?? '',
    cancelReason: interview.cancelReason ?? ''
  }
  formRef.value?.resetFields()
}
</script>
