<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="设备ID" prop="deviceId">
              <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="标识符" prop="identity">
              <el-input v-model="queryParams.identity" placeholder="请输入标识符" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="物模型名称" prop="modelName">
              <el-input v-model="queryParams.modelName" placeholder="请输入物模型名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="日志值" prop="logValue">
              <el-input v-model="queryParams.logValue" placeholder="请输入日志值" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:deviceLog:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:deviceLog:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:deviceLog:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:deviceLog:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="deviceLogList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="设备监测信息ID" align="center" prop="id" v-if="true" />
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="标识符" align="center" prop="identity" />
        <el-table-column label="物模型名称" align="center" prop="modelName" />
        <el-table-column label="类型" align="center" prop="logType" />
        <el-table-column label="日志值" align="center" prop="logValue" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:deviceLog:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:deviceLog:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改设备日志对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="deviceLogFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="标识符" prop="identity">
          <el-input v-model="form.identity" placeholder="请输入标识符" />
        </el-form-item>
        <el-form-item label="物模型名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入物模型名称" />
        </el-form-item>
        <el-form-item label="日志值" prop="logValue">
          <el-input v-model="form.logValue" placeholder="请输入日志值" />
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

<script setup name="DeviceLog" lang="ts">
import { listDeviceLog, getDeviceLog, delDeviceLog, addDeviceLog, updateDeviceLog } from '@/api/iot/deviceLog';
import { DeviceLogVO, DeviceLogQuery, DeviceLogForm } from '@/api/iot/deviceLog/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const deviceLogList = ref<DeviceLogVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const deviceLogFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: DeviceLogForm = {
  id: undefined,
  deviceId: undefined,
  identity: undefined,
  modelName: undefined,
  logType: undefined,
  logValue: undefined,
  remark: undefined
}
const data = reactive<PageData<DeviceLogForm, DeviceLogQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceId: undefined,
    identity: undefined,
    modelName: undefined,
    logType: undefined,
    logValue: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "设备监测信息ID不能为空", trigger: "blur" }
    ],
    deviceId: [
      { required: true, message: "设备ID不能为空", trigger: "blur" }
    ],
    identity: [
      { required: true, message: "标识符不能为空", trigger: "blur" }
    ],
    modelName: [
      { required: true, message: "物模型名称不能为空", trigger: "blur" }
    ],
    logType: [
      { required: true, message: "类型不能为空", trigger: "change" }
    ],
    logValue: [
      { required: true, message: "日志值不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备日志列表 */
const getList = async () => {
  loading.value = true;
  const res = await listDeviceLog(queryParams.value);
  deviceLogList.value = res.rows;
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
  deviceLogFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: DeviceLogVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加设备日志";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: DeviceLogVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getDeviceLog(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改设备日志";
}

/** 提交按钮 */
const submitForm = () => {
  deviceLogFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateDeviceLog(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addDeviceLog(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: DeviceLogVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备日志编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delDeviceLog(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/deviceLog/export', {
    ...queryParams.value
  }, `deviceLog_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
