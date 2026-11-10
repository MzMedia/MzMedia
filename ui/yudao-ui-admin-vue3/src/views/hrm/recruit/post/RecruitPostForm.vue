<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1040">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职位名称" prop="postName">
            <el-input v-model="formData.postName" maxlength="255" placeholder="请输入职位名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="用人部门" prop="deptId">
            <DeptSelect v-model="formData.deptId" class="!w-1/1" placeholder="请选择用人部门" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工作性质" prop="jobNature">
            <el-select
              v-model="formData.jobNature"
              class="!w-1/1"
              clearable
              placeholder="请选择工作性质"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_JOB_NATURE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工作城市" prop="areaId">
            <AreaSelect v-model="formData.areaId" class="!w-1/1" placeholder="请选择工作城市" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="招聘人数" prop="recruitNum">
            <el-input-number
              v-model="formData.recruitNum"
              :controls="false"
              :min="0"
              class="!w-1/1"
              placeholder="请输入招聘人数"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="招聘原因" prop="reason">
            <el-input v-model="formData.reason" maxlength="255" placeholder="请输入招聘原因" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工作经验" prop="workTime">
            <el-select
              v-model="formData.workTime"
              class="!w-1/1"
              clearable
              placeholder="请选择工作经验"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_WORK_TIME)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学历要求" prop="educationRequire">
            <el-select
              v-model="formData.educationRequire"
              class="!w-1/1"
              clearable
              placeholder="请选择学历要求"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_POST_EDUCATION)"
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
          <el-form-item label="薪资范围" prop="minSalary">
            <div class="w-full">
              <div class="flex w-full items-center gap-4px">
                <el-input-number
                  v-model="formData.minSalary"
                  :controls="false"
                  :disabled="salaryNegotiable"
                  :max="99999999.99"
                  :min="0"
                  :precision="2"
                  class="!w-0 flex-1"
                  placeholder="最低薪资"
                />
                <span class="shrink-0 text-[var(--el-text-color-secondary)]">至</span>
                <el-input-number
                  v-model="formData.maxSalary"
                  :controls="false"
                  :disabled="salaryNegotiable"
                  :max="99999999.99"
                  :min="0"
                  :precision="2"
                  class="!w-0 flex-1"
                  placeholder="最高薪资"
                />
                <el-select
                  v-model="formData.salaryUnit"
                  :disabled="salaryNegotiable"
                  class="!w-80px shrink-0"
                  placeholder="单位"
                >
                  <el-option
                    v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_SALARY_UNIT)"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
                <el-checkbox
                  v-model="salaryNegotiable"
                  class="shrink-0"
                  @change="handleSalaryNegotiableChange"
                >
                  面议
                </el-checkbox>
              </div>
              <div class="mt-4px text-12px text-[var(--el-text-color-secondary)]">
                最低薪资不能大于最高薪资；勾选“面议”后无需填写范围。
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最迟到岗时间" prop="latestEntryTime">
            <el-date-picker
              v-model="formData.latestEntryTime"
              class="!w-1/1"
              placeholder="请选择时间"
              type="datetime"
              value-format="x"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年龄要求" prop="minAge">
            <div class="w-full">
              <div class="flex w-full items-center gap-8px">
                <el-input-number
                  v-model="formData.minAge"
                  :controls="false"
                  :disabled="ageUnlimited"
                  :max="99"
                  :min="0"
                  class="!w-0 flex-1"
                  placeholder="最小年龄"
                />
                <span class="shrink-0 text-[var(--el-text-color-secondary)]">至</span>
                <el-input-number
                  v-model="formData.maxAge"
                  :controls="false"
                  :disabled="ageUnlimited"
                  :max="99"
                  :min="0"
                  class="!w-0 flex-1"
                  placeholder="最大年龄"
                />
                <el-checkbox
                  v-model="ageUnlimited"
                  class="shrink-0"
                  @change="handleAgeUnlimitedChange"
                >
                  不限
                </el-checkbox>
              </div>
              <div class="mt-4px text-12px text-[var(--el-text-color-secondary)]">
                最小年龄不能大于最大年龄；勾选“不限”后无需填写范围。
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="紧急程度" prop="emergencyLevel">
            <el-radio-group v-model="formData.emergencyLevel">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.HRM_RECRUIT_EMERGENCY_LEVEL)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="招聘负责人" prop="ownerEmployeeId">
            <HrmEmployeeSelect
              v-model="formData.ownerEmployeeId"
              class="!w-1/1"
              :entry-status="HrmEmployeeEntryStatus.ACTIVE"
              placeholder="请选择招聘负责人"
              title="选择招聘负责人"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职位类型" prop="postTypeId">
            <el-tree-select
              v-model="formData.postTypeId"
              :data="postTypeTree"
              :props="defaultProps"
              check-strictly
              class="!w-1/1"
              clearable
              node-key="id"
              placeholder="请选择职位类型"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="面试官" prop="interviewEmployeeIds">
            <HrmEmployeeSelect
              v-model="formData.interviewEmployeeIds"
              class="!w-1/1"
              :entry-status="HrmEmployeeEntryStatus.ACTIVE"
              multiple
              placeholder="请选择面试官"
              title="选择面试官"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职位描述" prop="description">
            <el-input
              v-model="formData.description"
              :rows="4"
              maxlength="4000"
              placeholder="请输入职位描述"
              show-word-limit
              type="textarea"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { CommonStatusEnum } from '@/utils/constants'
import { defaultProps, handleTree } from '@/utils/tree'
import * as RecruitPostApi from '@/api/hrm/recruit/post'
import * as RecruitPostTypeApi from '@/api/hrm/recruit/post/type'
import type { FormInstance, FormRules } from 'element-plus'
import AreaSelect from '@/views/system/area/components/AreaSelect.vue'
import DeptSelect from '@/views/system/dept/components/DeptSelect.vue'
import HrmEmployeeSelect from '@/views/hrm/employee/components/HrmEmployeeSelect.vue'
import {
  AGE_UNLIMITED_VALUE,
  HrmEmployeeEntryStatus,
  HrmRecruitEmergencyLevel,
  HrmRecruitJobNature,
  HrmRecruitPostEducation,
  HrmRecruitSalaryUnit,
  HrmRecruitWorkTime,
  SALARY_NEGOTIABLE_UNIT_VALUE,
  SALARY_NEGOTIABLE_VALUE
} from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmRecruitPostForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref<RecruitPostApi.HrmRecruitPostVO>({
  id: undefined,
  postName: '',
  deptId: undefined,
  jobNature: HrmRecruitJobNature.FULL_TIME,
  areaId: undefined,
  recruitNum: undefined,
  reason: '',
  workTime: HrmRecruitWorkTime.UNLIMITED,
  educationRequire: HrmRecruitPostEducation.UNLIMITED,
  minSalary: undefined,
  maxSalary: undefined,
  salaryUnit: HrmRecruitSalaryUnit.MONTH,
  minAge: undefined,
  maxAge: undefined,
  latestEntryTime: undefined,
  ownerEmployeeId: undefined,
  interviewEmployeeIds: [],
  description: '',
  emergencyLevel: HrmRecruitEmergencyLevel.URGENT,
  postTypeId: undefined
})
const salaryNegotiable = ref(false) // 薪资是否面议
const ageUnlimited = ref(false) // 年龄是否不限

/** 校验薪资范围 */
function validateSalaryRange(_rule: unknown, _value: unknown, callback: (error?: Error) => void) {
  if (
    !salaryNegotiable.value &&
    formData.value.minSalary != null &&
    formData.value.maxSalary != null &&
    formData.value.minSalary > formData.value.maxSalary
  ) {
    callback(new Error('最低薪资不能大于最高薪资'))
    return
  }
  callback()
}

/** 校验年龄范围 */
function validateAgeRange(_rule: unknown, _value: unknown, callback: (error?: Error) => void) {
  if (
    !ageUnlimited.value &&
    formData.value.minAge != null &&
    formData.value.maxAge != null &&
    formData.value.minAge > formData.value.maxAge
  ) {
    callback(new Error('最小年龄不能大于最大年龄'))
    return
  }
  callback()
}

/** 表单校验规则 */
const formRules = reactive<FormRules>({
  postName: [
    { required: true, message: '职位名称不能为空', trigger: 'blur' },
    { max: 255, message: '职位名称不能超过 255 个字符', trigger: 'blur' }
  ],
  jobNature: [{ required: true, message: '工作性质不能为空', trigger: 'change' }],
  recruitNum: [{ type: 'number', min: 0, message: '招聘人数不能小于 0', trigger: 'blur' }],
  reason: [{ max: 255, message: '招聘原因不能超过 255 个字符', trigger: 'blur' }],
  minSalary: [{ validator: validateSalaryRange, trigger: ['blur', 'change'] }],
  minAge: [{ validator: validateAgeRange, trigger: ['blur', 'change'] }]
})
const formRef = ref<FormInstance>() // 表单 Ref
const postTypeTree = ref<Tree[]>([]) // 招聘职位类型树

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新建招聘职位' : '编辑招聘职位'
  formType.value = type
  resetForm()
  formLoading.value = true
  try {
    // 加载招聘职位类型树
    postTypeTree.value = handleTree(
      await RecruitPostTypeApi.getRecruitPostTypeList({ status: CommonStatusEnum.ENABLE })
    )
    // 修改时，设置表单数据
    if (id) {
      const data = await RecruitPostApi.getRecruitPost(id)
      // 将后端特殊值转换为表单的“面议”和“不限”选中状态
      salaryNegotiable.value =
        data.salaryUnit === SALARY_NEGOTIABLE_UNIT_VALUE ||
        (data.minSalary === SALARY_NEGOTIABLE_VALUE && data.maxSalary === SALARY_NEGOTIABLE_VALUE)
      ageUnlimited.value =
        data.minAge === AGE_UNLIMITED_VALUE && data.maxAge === AGE_UNLIMITED_VALUE
      formData.value = {
        ...data,
        interviewEmployeeIds: data.interviewEmployeeIds ?? []
      }
      if (salaryNegotiable.value) {
        formData.value.minSalary = undefined
        formData.value.maxSalary = undefined
        formData.value.salaryUnit = HrmRecruitSalaryUnit.MONTH
      }
      if (ageUnlimited.value) {
        formData.value.minAge = undefined
        formData.value.maxAge = undefined
      }
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 切换薪资面议 */
function handleSalaryNegotiableChange() {
  formData.value.minSalary = undefined
  formData.value.maxSalary = undefined
  formRef.value?.clearValidate('minSalary')
}

/** 切换年龄不限 */
function handleAgeUnlimitedChange() {
  formData.value.minAge = undefined
  formData.value.maxAge = undefined
  formRef.value?.clearValidate('minAge')
}

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
    const data: RecruitPostApi.HrmRecruitPostVO = {
      ...formData.value,
      minSalary: salaryNegotiable.value ? SALARY_NEGOTIABLE_VALUE : formData.value.minSalary,
      maxSalary: salaryNegotiable.value ? SALARY_NEGOTIABLE_VALUE : formData.value.maxSalary,
      salaryUnit: salaryNegotiable.value ? SALARY_NEGOTIABLE_UNIT_VALUE : formData.value.salaryUnit,
      minAge: ageUnlimited.value ? AGE_UNLIMITED_VALUE : formData.value.minAge,
      maxAge: ageUnlimited.value ? AGE_UNLIMITED_VALUE : formData.value.maxAge
    }
    if (formType.value === 'create') {
      await RecruitPostApi.createRecruitPost(data)
      message.success(t('common.createSuccess'))
    } else {
      await RecruitPostApi.updateRecruitPost(data)
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
  salaryNegotiable.value = false
  ageUnlimited.value = false
  formData.value = {
    id: undefined,
    postName: '',
    deptId: undefined,
    jobNature: HrmRecruitJobNature.FULL_TIME,
    areaId: undefined,
    recruitNum: undefined,
    reason: '',
    workTime: HrmRecruitWorkTime.UNLIMITED,
    educationRequire: HrmRecruitPostEducation.UNLIMITED,
    minSalary: undefined,
    maxSalary: undefined,
    salaryUnit: HrmRecruitSalaryUnit.MONTH,
    minAge: undefined,
    maxAge: undefined,
    latestEntryTime: undefined,
    ownerEmployeeId: undefined,
    interviewEmployeeIds: [],
    description: '',
    emergencyLevel: HrmRecruitEmergencyLevel.URGENT,
    postTypeId: undefined
  }
  formRef.value?.resetFields()
}
</script>
