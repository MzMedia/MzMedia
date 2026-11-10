<template>
  <Dialog v-model="dialogVisible" title="删除招聘渠道" width="520">
    <el-alert
      :closable="false"
      class="!mb-20px"
      show-icon
      title="删除后，相关员工和候选人的招聘渠道将同步变更"
      type="warning"
    />
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="92px"
    >
      <el-form-item label="删除渠道">
        <el-input :model-value="channelName" disabled />
      </el-form-item>
      <el-form-item label="承接渠道" prop="transferChannelId">
        <RecruitChannelSelect
          v-model="formData.transferChannelId"
          :exclude-ids="formData.id ? [formData.id] : []"
          placeholder="请选择承接渠道"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="danger" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as RecruitChannelApi from '@/api/hrm/recruit/channel'
import type { FormInstance, FormRules } from 'element-plus'
import RecruitChannelSelect from './components/RecruitChannelSelect.vue'

defineOptions({ name: 'HrmRecruitChannelDeleteForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const channelName = ref('') // 待删除的渠道名称
const formData = ref({
  id: undefined as number | undefined,
  transferChannelId: undefined as number | undefined
})
const formRules = reactive<FormRules>({
  transferChannelId: [{ required: true, message: '承接渠道不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(channel: RecruitChannelApi.HrmRecruitChannelVO) {
  if (!channel.id) {
    return
  }
  dialogVisible.value = true
  resetForm()
  formData.value.id = channel.id
  channelName.value = channel.name
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate()
  if (!valid || !formData.value.id || !formData.value.transferChannelId) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await RecruitChannelApi.deleteRecruitChannel({
      id: formData.value.id,
      transferChannelId: formData.value.transferChannelId
    })
    message.success(t('common.delSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    transferChannelId: undefined
  }
  channelName.value = ''
  formRef.value?.resetFields()
}
</script>
