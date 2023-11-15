<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="物模型名称" prop="templateName">
            <el-input v-model="queryParams.templateName" placeholder="请输入物模型名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="标识符，产品下唯一" prop="identifier">
            <el-input v-model="queryParams.identifier" placeholder="请输入标识符，产品下唯一" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否系统通用" prop="isSys">
            <el-input v-model="queryParams.isSys" placeholder="请输入是否系统通用" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否图表展示" prop="isChart">
            <el-input v-model="queryParams.isChart" placeholder="请输入是否图表展示" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否实时监测" prop="isMonitor">
            <el-input v-model="queryParams.isMonitor" placeholder="请输入是否实时监测" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否历史存储 (0-否，1-是）" prop="isHistory">
            <el-input v-model="queryParams.isHistory" placeholder="请输入是否历史存储 (0-否，1-是）" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否只读数据(0-否，1-是)" prop="isReadonly">
            <el-input v-model="queryParams.isReadonly" placeholder="请输入是否只读数据(0-否，1-是)" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否设备分享权限(0-否，1-是)" prop="isSharePerm">
            <el-input v-model="queryParams.isSharePerm" placeholder="请输入是否设备分享权限(0-否，1-是)" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="排序，值越大，排序越靠前" prop="modelOrder">
            <el-input v-model="queryParams.modelOrder" placeholder="请输入排序，值越大，排序越靠前" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:modelTemplate:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:modelTemplate:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:modelTemplate:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:modelTemplate:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="modelTemplateList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="物模型ID" align="center" prop="templateId" v-if="true" />
        <el-table-column label="物模型名称" align="center" prop="templateName" />
        <el-table-column label="标识符，产品下唯一" align="center" prop="identifier" />
        <el-table-column label="模型类别" align="center" prop="type" />
        <el-table-column label="数据类型" align="center" prop="datatype" />
        <el-table-column label="数据定义" align="center" prop="specs" />
        <el-table-column label="是否系统通用" align="center" prop="isSys" />
        <el-table-column label="是否图表展示" align="center" prop="isChart" />
        <el-table-column label="是否实时监测" align="center" prop="isMonitor" />
        <el-table-column label="是否历史存储 (0-否，1-是）" align="center" prop="isHistory" />
        <el-table-column label="是否只读数据(0-否，1-是)" align="center" prop="isReadonly" />
        <el-table-column label="是否设备分享权限(0-否，1-是)" align="center" prop="isSharePerm" />
        <el-table-column label="排序，值越大，排序越靠前" align="center" prop="modelOrder" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:modelTemplate:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:modelTemplate:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </el-card>
    <!-- 添加或修改物模型模板对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="modelTemplateFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="物模型名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入物模型名称" />
        </el-form-item>
        <el-form-item label="标识符，产品下唯一" prop="identifier">
          <el-input v-model="form.identifier" placeholder="请输入标识符，产品下唯一" />
        </el-form-item>
        <el-form-item label="是否系统通用" prop="isSys">
          <el-input v-model="form.isSys" placeholder="请输入是否系统通用" />
        </el-form-item>
        <el-form-item label="是否图表展示" prop="isChart">
          <el-input v-model="form.isChart" placeholder="请输入是否图表展示" />
        </el-form-item>
        <el-form-item label="是否实时监测" prop="isMonitor">
          <el-input v-model="form.isMonitor" placeholder="请输入是否实时监测" />
        </el-form-item>
        <el-form-item label="是否历史存储 (0-否，1-是）" prop="isHistory">
          <el-input v-model="form.isHistory" placeholder="请输入是否历史存储 (0-否，1-是）" />
        </el-form-item>
        <el-form-item label="是否只读数据(0-否，1-是)" prop="isReadonly">
          <el-input v-model="form.isReadonly" placeholder="请输入是否只读数据(0-否，1-是)" />
        </el-form-item>
        <el-form-item label="是否设备分享权限(0-否，1-是)" prop="isSharePerm">
          <el-input v-model="form.isSharePerm" placeholder="请输入是否设备分享权限(0-否，1-是)" />
        </el-form-item>
        <el-form-item label="排序，值越大，排序越靠前" prop="modelOrder">
          <el-input v-model="form.modelOrder" placeholder="请输入排序，值越大，排序越靠前" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ModelTemplate" lang="ts">
import { listModelTemplate, getModelTemplate, delModelTemplate, addModelTemplate, updateModelTemplate } from '@/api/iot/modelTemplate';
import { ModelTemplateVO, ModelTemplateQuery, ModelTemplateForm } from '@/api/iot/modelTemplate/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const modelTemplateList = ref<ModelTemplateVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const modelTemplateFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ModelTemplateForm = {
  templateId: undefined,
  templateName: undefined,
  identifier: undefined,
  type: undefined,
  datatype: undefined,
  specs: undefined,
  isSys: undefined,
  isChart: undefined,
  isMonitor: undefined,
  isHistory: undefined,
  isReadonly: undefined,
  isSharePerm: undefined,
  modelOrder: undefined,
  remark: undefined
}
const data = reactive<PageData<ModelTemplateForm, ModelTemplateQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    templateName: undefined,
    identifier: undefined,
    type: undefined,
    datatype: undefined,
    specs: undefined,
    isSys: undefined,
    isChart: undefined,
    isMonitor: undefined,
    isHistory: undefined,
    isReadonly: undefined,
    isSharePerm: undefined,
    modelOrder: undefined,
    params: {
    }
  },
  rules: {
    templateId: [
      { required: true, message: "物模型ID不能为空", trigger: "blur" }
    ],
    templateName: [
      { required: true, message: "物模型名称不能为空", trigger: "blur" }
    ],
    identifier: [
      { required: true, message: "标识符，产品下唯一不能为空", trigger: "blur" }
    ],
    type: [
      { required: true, message: "模型类别不能为空", trigger: "change" }
    ],
    datatype: [
      { required: true, message: "数据类型不能为空", trigger: "change" }
    ],
    specs: [
      { required: true, message: "数据定义不能为空", trigger: "blur" }
    ],
    isSys: [
      { required: true, message: "是否系统通用不能为空", trigger: "blur" }
    ],
    isChart: [
      { required: true, message: "是否图表展示不能为空", trigger: "blur" }
    ],
    isMonitor: [
      { required: true, message: "是否实时监测不能为空", trigger: "blur" }
    ],
    isHistory: [
      { required: true, message: "是否历史存储 (0-否，1-是）不能为空", trigger: "blur" }
    ],
    isReadonly: [
      { required: true, message: "是否只读数据(0-否，1-是)不能为空", trigger: "blur" }
    ],
    isSharePerm: [
      { required: true, message: "是否设备分享权限(0-否，1-是)不能为空", trigger: "blur" }
    ],
    modelOrder: [
      { required: true, message: "排序，值越大，排序越靠前不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询物模型模板列表 */
const getList = async () => {
  loading.value = true;
  const res = await listModelTemplate(queryParams.value);
  modelTemplateList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  modelTemplateFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ModelTemplateVO[]) => {
  ids.value = selection.map(item => item.templateId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加物模型模板";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ModelTemplateVO) => {
  reset();
  const _templateId = row?.templateId || ids.value[0]
  const res = await getModelTemplate(_templateId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改物模型模板";
}

/** 提交按钮 */
const submitForm = () => {
  modelTemplateFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.templateId) {
        await updateModelTemplate(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addModelTemplate(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ModelTemplateVO) => {
  const _templateIds = row?.templateId || ids.value;
  await proxy?.$modal.confirm('是否确认删除物模型模板编号为"' + _templateIds + '"的数据项？').finally(() => loading.value = false);
  await delModelTemplate(_templateIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/modelTemplate/export', {
    ...queryParams.value
  }, `modelTemplate_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
