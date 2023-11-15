<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="设备ID" prop="deviceId">
            <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="设备名称" prop="deviceName">
            <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="设备昵称" prop="tag">
            <el-input v-model="queryParams.tag" placeholder="请输入设备昵称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="激活时间" prop="activeTime">
            <el-date-picker clearable
              v-model="queryParams.activeTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择激活时间"
            />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:device:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:device:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:device:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:device:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户ID" align="center" prop="userId" v-if="true" />
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="设备名称" align="center" prop="deviceName" />
        <el-table-column label="产品key" align="center" prop="productKey" />
        <el-table-column label="设备编号" align="center" prop="serialNumber" />
        <el-table-column label="产品密钥" align="center" prop="secret" />
        <el-table-column label="固件版本" align="center" prop="firmwareVersion" />
        <el-table-column label="设备状态" align="center" prop="status" />
        <el-table-column label="设备IP" align="center" prop="ip" />
        <el-table-column label="激活时间" align="center" prop="activeTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.activeTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="物模型值" align="center" prop="thingsModel" />
        <el-table-column label="设备摘要" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:device:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:device:remove']"></el-button>
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
    <!-- 添加或修改设备信息对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="deviceFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="子用户ID" prop="subUid">
          <el-input v-model="form.subUid" placeholder="请输入子用户ID" />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="子设备ID" prop="subDid">
          <el-input v-model="form.subDid" placeholder="请输入子设备ID" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入设备昵称" />
        </el-form-item>
        <el-form-item label="产品key" prop="productKey">
          <el-input v-model="form.productKey" placeholder="请输入产品key" />
        </el-form-item>
        <el-form-item label="设备编号" prop="serialNumber">
          <el-input v-model="form.serialNumber" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="产品密钥" prop="secret">
          <el-input v-model="form.secret" placeholder="请输入产品密钥" />
        </el-form-item>
        <el-form-item label="固件版本" prop="firmwareVersion">
          <el-input v-model="form.firmwareVersion" placeholder="请输入固件版本" />
        </el-form-item>
        <el-form-item label="设备昵称" prop="tag">
          <el-input v-model="form.tag" placeholder="请输入设备昵称" />
        </el-form-item>
        <el-form-item label="设备IP" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入设备IP" />
        </el-form-item>
        <el-form-item label="激活时间" prop="activeTime">
          <el-date-picker clearable
            v-model="form.activeTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择激活时间">
          </el-date-picker>
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

<script setup name="Device" lang="ts">
import { listDevice, getDevice, delDevice, addDevice, updateDevice } from '@/api/iot/device';
import { DeviceVO, DeviceQuery, DeviceForm } from '@/api/iot/device/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const deviceList = ref<DeviceVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const deviceFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: DeviceForm = {
  userId: undefined,
  subUid: undefined,
  deviceId: undefined,
  subDid: undefined,
  deviceName: undefined,
  nickName: undefined,
  productKey: undefined,
  serialNumber: undefined,
  secret: undefined,
  firmwareVersion: undefined,
  locate: undefined,
  tag: undefined,
  status: undefined,
  ip: undefined,
  activeTime: undefined,
  thingsModel: undefined,
  summary: undefined,
  remark: undefined
}
const data = reactive<PageData<DeviceForm, DeviceQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    subUid: undefined,
    deviceId: undefined,
    subDid: undefined,
    deviceName: undefined,
    nickName: undefined,
    productKey: undefined,
    serialNumber: undefined,
    secret: undefined,
    firmwareVersion: undefined,
    locate: undefined,
    tag: undefined,
    status: undefined,
    ip: undefined,
    activeTime: undefined,
    thingsModel: undefined,
    summary: undefined,
    params: {
    }
  },
  rules: {
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    subUid: [
      { required: true, message: "子用户ID不能为空", trigger: "blur" }
    ],
    deviceId: [
      { required: true, message: "设备ID不能为空", trigger: "blur" }
    ],
    subDid: [
      { required: true, message: "子设备ID不能为空", trigger: "blur" }
    ],
    deviceName: [
      { required: true, message: "设备名称不能为空", trigger: "blur" }
    ],
    nickName: [
      { required: true, message: "设备昵称不能为空", trigger: "blur" }
    ],
    productKey: [
      { required: true, message: "产品key不能为空", trigger: "blur" }
    ],
    serialNumber: [
      { required: true, message: "设备编号不能为空", trigger: "blur" }
    ],
    secret: [
      { required: true, message: "产品密钥不能为空", trigger: "blur" }
    ],
    firmwareVersion: [
      { required: true, message: "固件版本不能为空", trigger: "blur" }
    ],
    locate: [
      { required: true, message: "设备昵称不能为空", trigger: "blur" }
    ],
    tag: [
      { required: true, message: "设备昵称不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "设备状态不能为空", trigger: "change" }
    ],
    ip: [
      { required: true, message: "设备IP不能为空", trigger: "blur" }
    ],
    activeTime: [
      { required: true, message: "激活时间不能为空", trigger: "blur" }
    ],
    thingsModel: [
      { required: true, message: "物模型值不能为空", trigger: "blur" }
    ],
    summary: [
      { required: true, message: "设备摘要", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备信息列表 */
const getList = async () => {
  loading.value = true;
  const res = await listDevice(queryParams.value);
  deviceList.value = res.rows;
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
  deviceFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: DeviceVO[]) => {
  ids.value = selection.map(item => item.userId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加设备信息";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: DeviceVO) => {
  reset();
  const _userId = row?.userId || ids.value[0]
  const res = await getDevice(_userId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改设备信息";
}

/** 提交按钮 */
const submitForm = () => {
  deviceFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.userId) {
        await updateDevice(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addDevice(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: DeviceVO) => {
  const _userIds = row?.userId || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备信息编号为"' + _userIds + '"的数据项？').finally(() => loading.value = false);
  await delDevice(_userIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/device/export', {
    ...queryParams.value
  }, `device_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
