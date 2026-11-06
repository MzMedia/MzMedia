<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="场景名称" prop="sceneName">
              <el-input v-model="queryParams.sceneName" placeholder="请输入场景名称" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:sceneRule:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:sceneRule:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:sceneRule:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:sceneRule:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="sceneRuleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="场景ID" align="center" prop="id" v-if="true" />
        <el-table-column label="场景名称" align="center" prop="sceneName" />
        <el-table-column label="执行动作" align="center" prop="actions" />
        <el-table-column label="场景状态" align="center" prop="status" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:sceneRule:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:sceneRule:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改场景联动对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="sceneRuleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="场景名称" prop="sceneName">
          <el-input v-model="form.sceneName" placeholder="请输入场景名称" />
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

<script setup name="SceneRule" lang="ts">
import { listSceneRule, getSceneRule, delSceneRule, addSceneRule, updateSceneRule } from '@/api/iot/sceneRule';
import { SceneRuleVO, SceneRuleQuery, SceneRuleForm } from '@/api/iot/sceneRule/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const sceneRuleList = ref<SceneRuleVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const sceneRuleFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: SceneRuleForm = {
  id: undefined,
  sceneName: undefined,
  actions: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<SceneRuleForm, SceneRuleQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sceneName: undefined,
    actions: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "场景ID不能为空", trigger: "blur" }
    ],
    sceneName: [
      { required: true, message: "场景名称不能为空", trigger: "blur" }
    ],
    actions: [
      { required: true, message: "执行动作不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "场景状态不能为空", trigger: "change" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询场景联动列表 */
const getList = async () => {
  loading.value = true;
  const res = await listSceneRule(queryParams.value);
  sceneRuleList.value = res.rows;
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
  sceneRuleFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: SceneRuleVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加场景联动";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: SceneRuleVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getSceneRule(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改场景联动";
}

/** 提交按钮 */
const submitForm = () => {
  sceneRuleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateSceneRule(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addSceneRule(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: SceneRuleVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除场景联动编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delSceneRule(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/sceneRule/export', {
    ...queryParams.value
  }, `sceneRule_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
