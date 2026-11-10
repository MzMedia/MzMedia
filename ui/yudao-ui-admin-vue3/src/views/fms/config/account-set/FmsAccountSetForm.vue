<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="920">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-divider content-position="left">基本信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="公司编码" prop="companyCode">
            <el-input v-model="formData.companyCode" maxlength="64" placeholder="请输入公司编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公司名称" prop="companyName">
            <el-input v-model="formData.companyName" maxlength="255" placeholder="请输入公司名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="公司简介" prop="companyProfile">
            <el-input
              v-model="formData.companyProfile"
              :rows="3"
              maxlength="500"
              placeholder="请输入公司简介"
              show-word-limit
              type="textarea"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在行业" prop="industry">
            <el-input v-model="formData.industry" maxlength="255" placeholder="请输入所在行业" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在地" prop="location">
            <el-input v-model="formData.location" maxlength="255" placeholder="请输入所在地" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="法人代表" prop="legalRepresentative">
            <el-input
              v-model="formData.legalRepresentative"
              maxlength="255"
              placeholder="请输入法人代表"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="法人身份证号" prop="legalRepresentativeIdNumber">
            <el-input
              v-model="formData.legalRepresentativeIdNumber"
              maxlength="255"
              placeholder="请输入法人身份证号"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="营业执照号" prop="businessLicenseNumber">
            <el-input
              v-model="formData.businessLicenseNumber"
              maxlength="255"
              placeholder="请输入营业执照号"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组织机构代码" prop="organizationCode">
            <el-input
              v-model="formData.organizationCode"
              maxlength="255"
              placeholder="请输入组织机构代码"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="formData.remark"
              :rows="2"
              maxlength="500"
              placeholder="请输入备注"
              type="textarea"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">联系方式</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="formData.contactName" maxlength="255" placeholder="请输入联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="办公电话" prop="officeTelephone">
            <el-input
              v-model="formData.officeTelephone"
              maxlength="32"
              placeholder="请输入办公电话"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码" prop="mobile">
            <el-input v-model="formData.mobile" maxlength="32" placeholder="请输入手机号码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="传真号码" prop="faxNumber">
            <el-input v-model="formData.faxNumber" maxlength="32" placeholder="请输入传真号码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="QQ 号码" prop="qqNumber">
            <el-input v-model="formData.qqNumber" maxlength="255" placeholder="请输入 QQ 号码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" maxlength="255" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他" prop="otherContact">
            <el-input
              v-model="formData.otherContact"
              maxlength="255"
              placeholder="请输入其他联系方式"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="详细地址" prop="address">
            <el-input v-model="formData.address" maxlength="255" placeholder="请输入详细地址" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsAccountSetApi } from '@/api/fms/config/account-set'
import type { FmsAccountSetVO } from '@/api/fms/config/account-set'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsAccountSetForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<FmsAccountSetVO>(createEmptyFormData()) // 表单数据
const formRules = reactive<FormRules>({
  companyCode: [{ required: true, message: '公司编码不能为空', trigger: 'blur' }],
  companyName: [{ required: true, message: '公司名称不能为空', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await FmsAccountSetApi.getAccountSet(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await FmsAccountSetApi.createAccountSet(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsAccountSetApi.updateAccountSet(formData.value)
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
  formData.value = createEmptyFormData()
  formRef.value?.resetFields()
}

/** 创建空表单数据 */
function createEmptyFormData(): FmsAccountSetVO {
  return {
    id: undefined,
    companyCode: '',
    companyName: '',
    companyProfile: '',
    industry: '',
    location: '',
    legalRepresentative: '',
    legalRepresentativeIdNumber: '',
    businessLicenseNumber: '',
    organizationCode: '',
    remark: '',
    contactName: '',
    officeTelephone: '',
    mobile: '',
    faxNumber: '',
    qqNumber: '',
    email: '',
    otherContact: '',
    address: ''
  }
}
</script>
