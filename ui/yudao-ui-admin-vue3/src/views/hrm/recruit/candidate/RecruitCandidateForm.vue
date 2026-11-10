<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="920">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="候选人姓名" prop="name">
            <el-input v-model="formData.name" maxlength="255" placeholder="请输入候选人姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码" prop="mobile">
            <el-input v-model="formData.mobile" maxlength="18" placeholder="请输入手机号码" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="formData.sex">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX).filter(
                  (item) => item.value !== SystemUserSexEnum.UNKNOWN
                )"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="年龄" prop="age">
            <el-input-number
              v-model="formData.age"
              :controls="false"
              :max="99"
              :min="0"
              class="!w-1/1"
              placeholder="请输入年龄"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" maxlength="255" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应聘职位" prop="postId">
            <RecruitPostSelect
              v-model="formData.postId"
              :clearable="false"
              class="!w-1/1"
              placeholder="请选择应聘职位"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工作年限" prop="workTime">
            <el-input-number
              v-model="formData.workTime"
              :controls="false"
              :max="60"
              :min="0"
              class="!w-1/1"
              placeholder="请输入工作年限"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学历" prop="education">
            <el-select v-model="formData.education" class="!w-1/1" placeholder="请选择学历">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_CANDIDATE_EDUCATION)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="毕业院校" prop="graduateSchool">
            <el-input
              v-model="formData.graduateSchool"
              maxlength="255"
              placeholder="请输入毕业院校"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最近工作单位" prop="latestWorkPlace">
            <el-input
              v-model="formData.latestWorkPlace"
              maxlength="255"
              placeholder="请输入最近工作单位"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="招聘渠道" prop="channelId">
            <RecruitChannelSelect
              v-model="formData.channelId"
              class="!w-1/1"
              placeholder="请选择招聘渠道"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="简历附件" prop="resumeUrls">
        <UploadFile
          v-model="formData.resumeUrls"
          :file-size="20"
          :file-type="['doc', 'docx', 'pdf']"
          :limit="5"
          directory="hrm/recruit/candidate/resume"
        />
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
import { SystemUserSexEnum } from '@/utils/constants'
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import RecruitChannelSelect from '@/views/hrm/recruit/channel/components/RecruitChannelSelect.vue'
import RecruitPostSelect from '@/views/hrm/recruit/post/components/RecruitPostSelect.vue'

defineOptions({ name: 'HrmRecruitCandidateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<RecruitCandidateApi.HrmRecruitCandidateVO>({
  id: undefined,
  name: '',
  mobile: '',
  sex: SystemUserSexEnum.MALE,
  age: undefined,
  email: '',
  postId: undefined,
  workTime: undefined,
  education: undefined,
  graduateSchool: '',
  latestWorkPlace: '',
  channelId: undefined,
  remark: '',
  resumeUrls: []
}) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '候选人姓名不能为空', trigger: 'blur' }],
  mobile: [
    { required: true, message: '手机号码不能为空', trigger: 'blur' },
    {
      pattern: /^(\+?0?\d{2,4}-?)?\d{6,11}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  sex: [{ required: true, message: '性别不能为空', trigger: 'change' }],
  postId: [{ required: true, message: '应聘职位不能为空', trigger: 'change' }],
  education: [{ required: true, message: '学历不能为空', trigger: 'change' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新建候选人' : '编辑候选人'
  formType.value = type
  resetForm()
  // 修改时，设置表单数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await RecruitCandidateApi.getRecruitCandidate(id)
      formData.value.resumeUrls ??= []
    } finally {
      formLoading.value = false
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
    if (formType.value === 'create') {
      await RecruitCandidateApi.createRecruitCandidate(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await RecruitCandidateApi.updateRecruitCandidate(formData.value)
      message.success(t('common.updateSuccess'))
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
  formData.value = {
    id: undefined,
    name: '',
    mobile: '',
    sex: SystemUserSexEnum.MALE,
    age: undefined,
    email: '',
    postId: undefined,
    workTime: undefined,
    education: undefined,
    graduateSchool: '',
    latestWorkPlace: '',
    channelId: undefined,
    remark: '',
    resumeUrls: []
  }
  formRef.value?.resetFields()
}
</script>
