<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="所属场景ID" prop="sceneId">
              <el-input v-model="queryParams.sceneId" placeholder="请输入所属场景ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备ID" prop="deviceId">
              <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品ID" prop="productId">
              <el-input v-model="queryParams.productId" placeholder="请输入产品ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="触发源" prop="source">
              <el-input v-model="queryParams.source" placeholder="请输入触发源" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="物模型标识符" prop="modelId">
              <el-input v-model="queryParams.modelId" placeholder="请输入物模型标识符" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="物模型名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入物模型名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="物模型值" prop="value">
              <el-input v-model="queryParams.value" placeholder="请输入物模型值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="操作符" prop="operator">
              <el-input v-model="queryParams.operator" placeholder="请输入操作符" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:sceneTrigger:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:sceneTrigger:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:sceneTrigger:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:sceneTrigger:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="sceneTriggerList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="场景触发器ID" align="center" prop="id" v-if="true" />
        <el-table-column label="所属场景ID" align="center" prop="sceneId" />
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="产品ID" align="center" prop="productId" />
        <el-table-column label="告警状态" align="center" prop="status" />
        <el-table-column label="触发源" align="center" prop="source" />
        <el-table-column label="物模型标识符" align="center" prop="modelId" />
        <el-table-column label="物模型名称" align="center" prop="name" />
        <el-table-column label="物模型值" align="center" prop="value" />
        <el-table-column label="操作符" align="center" prop="operator" />
        <el-table-column label="物模型类别" align="center" prop="type" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:sceneTrigger:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:sceneTrigger:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改场景联动触发器对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="sceneTriggerFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属场景ID" prop="sceneId">
          <el-input v-model="form.sceneId" placeholder="请输入所属场景ID" />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="产品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品ID" />
        </el-form-item>
        <el-form-item label="触发源" prop="source">
          <el-input v-model="form.source" placeholder="请输入触发源" />
        </el-form-item>
        <el-form-item label="物模型标识符" prop="modelId">
          <el-input v-model="form.modelId" placeholder="请输入物模型标识符" />
        </el-form-item>
        <el-form-item label="物模型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入物模型名称" />
        </el-form-item>
        <el-form-item label="物模型值" prop="value">
          <el-input v-model="form.value" placeholder="请输入物模型值" />
        </el-form-item>
        <el-form-item label="操作符" prop="operator">
          <el-input v-model="form.operator" placeholder="请输入操作符" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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

<script setup name="SceneTrigger" lang="ts">
import { listSceneTrigger, getSceneTrigger, delSceneTrigger, addSceneTrigger, updateSceneTrigger } from '@/api/iot/sceneTrigger';
import { SceneTriggerVO, SceneTriggerQuery, SceneTriggerForm } from '@/api/iot/sceneTrigger/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const sceneTriggerList = ref<SceneTriggerVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const sceneTriggerFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: SceneTriggerForm = {
  id: undefined,
  sceneId: undefined,
  deviceId: undefined,
  productId: undefined,
  status: undefined,
  source: undefined,
  modelId: undefined,
  name: undefined,
  value: undefined,
  operator: undefined,
  type: undefined,
  remark: undefined
}
const data = reactive<PageData<SceneTriggerForm, SceneTriggerQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sceneId: undefined,
    deviceId: undefined,
    productId: undefined,
    status: undefined,
    source: undefined,
    modelId: undefined,
    name: undefined,
    value: undefined,
    operator: undefined,
    type: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "场景触发器ID不能为空", trigger: "blur" }
    ],
    sceneId: [
      { required: true, message: "所属场景ID不能为空", trigger: "blur" }
    ],
    deviceId: [
      { required: true, message: "设备ID不能为空", trigger: "blur" }
    ],
    productId: [
      { required: true, message: "产品ID不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "告警状态不能为空", trigger: "change" }
    ],
    source: [
      { required: true, message: "触发源不能为空", trigger: "blur" }
    ],
    modelId: [
      { required: true, message: "物模型标识符不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "物模型名称不能为空", trigger: "blur" }
    ],
    value: [
      { required: true, message: "物模型值不能为空", trigger: "blur" }
    ],
    operator: [
      { required: true, message: "操作符不能为空", trigger: "blur" }
    ],
    type: [
      { required: true, message: "物模型类别不能为空", trigger: "change" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询场景联动触发器列表 */
const getList = async () => {
  loading.value = true;
  const res = await listSceneTrigger(queryParams.value);
  sceneTriggerList.value = res.rows;
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
  sceneTriggerFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: SceneTriggerVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加场景联动触发器";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: SceneTriggerVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getSceneTrigger(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改场景联动触发器";
}

/** 提交按钮 */
const submitForm = () => {
  sceneTriggerFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateSceneTrigger(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addSceneTrigger(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: SceneTriggerVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除场景联动触发器编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delSceneTrigger(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/sceneTrigger/export', {
    ...queryParams.value
  }, `sceneTrigger_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
