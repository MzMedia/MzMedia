<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="产品ID" prop="productId">
              <el-input v-model="queryParams.productId" placeholder="请输入产品ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备SipID" prop="deviceSipId">
              <el-input v-model="queryParams.deviceSipId" placeholder="请输入设备SipID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="厂商名称" prop="manufacturer">
              <el-input v-model="queryParams.manufacturer" placeholder="请输入厂商名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品型号" prop="model">
              <el-input v-model="queryParams.model" placeholder="请输入产品型号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="固件版本" prop="firmware">
              <el-input v-model="queryParams.firmware" placeholder="请输入固件版本" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="传输模式" prop="transport">
              <el-input v-model="queryParams.transport" placeholder="请输入传输模式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="流模式" prop="streamMode">
              <el-input v-model="queryParams.streamMode" placeholder="请输入流模式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="在线状态" prop="online">
              <el-input v-model="queryParams.online" placeholder="请输入在线状态" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="注册时间" prop="registerTime">
              <el-date-picker clearable
                v-model="queryParams.registerTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择注册时间"
              />
            </el-form-item>
            <el-form-item label="最后上线时间" prop="lastConnectTime">
              <el-date-picker clearable
                v-model="queryParams.lastConnectTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择最后上线时间"
              />
            </el-form-item>
            <el-form-item label="激活时间" prop="activeTime">
              <el-date-picker clearable
                v-model="queryParams.activeTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择激活时间"
              />
            </el-form-item>
            <el-form-item label="设备入网IP" prop="ip">
              <el-input v-model="queryParams.ip" placeholder="请输入设备入网IP" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备接入端口号" prop="port">
              <el-input v-model="queryParams.port" placeholder="请输入设备接入端口号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备地址" prop="hostAddress">
              <el-input v-model="queryParams.hostAddress" placeholder="请输入设备地址" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:sipDevice:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:sipDevice:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:sipDevice:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:sipDevice:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="sipDeviceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="设备ID" align="center" prop="id" v-if="true" />
        <el-table-column label="产品ID" align="center" prop="productId" />
        <el-table-column label="设备SipID" align="center" prop="deviceSipId" />
        <el-table-column label="设备名称" align="center" prop="deviceName" />
        <el-table-column label="厂商名称" align="center" prop="manufacturer" />
        <el-table-column label="产品型号" align="center" prop="model" />
        <el-table-column label="固件版本" align="center" prop="firmware" />
        <el-table-column label="传输模式" align="center" prop="transport" />
        <el-table-column label="流模式" align="center" prop="streamMode" />
        <el-table-column label="在线状态" align="center" prop="online" />
        <el-table-column label="注册时间" align="center" prop="registerTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.registerTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后上线时间" align="center" prop="lastConnectTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastConnectTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="激活时间" align="center" prop="activeTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.activeTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备入网IP" align="center" prop="ip" />
        <el-table-column label="设备接入端口号" align="center" prop="port" />
        <el-table-column label="设备地址" align="center" prop="hostAddress" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:sipDevice:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:sipDevice:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改监控设备对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="sipDeviceFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品ID" />
        </el-form-item>
        <el-form-item label="设备SipID" prop="deviceSipId">
          <el-input v-model="form.deviceSipId" placeholder="请输入设备SipID" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="厂商名称" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入厂商名称" />
        </el-form-item>
        <el-form-item label="产品型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入产品型号" />
        </el-form-item>
        <el-form-item label="固件版本" prop="firmware">
          <el-input v-model="form.firmware" placeholder="请输入固件版本" />
        </el-form-item>
        <el-form-item label="传输模式" prop="transport">
          <el-input v-model="form.transport" placeholder="请输入传输模式" />
        </el-form-item>
        <el-form-item label="流模式" prop="streamMode">
          <el-input v-model="form.streamMode" placeholder="请输入流模式" />
        </el-form-item>
        <el-form-item label="在线状态" prop="online">
          <el-input v-model="form.online" placeholder="请输入在线状态" />
        </el-form-item>
        <el-form-item label="注册时间" prop="registerTime">
          <el-date-picker clearable
            v-model="form.registerTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择注册时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="最后上线时间" prop="lastConnectTime">
          <el-date-picker clearable
            v-model="form.lastConnectTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择最后上线时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="激活时间" prop="activeTime">
          <el-date-picker clearable
            v-model="form.activeTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择激活时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="设备入网IP" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入设备入网IP" />
        </el-form-item>
        <el-form-item label="设备接入端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入设备接入端口号" />
        </el-form-item>
        <el-form-item label="设备地址" prop="hostAddress">
          <el-input v-model="form.hostAddress" placeholder="请输入设备地址" />
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

<script setup name="SipDevice" lang="ts">
import { listSipDevice, getSipDevice, delSipDevice, addSipDevice, updateSipDevice } from '@/api/iot/sipDevice';
import { SipDeviceVO, SipDeviceQuery, SipDeviceForm } from '@/api/iot/sipDevice/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const sipDeviceList = ref<SipDeviceVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const sipDeviceFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: SipDeviceForm = {
  id: undefined,
  productId: undefined,
  deviceSipId: undefined,
  deviceName: undefined,
  manufacturer: undefined,
  model: undefined,
  firmware: undefined,
  transport: undefined,
  streamMode: undefined,
  online: undefined,
  registerTime: undefined,
  lastConnectTime: undefined,
  activeTime: undefined,
  ip: undefined,
  port: undefined,
  hostAddress: undefined,
  remark: undefined
}
const data = reactive<PageData<SipDeviceForm, SipDeviceQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productId: undefined,
    deviceSipId: undefined,
    deviceName: undefined,
    manufacturer: undefined,
    model: undefined,
    firmware: undefined,
    transport: undefined,
    streamMode: undefined,
    online: undefined,
    registerTime: undefined,
    lastConnectTime: undefined,
    activeTime: undefined,
    ip: undefined,
    port: undefined,
    hostAddress: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "设备ID不能为空", trigger: "blur" }
    ],
    productId: [
      { required: true, message: "产品ID不能为空", trigger: "blur" }
    ],
    deviceSipId: [
      { required: true, message: "设备SipID不能为空", trigger: "blur" }
    ],
    deviceName: [
      { required: true, message: "设备名称不能为空", trigger: "blur" }
    ],
    manufacturer: [
      { required: true, message: "厂商名称不能为空", trigger: "blur" }
    ],
    model: [
      { required: true, message: "产品型号不能为空", trigger: "blur" }
    ],
    firmware: [
      { required: true, message: "固件版本不能为空", trigger: "blur" }
    ],
    transport: [
      { required: true, message: "传输模式不能为空", trigger: "blur" }
    ],
    streamMode: [
      { required: true, message: "流模式不能为空", trigger: "blur" }
    ],
    online: [
      { required: true, message: "在线状态不能为空", trigger: "blur" }
    ],
    registerTime: [
      { required: true, message: "注册时间不能为空", trigger: "blur" }
    ],
    lastConnectTime: [
      { required: true, message: "最后上线时间不能为空", trigger: "blur" }
    ],
    activeTime: [
      { required: true, message: "激活时间不能为空", trigger: "blur" }
    ],
    ip: [
      { required: true, message: "设备入网IP不能为空", trigger: "blur" }
    ],
    port: [
      { required: true, message: "设备接入端口号不能为空", trigger: "blur" }
    ],
    hostAddress: [
      { required: true, message: "设备地址不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询监控设备列表 */
const getList = async () => {
  loading.value = true;
  const res = await listSipDevice(queryParams.value);
  sipDeviceList.value = res.rows;
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
  sipDeviceFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: SipDeviceVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加监控设备";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: SipDeviceVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getSipDevice(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改监控设备";
}

/** 提交按钮 */
const submitForm = () => {
  sipDeviceFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateSipDevice(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addSipDevice(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: SipDeviceVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除监控设备编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delSipDevice(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/sipDevice/export', {
    ...queryParams.value
  }, `sipDevice_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
