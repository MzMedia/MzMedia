<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="用户昵称" prop="userName">
              <el-input v-model="queryParams.userName" placeholder="请输入用户昵称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="设备ID" prop="deviceId">
              <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否为设备所有者" prop="isOwner">
              <el-input v-model="queryParams.isOwner" placeholder="请输入是否为设备所有者" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="用户物模型权限，多个以英文逗号分隔" prop="perms">
              <el-input v-model="queryParams.perms" placeholder="请输入用户物模型权限，多个以英文逗号分隔" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:deviceUser:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:deviceUser:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:deviceUser:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:deviceUser:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="deviceUserList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" v-if="true" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="用户昵称" align="center" prop="userName" />
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="手机号码" align="center" prop="phonenumber" />
        <el-table-column label="是否为设备所有者" align="center" prop="isOwner" />
        <el-table-column label="用户物模型权限，多个以英文逗号分隔" align="center" prop="perms" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:deviceUser:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:deviceUser:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改设备用户对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="deviceUserFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="form.phonenumber" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="是否为设备所有者" prop="isOwner">
          <el-input v-model="form.isOwner" placeholder="请输入是否为设备所有者" />
        </el-form-item>
        <el-form-item label="用户物模型权限，多个以英文逗号分隔" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入用户物模型权限，多个以英文逗号分隔" />
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

<script setup name="DeviceUser" lang="ts">
import { listDeviceUser, getDeviceUser, delDeviceUser, addDeviceUser, updateDeviceUser } from '@/api/iot/deviceUser';
import { DeviceUserVO, DeviceUserQuery, DeviceUserForm } from '@/api/iot/deviceUser/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const deviceUserList = ref<DeviceUserVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const deviceUserFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: DeviceUserForm = {
  id: undefined,
  userId: undefined,
  userName: undefined,
  deviceId: undefined,
  phonenumber: undefined,
  isOwner: undefined,
  perms: undefined,
  remark: undefined
}
const data = reactive<PageData<DeviceUserForm, DeviceUserQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    userName: undefined,
    deviceId: undefined,
    phonenumber: undefined,
    isOwner: undefined,
    perms: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    userName: [
      { required: true, message: "用户昵称不能为空", trigger: "blur" }
    ],
    deviceId: [
      { required: true, message: "设备ID不能为空", trigger: "blur" }
    ],
    phonenumber: [
      { required: true, message: "手机号码不能为空", trigger: "blur" }
    ],
    isOwner: [
      { required: true, message: "是否为设备所有者不能为空", trigger: "blur" }
    ],
    perms: [
      { required: true, message: "用户物模型权限，多个以英文逗号分隔不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备用户列表 */
const getList = async () => {
  loading.value = true;
  const res = await listDeviceUser(queryParams.value);
  deviceUserList.value = res.rows;
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
  deviceUserFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: DeviceUserVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加设备用户";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: DeviceUserVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getDeviceUser(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改设备用户";
}

/** 提交按钮 */
const submitForm = () => {
  deviceUserFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateDeviceUser(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addDeviceUser(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: DeviceUserVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除设备用户编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delDeviceUser(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/deviceUser/export', {
    ...queryParams.value
  }, `deviceUser_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
