<template>
  <!-- 操作栏 -->
  <el-row class="mb-12px" justify="end">
    <el-button v-hasPermi="['hrm:employee:update']" type="primary" plain @click="openForm()">
      <Icon icon="ep:plus" class="mr-5px" />新增
    </el-button>
  </el-row>
  <!-- 列表 -->
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column label="学历" prop="education" min-width="100">
      <template #default="{ row }">
        <dict-tag
          v-if="row.education != null"
          :type="DICT_TYPE.HRM_EMPLOYEE_EDUCATION"
          :value="row.education"
        />
        <span v-else>-</span>
      </template>
    </el-table-column>
    <el-table-column label="毕业院校" prop="graduateSchool" min-width="150" />
    <el-table-column label="专业" prop="major" min-width="120" />
    <el-table-column
      label="入学日期"
      prop="admissionTime"
      width="120"
      :formatter="dateFormatter2"
    />
    <el-table-column
      label="毕业日期"
      prop="graduationTime"
      width="120"
      :formatter="dateFormatter2"
    />
    <el-table-column label="教学方式" prop="teachingMethods" width="110">
      <template #default="{ row }">
        {{ formatEmployeeTeachingMethod(row.teachingMethods) }}
      </template>
    </el-table-column>
    <el-table-column label="第一学历" prop="firstDegree" width="100">
      <template #default="{ row }">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="row.firstDegree" />
      </template>
    </el-table-column>
    <el-table-column label="操作" fixed="right" width="120">
      <template #default="{ row }">
        <el-button v-hasPermi="['hrm:employee:update']" link type="primary" @click="openForm(row)">
          编辑
        </el-button>
        <el-button
          v-hasPermi="['hrm:employee:delete']"
          link
          type="danger"
          @click="handleDelete(row.id)"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 表单弹窗：添加/修改 -->
  <EmployeeEducationExperienceForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import * as EducationExperienceApi from '@/api/hrm/employee/education-experience'
import { formatEmployeeTeachingMethod } from '@/views/hrm/utils/format'
import EmployeeEducationExperienceForm from './EmployeeEducationExperienceForm.vue'

defineOptions({ name: 'HrmEmployeeEducationExperienceList' })

const props = defineProps<{
  employeeId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref<EducationExperienceApi.HrmEmployeeEducationExperienceVO[]>([]) // 列表的数据

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    list.value = await EducationExperienceApi.getEmployeeEducationExperienceList(props.employeeId)
  } finally {
    loading.value = false
  }
}

const formRef = ref<InstanceType<typeof EmployeeEducationExperienceForm>>() // 表单 Ref

/** 添加/修改操作 */
function openForm(row?: EducationExperienceApi.HrmEmployeeEducationExperienceVO) {
  formRef.value?.open(props.employeeId, row)
}

/** 删除按钮操作 */
async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await EducationExperienceApi.deleteEmployeeEducationExperience(id)
    message.success('删除成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
