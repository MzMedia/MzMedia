<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="告警名称" prop="alertName">
              <el-input v-model="queryParams.alertName" placeholder="请输入告警名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="告警级别" prop="alertLevel">
              <el-input v-model="queryParams.alertLevel" placeholder="请输入告警级别" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="所属场景ID" prop="sceneId">
              <el-input v-model="queryParams.sceneId" placeholder="请输入所属场景ID" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:alertRule:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:alertRule:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:alertRule:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:alertRule:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="alertRuleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="告警ID" align="center" prop="id" v-if="true" />
        <el-table-column label="告警名称" align="center" prop="alertName" />
        <el-table-column label="告警级别" align="center" prop="alertLevel" />
        <el-table-column label="所属场景ID" align="center" prop="sceneId" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:alertRule:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:alertRule:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改设备告警规则对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="alertRuleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="告警名称" prop="alertName">
          <el-input v-model="form.alertName" placeholder="请输入告警名称" />
        </el-form-item>
        <el-form-item label="告警级别" prop="alertLevel">
          <el-input v-model="form.alertLevel" placeholder="请输入告警级别" />
        </el-form-item>
        <el-form-item label="所属场景ID" prop="sceneId">
          <el-input v-model="form.sceneId" placeholder="请输入所属场景ID" />
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

<script setup name="AlertRule" lang="ts">
import { listAlertRule, getAlertRule, delAlertRule, addAlertRule, updateAlertRule } from '@/api/iot/alertRule';
import { AlertRuleVO, AlertRuleQuery, AlertRuleForm } from '@/api/iot/alertRule/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const alertRuleList = ref<AlertRuleVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const alertRuleFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: AlertRuleForm = {
  id: undefined,
  alertName: undefined,
  alertLevel: undefined,
  sceneId: undefined,
  remark: undefined
}
const data = reactive<PageData<AlertRuleForm, AlertRuleQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    alertName: undefined,
    alertLevel: undefined,
    sceneId: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "告警ID不能为空", trigger: "blur" }
    ],
    alertName: [
      { required: true, message: "告警名称不能为空", trigger: "blur" }
    ],
    alertLevel: [
      { required: true, message: "告警级别不能为空", trigger: "blur" }
    ],
    sceneId: [
      { required: true, message: "所属场景ID不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备告警规则列表 */
const getList = async () => {
  loading.value = true;
  const res = await listAlertRule(queryParams.value);
  alertRuleList.value = res.rows;
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
  alertRuleFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: AlertRuleVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加设备告警规则";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: AlertRuleVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getAlertRule(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改设备告警规则";
}

/** 提交按钮 */
const submitForm = () => {
  alertRuleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateAlertRule(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addAlertRule(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: AlertRuleVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备告警规则编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delAlertRule(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/alertRule/export', {
    ...queryParams.value
  }, `alertRule_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
