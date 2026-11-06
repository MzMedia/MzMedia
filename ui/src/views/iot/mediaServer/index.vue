<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="服务器标识" prop="serverId">
              <el-input v-model="queryParams.serverId" placeholder="请输入服务器标识" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="使能开关" prop="enabled">
              <el-input v-model="queryParams.enabled" placeholder="请输入使能开关" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="默认播放协议" prop="protocol">
              <el-input v-model="queryParams.protocol" placeholder="请输入默认播放协议" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="服务器ip" prop="ip">
              <el-input v-model="queryParams.ip" placeholder="请输入服务器ip" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="服务器域名" prop="domain">
              <el-input v-model="queryParams.domain" placeholder="请输入服务器域名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="回调服务器地址" prop="hookurl">
              <el-input v-model="queryParams.hookurl" placeholder="请输入回调服务器地址" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="流媒体密钥" prop="secret">
              <el-input v-model="queryParams.secret" placeholder="请输入流媒体密钥" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="http端口" prop="portHttp">
              <el-input v-model="queryParams.portHttp" placeholder="请输入http端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="https端口" prop="portHttps">
              <el-input v-model="queryParams.portHttps" placeholder="请输入https端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="rtmp端口" prop="portRtmp">
              <el-input v-model="queryParams.portRtmp" placeholder="请输入rtmp端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="rtsp端口" prop="portRtsp">
              <el-input v-model="queryParams.portRtsp" placeholder="请输入rtsp端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="RTP收流端口" prop="rtpProxyPort">
              <el-input v-model="queryParams.rtpProxyPort" placeholder="请输入RTP收流端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="ws端口" prop="portWs">
              <el-input v-model="queryParams.portWs" placeholder="请输入ws端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否使用多端口模式" prop="rtpEnable">
              <el-input v-model="queryParams.rtpEnable" placeholder="请输入是否使用多端口模式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="rtp端口范围" prop="rtpPortRange">
              <el-input v-model="queryParams.rtpPortRange" placeholder="请输入rtp端口范围" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="录像服务端口" prop="recordPort">
              <el-input v-model="queryParams.recordPort" placeholder="请输入录像服务端口" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否自动同步配置ZLM" prop="autoConfig">
              <el-input v-model="queryParams.autoConfig" placeholder="请输入是否自动同步配置ZLM" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:mediaServer:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:mediaServer:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:mediaServer:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:mediaServer:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="mediaServerList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="流媒体配置ID" align="center" prop="id" v-if="true" />
        <el-table-column label="服务器标识" align="center" prop="serverId" />
        <el-table-column label="使能开关" align="center" prop="enabled" />
        <el-table-column label="默认播放协议" align="center" prop="protocol" />
        <el-table-column label="服务器ip" align="center" prop="ip" />
        <el-table-column label="服务器域名" align="center" prop="domain" />
        <el-table-column label="回调服务器地址" align="center" prop="hookurl" />
        <el-table-column label="流媒体密钥" align="center" prop="secret" />
        <el-table-column label="http端口" align="center" prop="portHttp" />
        <el-table-column label="https端口" align="center" prop="portHttps" />
        <el-table-column label="rtmp端口" align="center" prop="portRtmp" />
        <el-table-column label="rtsp端口" align="center" prop="portRtsp" />
        <el-table-column label="RTP收流端口" align="center" prop="rtpProxyPort" />
        <el-table-column label="ws端口" align="center" prop="portWs" />
        <el-table-column label="是否使用多端口模式" align="center" prop="rtpEnable" />
        <el-table-column label="rtp端口范围" align="center" prop="rtpPortRange" />
        <el-table-column label="录像服务端口" align="center" prop="recordPort" />
        <el-table-column label="是否自动同步配置ZLM" align="center" prop="autoConfig" />
        <el-table-column label="状态" align="center" prop="status" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:mediaServer:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:mediaServer:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改流媒体服务器配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="mediaServerFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务器标识" prop="serverId">
          <el-input v-model="form.serverId" placeholder="请输入服务器标识" />
        </el-form-item>
        <el-form-item label="使能开关" prop="enabled">
          <el-input v-model="form.enabled" placeholder="请输入使能开关" />
        </el-form-item>
        <el-form-item label="默认播放协议" prop="protocol">
          <el-input v-model="form.protocol" placeholder="请输入默认播放协议" />
        </el-form-item>
        <el-form-item label="服务器ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入服务器ip" />
        </el-form-item>
        <el-form-item label="服务器域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入服务器域名" />
        </el-form-item>
        <el-form-item label="回调服务器地址" prop="hookurl">
          <el-input v-model="form.hookurl" placeholder="请输入回调服务器地址" />
        </el-form-item>
        <el-form-item label="流媒体密钥" prop="secret">
          <el-input v-model="form.secret" placeholder="请输入流媒体密钥" />
        </el-form-item>
        <el-form-item label="http端口" prop="portHttp">
          <el-input v-model="form.portHttp" placeholder="请输入http端口" />
        </el-form-item>
        <el-form-item label="https端口" prop="portHttps">
          <el-input v-model="form.portHttps" placeholder="请输入https端口" />
        </el-form-item>
        <el-form-item label="rtmp端口" prop="portRtmp">
          <el-input v-model="form.portRtmp" placeholder="请输入rtmp端口" />
        </el-form-item>
        <el-form-item label="rtsp端口" prop="portRtsp">
          <el-input v-model="form.portRtsp" placeholder="请输入rtsp端口" />
        </el-form-item>
        <el-form-item label="RTP收流端口" prop="rtpProxyPort">
          <el-input v-model="form.rtpProxyPort" placeholder="请输入RTP收流端口" />
        </el-form-item>
        <el-form-item label="ws端口" prop="portWs">
          <el-input v-model="form.portWs" placeholder="请输入ws端口" />
        </el-form-item>
        <el-form-item label="是否使用多端口模式" prop="rtpEnable">
          <el-input v-model="form.rtpEnable" placeholder="请输入是否使用多端口模式" />
        </el-form-item>
        <el-form-item label="rtp端口范围" prop="rtpPortRange">
          <el-input v-model="form.rtpPortRange" placeholder="请输入rtp端口范围" />
        </el-form-item>
        <el-form-item label="录像服务端口" prop="recordPort">
          <el-input v-model="form.recordPort" placeholder="请输入录像服务端口" />
        </el-form-item>
        <el-form-item label="是否自动同步配置ZLM" prop="autoConfig">
          <el-input v-model="form.autoConfig" placeholder="请输入是否自动同步配置ZLM" />
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

<script setup name="MediaServer" lang="ts">
import { listMediaServer, getMediaServer, delMediaServer, addMediaServer, updateMediaServer } from '@/api/iot/mediaServer';
import { MediaServerVO, MediaServerQuery, MediaServerForm } from '@/api/iot/mediaServer/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const mediaServerList = ref<MediaServerVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const mediaServerFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MediaServerForm = {
  id: undefined,
  serverId: undefined,
  enabled: undefined,
  protocol: undefined,
  ip: undefined,
  domain: undefined,
  hookurl: undefined,
  secret: undefined,
  portHttp: undefined,
  portHttps: undefined,
  portRtmp: undefined,
  portRtsp: undefined,
  rtpProxyPort: undefined,
  portWs: undefined,
  rtpEnable: undefined,
  rtpPortRange: undefined,
  recordPort: undefined,
  autoConfig: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<MediaServerForm, MediaServerQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    serverId: undefined,
    enabled: undefined,
    protocol: undefined,
    ip: undefined,
    domain: undefined,
    hookurl: undefined,
    secret: undefined,
    portHttp: undefined,
    portHttps: undefined,
    portRtmp: undefined,
    portRtsp: undefined,
    rtpProxyPort: undefined,
    portWs: undefined,
    rtpEnable: undefined,
    rtpPortRange: undefined,
    recordPort: undefined,
    autoConfig: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "流媒体配置ID不能为空", trigger: "blur" }
    ],
    serverId: [
      { required: true, message: "服务器标识不能为空", trigger: "blur" }
    ],
    enabled: [
      { required: true, message: "使能开关不能为空", trigger: "blur" }
    ],
    protocol: [
      { required: true, message: "默认播放协议不能为空", trigger: "blur" }
    ],
    ip: [
      { required: true, message: "服务器ip不能为空", trigger: "blur" }
    ],
    domain: [
      { required: true, message: "服务器域名不能为空", trigger: "blur" }
    ],
    hookurl: [
      { required: true, message: "回调服务器地址不能为空", trigger: "blur" }
    ],
    secret: [
      { required: true, message: "流媒体密钥不能为空", trigger: "blur" }
    ],
    portHttp: [
      { required: true, message: "http端口不能为空", trigger: "blur" }
    ],
    portHttps: [
      { required: true, message: "https端口不能为空", trigger: "blur" }
    ],
    portRtmp: [
      { required: true, message: "rtmp端口不能为空", trigger: "blur" }
    ],
    portRtsp: [
      { required: true, message: "rtsp端口不能为空", trigger: "blur" }
    ],
    rtpProxyPort: [
      { required: true, message: "RTP收流端口不能为空", trigger: "blur" }
    ],
    portWs: [
      { required: true, message: "ws端口不能为空", trigger: "blur" }
    ],
    rtpEnable: [
      { required: true, message: "是否使用多端口模式不能为空", trigger: "blur" }
    ],
    rtpPortRange: [
      { required: true, message: "rtp端口范围不能为空", trigger: "blur" }
    ],
    recordPort: [
      { required: true, message: "录像服务端口不能为空", trigger: "blur" }
    ],
    autoConfig: [
      { required: true, message: "是否自动同步配置ZLM不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询流媒体服务器配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMediaServer(queryParams.value);
  mediaServerList.value = res.rows;
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
  mediaServerFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MediaServerVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加流媒体服务器配置";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MediaServerVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMediaServer(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改流媒体服务器配置";
}

/** 提交按钮 */
const submitForm = () => {
  mediaServerFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMediaServer(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMediaServer(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MediaServerVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除流媒体服务器配置编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMediaServer(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/mediaServer/export', {
    ...queryParams.value
  }, `mediaServer_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
