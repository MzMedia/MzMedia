<template>
  <Dialog v-model="dialogVisible" title="选择结转模板" width="760px">
    <div class="mb-16px flex items-center gap-20px">
      <el-tabs
        v-model="category"
        class="min-w-0 flex-1 [&_.el-tabs__content]:hidden [&_.el-tabs__header]:!m-0"
      >
        <el-tab-pane
          v-for="item in categoryOptions"
          :key="item.value"
          :label="item.label"
          :name="item.value"
        />
      </el-tabs>
      <el-dropdown
        v-if="fmsStore.isAccountSetWritable"
        v-hasPermi="['fms:closing:update']"
        trigger="click"
        @command="handleCreate"
      >
        <el-button type="primary" plain>
          <Icon icon="ep:plus" class="mr-5px" />新增
          <Icon icon="ep:arrow-down" class="ml-5px" />
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="template">新增模板</el-dropdown-item>
            <el-dropdown-item command="scheme">新增方案</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 结账模板列表 -->
    <el-table
      v-loading="loading"
      :data="filteredTemplates"
      border
      stripe
      highlight-current-row
      @row-dblclick="selectTemplate"
    >
      <el-table-column label="模板名称" prop="name" min-width="260" />
      <el-table-column label="分录数" align="center" width="90">
        <template #default="scope">{{ scope.row.subjects.length }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="210">
        <template #default="scope">
          <el-button link type="primary" @click="selectTemplate(scope.row)">使用</el-button>
          <template v-if="fmsStore.isAccountSetWritable">
            <el-button
              v-hasPermi="['fms:closing:update']"
              link
              type="primary"
              @click="templateFormRef?.open('update', scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPermi="['fms:closing:update']"
              link
              type="danger"
              @click="deleteTemplate(scope.row)"
            >
              删除
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <div class="mt-12px text-12px text-[var(--el-text-color-secondary)]">
      双击模板可直接进入结账方案表单
    </div>
  </Dialog>

  <!-- 添加或修改结账模板 -->
  <ClosingTemplateForm
    ref="templateFormRef"
    :account-set-id="accountSetId"
    :subjects="subjects"
    @success="getList"
  />
</template>

<script lang="ts" setup>
import { FmsClosingTemplateApi, type FmsClosingTemplateVO } from '@/api/fms/closing/template'
import type { FmsSubjectVO } from '@/api/fms/config/subject'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_CLOSING_TEMPLATE_CATEGORY } from '@/views/fms/utils/constants'
import ClosingTemplateForm from './ClosingTemplateForm.vue'

defineOptions({ name: 'FmsClosingTemplateSelect' })

const props = defineProps<{
  accountSetId: number // 账套编号
  subjects: FmsSubjectVO[] // 末级科目列表
}>()
const emit = defineEmits<{
  select: [template?: FmsClosingTemplateVO]
}>()
const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态
const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 列表的加载中
const category = ref<number>(FMS_CLOSING_TEMPLATE_CATEGORY.DAILY_EXPENSE) // 当前模板分类
const templates = ref<FmsClosingTemplateVO[]>([]) // 结账模板列表
const templateFormRef = ref<InstanceType<typeof ClosingTemplateForm>>() // 结账模板表单
const categoryOptions = getIntDictOptions(DICT_TYPE.FMS_CLOSING_TEMPLATE_CATEGORY) // 模板分类选项
const filteredTemplates = computed(() =>
  templates.value.filter((item) => item.category === category.value)
) // 当前分类的模板列表

/** 打开弹窗 */
async function open() {
  category.value = FMS_CLOSING_TEMPLATE_CATEGORY.DAILY_EXPENSE
  dialogVisible.value = true
  await getList()
}

/** 查询结账模板列表 */
async function getList() {
  loading.value = true
  try {
    templates.value = await FmsClosingTemplateApi.getClosingTemplateList(props.accountSetId)
  } finally {
    loading.value = false
  }
}

/** 使用结账模板 */
function selectTemplate(template: FmsClosingTemplateVO) {
  emit('select', template)
  dialogVisible.value = false
}

/** 新增结账方案 */
function createBlankScheme() {
  emit('select')
  dialogVisible.value = false
}

/** 处理新增操作 */
function handleCreate(command: 'template' | 'scheme') {
  if (command === 'template') {
    templateFormRef.value?.open('create', undefined, category.value)
    return
  }
  createBlankScheme()
}

/** 删除结账模板 */
async function deleteTemplate(template: FmsClosingTemplateVO) {
  if (!template.id) return
  try {
    await message.delConfirm(`确认删除结账模板“${template.name}”吗？`)
    await FmsClosingTemplateApi.deleteClosingTemplate(props.accountSetId, template.id)
    message.success('删除成功')
    await getList()
  } catch {}
}

defineExpose({ open })
</script>
