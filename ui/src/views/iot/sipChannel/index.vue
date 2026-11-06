<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="产品ID" prop="productId">
              <el-input v-model="queryParams.productId" placeholder="请输入产品ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备SipID" prop="deviceSipId">
              <el-input v-model="queryParams.deviceSipId" placeholder="请输入设备SipID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="通道SipID" prop="channelSipId">
              <el-input v-model="queryParams.channelSipId" placeholder="请输入通道SipID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="通道名称" prop="channelName">
              <el-input v-model="queryParams.channelName" placeholder="请输入通道名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="注册时间" prop="registerTime">
              <el-date-picker clearable
                v-model="queryParams.registerTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择注册时间"
              />
            </el-form-item>
            <el-form-item label="城市编码" prop="cityCode">
              <el-input v-model="queryParams.cityCode" placeholder="请输入城市编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="行政区域" prop="civilCode">
              <el-input v-model="queryParams.civilCode" placeholder="请输入行政区域" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="厂商名称" prop="manufacture">
              <el-input v-model="queryParams.manufacture" placeholder="请输入厂商名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品型号" prop="model">
              <el-input v-model="queryParams.model" placeholder="请输入产品型号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备归属" prop="owner">
              <el-input v-model="queryParams.owner" placeholder="请输入设备归属" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="警区" prop="block">
              <el-input v-model="queryParams.block" placeholder="请输入警区" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="安装地址" prop="address">
              <el-input v-model="queryParams.address" placeholder="请输入安装地址" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="父级id" prop="parentId">
              <el-input v-model="queryParams.parentId" placeholder="请输入父级id" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备入网IP" prop="ipAddress">
              <el-input v-model="queryParams.ipAddress" placeholder="请输入设备入网IP" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备接入端口号" prop="port">
              <el-input v-model="queryParams.port" placeholder="请输入设备接入端口号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="queryParams.password" placeholder="请输入密码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="PTZ类型描述字符串" prop="PTZTypeText">
              <el-input v-model="queryParams.PTZTypeText" placeholder="请输入PTZ类型描述字符串" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备经度" prop="longitude">
              <el-input v-model="queryParams.longitude" placeholder="请输入设备经度" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备纬度" prop="latitude">
              <el-input v-model="queryParams.latitude" placeholder="请输入设备纬度" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="流媒体ID" prop="streamId">
              <el-input v-model="queryParams.streamId" placeholder="请输入流媒体ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="子设备数" prop="subCount">
              <el-input v-model="queryParams.subCount" placeholder="请输入子设备数" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否有子设备" prop="parental">
              <el-input v-model="queryParams.parental" placeholder="请输入是否有子设备" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否含有音频" prop="hasAudio">
              <el-input v-model="queryParams.hasAudio" placeholder="请输入是否含有音频" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:sipChannel:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:sipChannel:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:sipChannel:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:sipChannel:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="sipChannelList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键" align="center" prop="id" v-if="true" />
        <el-table-column label="产品ID" align="center" prop="productId" />
        <el-table-column label="产品名称" align="center" prop="productName" />
        <el-table-column label="设备SipID" align="center" prop="deviceSipId" />
        <el-table-column label="通道SipID" align="center" prop="channelSipId" />
        <el-table-column label="通道名称" align="center" prop="channelName" />
        <el-table-column label="注册时间" align="center" prop="registerTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.registerTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备类型" align="center" prop="deviceType" />
        <el-table-column label="通道类型" align="center" prop="channelType" />
        <el-table-column label="城市编码" align="center" prop="cityCode" />
        <el-table-column label="行政区域" align="center" prop="civilCode" />
        <el-table-column label="厂商名称" align="center" prop="manufacture" />
        <el-table-column label="产品型号" align="center" prop="model" />
        <el-table-column label="设备归属" align="center" prop="owner" />
        <el-table-column label="警区" align="center" prop="block" />
        <el-table-column label="安装地址" align="center" prop="address" />
        <el-table-column label="父级id" align="center" prop="parentId" />
        <el-table-column label="设备入网IP" align="center" prop="ipAddress" />
        <el-table-column label="设备接入端口号" align="center" prop="port" />
        <el-table-column label="密码" align="center" prop="password" />
        <el-table-column label="PTZ类型" align="center" prop="PTZType" />
        <el-table-column label="PTZ类型描述字符串" align="center" prop="PTZTypeText" />
        <el-table-column label="设备状态" align="center" prop="status" />
        <el-table-column label="设备经度" align="center" prop="longitude" />
        <el-table-column label="设备纬度" align="center" prop="latitude" />
        <el-table-column label="流媒体ID" align="center" prop="streamId" />
        <el-table-column label="子设备数" align="center" prop="subCount" />
        <el-table-column label="是否有子设备" align="center" prop="parental" />
        <el-table-column label="是否含有音频" align="center" prop="hasAudio" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:sipChannel:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:sipChannel:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改监控设备通道信息对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="sipChannelFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入产品ID" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="设备SipID" prop="deviceSipId">
          <el-input v-model="form.deviceSipId" placeholder="请输入设备SipID" />
        </el-form-item>
        <el-form-item label="通道SipID" prop="channelSipId">
          <el-input v-model="form.channelSipId" placeholder="请输入通道SipID" />
        </el-form-item>
        <el-form-item label="通道名称" prop="channelName">
          <el-input v-model="form.channelName" placeholder="请输入通道名称" />
        </el-form-item>
        <el-form-item label="注册时间" prop="registerTime">
          <el-date-picker clearable
            v-model="form.registerTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择注册时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="城市编码" prop="cityCode">
          <el-input v-model="form.cityCode" placeholder="请输入城市编码" />
        </el-form-item>
        <el-form-item label="行政区域" prop="civilCode">
          <el-input v-model="form.civilCode" placeholder="请输入行政区域" />
        </el-form-item>
        <el-form-item label="厂商名称" prop="manufacture">
          <el-input v-model="form.manufacture" placeholder="请输入厂商名称" />
        </el-form-item>
        <el-form-item label="产品型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入产品型号" />
        </el-form-item>
        <el-form-item label="设备归属" prop="owner">
          <el-input v-model="form.owner" placeholder="请输入设备归属" />
        </el-form-item>
        <el-form-item label="警区" prop="block">
          <el-input v-model="form.block" placeholder="请输入警区" />
        </el-form-item>
        <el-form-item label="安装地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入安装地址" />
        </el-form-item>
        <el-form-item label="父级id" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父级id" />
        </el-form-item>
        <el-form-item label="设备入网IP" prop="ipAddress">
          <el-input v-model="form.ipAddress" placeholder="请输入设备入网IP" />
        </el-form-item>
        <el-form-item label="设备接入端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入设备接入端口号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="PTZ类型描述字符串" prop="PTZTypeText">
          <el-input v-model="form.PTZTypeText" placeholder="请输入PTZ类型描述字符串" />
        </el-form-item>
        <el-form-item label="设备经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入设备经度" />
        </el-form-item>
        <el-form-item label="设备纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入设备纬度" />
        </el-form-item>
        <el-form-item label="流媒体ID" prop="streamId">
          <el-input v-model="form.streamId" placeholder="请输入流媒体ID" />
        </el-form-item>
        <el-form-item label="子设备数" prop="subCount">
          <el-input v-model="form.subCount" placeholder="请输入子设备数" />
        </el-form-item>
        <el-form-item label="是否有子设备" prop="parental">
          <el-input v-model="form.parental" placeholder="请输入是否有子设备" />
        </el-form-item>
        <el-form-item label="是否含有音频" prop="hasAudio">
          <el-input v-model="form.hasAudio" placeholder="请输入是否含有音频" />
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

<script setup name="SipChannel" lang="ts">
import { listSipChannel, getSipChannel, delSipChannel, addSipChannel, updateSipChannel } from '@/api/iot/sipChannel';
import { SipChannelVO, SipChannelQuery, SipChannelForm } from '@/api/iot/sipChannel/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const sipChannelList = ref<SipChannelVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const sipChannelFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: SipChannelForm = {
  id: undefined,
  productId: undefined,
  productName: undefined,
  deviceSipId: undefined,
  channelSipId: undefined,
  channelName: undefined,
  registerTime: undefined,
  deviceType: undefined,
  channelType: undefined,
  cityCode: undefined,
  civilCode: undefined,
  manufacture: undefined,
  model: undefined,
  owner: undefined,
  block: undefined,
  address: undefined,
  parentId: undefined,
  ipAddress: undefined,
  port: undefined,
  password: undefined,
  PTZType: undefined,
  PTZTypeText: undefined,
  status: undefined,
  longitude: undefined,
  latitude: undefined,
  streamId: undefined,
  subCount: undefined,
  parental: undefined,
  hasAudio: undefined,
  remark: undefined
}
const data = reactive<PageData<SipChannelForm, SipChannelQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productId: undefined,
    productName: undefined,
    deviceSipId: undefined,
    channelSipId: undefined,
    channelName: undefined,
    registerTime: undefined,
    deviceType: undefined,
    channelType: undefined,
    cityCode: undefined,
    civilCode: undefined,
    manufacture: undefined,
    model: undefined,
    owner: undefined,
    block: undefined,
    address: undefined,
    parentId: undefined,
    ipAddress: undefined,
    port: undefined,
    password: undefined,
    PTZType: undefined,
    PTZTypeText: undefined,
    status: undefined,
    longitude: undefined,
    latitude: undefined,
    streamId: undefined,
    subCount: undefined,
    parental: undefined,
    hasAudio: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "主键不能为空", trigger: "blur" }
    ],
    productId: [
      { required: true, message: "产品ID不能为空", trigger: "blur" }
    ],
    productName: [
      { required: true, message: "产品名称不能为空", trigger: "blur" }
    ],
    deviceSipId: [
      { required: true, message: "设备SipID不能为空", trigger: "blur" }
    ],
    channelSipId: [
      { required: true, message: "通道SipID不能为空", trigger: "blur" }
    ],
    channelName: [
      { required: true, message: "通道名称不能为空", trigger: "blur" }
    ],
    registerTime: [
      { required: true, message: "注册时间不能为空", trigger: "blur" }
    ],
    deviceType: [
      { required: true, message: "设备类型不能为空", trigger: "change" }
    ],
    channelType: [
      { required: true, message: "通道类型不能为空", trigger: "change" }
    ],
    cityCode: [
      { required: true, message: "城市编码不能为空", trigger: "blur" }
    ],
    civilCode: [
      { required: true, message: "行政区域不能为空", trigger: "blur" }
    ],
    manufacture: [
      { required: true, message: "厂商名称不能为空", trigger: "blur" }
    ],
    model: [
      { required: true, message: "产品型号不能为空", trigger: "blur" }
    ],
    owner: [
      { required: true, message: "设备归属不能为空", trigger: "blur" }
    ],
    block: [
      { required: true, message: "警区不能为空", trigger: "blur" }
    ],
    address: [
      { required: true, message: "安装地址不能为空", trigger: "blur" }
    ],
    parentId: [
      { required: true, message: "父级id不能为空", trigger: "blur" }
    ],
    ipAddress: [
      { required: true, message: "设备入网IP不能为空", trigger: "blur" }
    ],
    port: [
      { required: true, message: "设备接入端口号不能为空", trigger: "blur" }
    ],
    password: [
      { required: true, message: "密码不能为空", trigger: "blur" }
    ],
    PTZType: [
      { required: true, message: "PTZ类型不能为空", trigger: "change" }
    ],
    PTZTypeText: [
      { required: true, message: "PTZ类型描述字符串不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "设备状态不能为空", trigger: "change" }
    ],
    longitude: [
      { required: true, message: "设备经度不能为空", trigger: "blur" }
    ],
    latitude: [
      { required: true, message: "设备纬度不能为空", trigger: "blur" }
    ],
    streamId: [
      { required: true, message: "流媒体ID不能为空", trigger: "blur" }
    ],
    subCount: [
      { required: true, message: "子设备数不能为空", trigger: "blur" }
    ],
    parental: [
      { required: true, message: "是否有子设备不能为空", trigger: "blur" }
    ],
    hasAudio: [
      { required: true, message: "是否含有音频不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询监控设备通道信息列表 */
const getList = async () => {
  loading.value = true;
  const res = await listSipChannel(queryParams.value);
  sipChannelList.value = res.rows;
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
  sipChannelFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: SipChannelVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加监控设备通道信息";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: SipChannelVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getSipChannel(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改监控设备通道信息";
}

/** 提交按钮 */
const submitForm = () => {
  sipChannelFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateSipChannel(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addSipChannel(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: SipChannelVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除监控设备通道信息编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delSipChannel(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/sipChannel/export', {
    ...queryParams.value
  }, `sipChannel_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
