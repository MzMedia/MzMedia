<template>
  <doc-alert title="【员工】员工管理" url="https://doc.iocoder.cn/hrm/employee/" />

  <ContentWrap>
    <div class="relative">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="新建员工字段设置" name="create">
          <EmployeeCreateFieldConfig ref="createFieldConfigRef" />
        </el-tab-pane>
        <el-tab-pane label="员工档案设置" name="archive">
          <EmployeeArchiveFieldConfig ref="archiveFieldConfigRef" />
        </el-tab-pane>
      </el-tabs>
      <el-button
        v-hasPermi="['hrm:employee:config:update']"
        class="absolute right-0 top-0"
        type="primary"
        :loading="saving"
        @click="submitForm"
      >
        <Icon icon="ep:check" class="mr-5px" />保存
      </el-button>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import EmployeeArchiveFieldConfig from './EmployeeArchiveFieldConfig.vue'
import EmployeeCreateFieldConfig from './EmployeeCreateFieldConfig.vue'

defineOptions({ name: 'HrmEmployeeConfig' })

const activeTab = ref('create') // 当前选中的页签
const saving = ref(false) // 保存按钮的加载中
const createFieldConfigRef = ref<InstanceType<typeof EmployeeCreateFieldConfig>>() // 新建员工字段设置 Ref
const archiveFieldConfigRef = ref<InstanceType<typeof EmployeeArchiveFieldConfig>>() // 员工档案设置 Ref

/** 保存字段配置 */
async function submitForm() {
  saving.value = true
  try {
    if (activeTab.value === 'create') {
      await createFieldConfigRef.value?.submitForm()
      return
    }
    await archiveFieldConfigRef.value?.submitForm()
  } finally {
    saving.value = false
  }
}
</script>
