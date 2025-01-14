<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="产品分类ID" prop="categoryId">
              <el-input v-model="queryParams.categoryId" placeholder="请输入产品分类ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="协议编号" prop="protocolCode">
              <el-input v-model="queryParams.protocolCode" placeholder="请输入协议编号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否系统通用" prop="isSys">
              <el-input v-model="queryParams.isSys" placeholder="请输入是否系统通用" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="是否启用授权码" prop="isAuthorize">
              <el-input v-model="queryParams.isAuthorize" placeholder="请输入是否启用授权码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="mqtt账号" prop="mqttAccount">
              <el-input v-model="queryParams.mqttAccount" placeholder="请输入mqtt账号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="mqtt密码" prop="mqttPassword">
              <el-input v-model="queryParams.mqttPassword" placeholder="请输入mqtt密码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="产品秘钥" prop="mqttSecret">
              <el-input v-model="queryParams.mqttSecret" placeholder="请输入产品秘钥" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="联网方式" prop="networkMethod">
              <el-input v-model="queryParams.networkMethod" placeholder="请输入联网方式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="认证方式" prop="vertificateMethod">
              <el-input v-model="queryParams.vertificateMethod" placeholder="请输入认证方式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="图片地址" prop="imgUrl">
              <el-input v-model="queryParams.imgUrl" placeholder="请输入图片地址" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:product:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:product:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:product:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:product:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="产品ID" align="center" prop="id" v-if="true" />
        <el-table-column label="产品分类ID" align="center" prop="categoryId" />
        <el-table-column label="产品名称" align="center" prop="productName" />
        <el-table-column label="协议编号" align="center" prop="protocolCode" />
        <el-table-column label="是否系统通用" align="center" prop="isSys" />
        <el-table-column label="是否启用授权码" align="center" prop="isAuthorize" />
        <el-table-column label="mqtt账号" align="center" prop="mqttAccount" />
        <el-table-column label="mqtt密码" align="center" prop="mqttPassword" />
        <el-table-column label="产品秘钥" align="center" prop="mqttSecret" />
        <el-table-column label="状态" align="center" prop="status" />
        <el-table-column label="物模型JSON" align="center" prop="thingsModelsJson" />
        <el-table-column label="设备类型" align="center" prop="deviceType" />
        <el-table-column label="联网方式" align="center" prop="networkMethod" />
        <el-table-column label="认证方式" align="center" prop="vertificateMethod" />
        <el-table-column label="图片地址" align="center" prop="imgUrl" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:product:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:product:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改产品对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="productFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产品分类ID" prop="categoryId">
          <el-input v-model="form.categoryId" placeholder="请输入产品分类ID" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="协议编号" prop="protocolCode">
          <el-input v-model="form.protocolCode" placeholder="请输入协议编号" />
        </el-form-item>
        <el-form-item label="是否系统通用" prop="isSys">
          <el-input v-model="form.isSys" placeholder="请输入是否系统通用" />
        </el-form-item>
        <el-form-item label="是否启用授权码" prop="isAuthorize">
          <el-input v-model="form.isAuthorize" placeholder="请输入是否启用授权码" />
        </el-form-item>
        <el-form-item label="mqtt账号" prop="mqttAccount">
          <el-input v-model="form.mqttAccount" placeholder="请输入mqtt账号" />
        </el-form-item>
        <el-form-item label="mqtt密码" prop="mqttPassword">
          <el-input v-model="form.mqttPassword" placeholder="请输入mqtt密码" />
        </el-form-item>
        <el-form-item label="产品秘钥" prop="mqttSecret">
          <el-input v-model="form.mqttSecret" placeholder="请输入产品秘钥" />
        </el-form-item>
        <el-form-item label="联网方式" prop="networkMethod">
          <el-input v-model="form.networkMethod" placeholder="请输入联网方式" />
        </el-form-item>
        <el-form-item label="认证方式" prop="vertificateMethod">
          <el-input v-model="form.vertificateMethod" placeholder="请输入认证方式" />
        </el-form-item>
        <el-form-item label="图片地址" prop="imgUrl">
          <el-input v-model="form.imgUrl" placeholder="请输入图片地址" />
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

<script setup name="Product" lang="ts">
import { listProduct, getProduct, delProduct, addProduct, updateProduct } from '@/api/iot/product';
import { ProductVO, ProductQuery, ProductForm } from '@/api/iot/product/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const productList = ref<ProductVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const productFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ProductForm = {
  id: undefined,
  categoryId: undefined,
  productName: undefined,
  protocolCode: undefined,
  isSys: undefined,
  isAuthorize: undefined,
  mqttAccount: undefined,
  mqttPassword: undefined,
  mqttSecret: undefined,
  status: undefined,
  thingsModelsJson: undefined,
  deviceType: undefined,
  networkMethod: undefined,
  vertificateMethod: undefined,
  imgUrl: undefined,
  remark: undefined
}
const data = reactive<PageData<ProductForm, ProductQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryId: undefined,
    productName: undefined,
    protocolCode: undefined,
    isSys: undefined,
    isAuthorize: undefined,
    mqttAccount: undefined,
    mqttPassword: undefined,
    mqttSecret: undefined,
    status: undefined,
    thingsModelsJson: undefined,
    deviceType: undefined,
    networkMethod: undefined,
    vertificateMethod: undefined,
    imgUrl: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "产品ID不能为空", trigger: "blur" }
    ],
    categoryId: [
      { required: true, message: "产品分类ID不能为空", trigger: "blur" }
    ],
    productName: [
      { required: true, message: "产品名称不能为空", trigger: "blur" }
    ],
    protocolCode: [
      { required: true, message: "协议编号不能为空", trigger: "blur" }
    ],
    isSys: [
      { required: true, message: "是否系统通用不能为空", trigger: "blur" }
    ],
    isAuthorize: [
      { required: true, message: "是否启用授权码不能为空", trigger: "blur" }
    ],
    mqttAccount: [
      { required: true, message: "mqtt账号不能为空", trigger: "blur" }
    ],
    mqttPassword: [
      { required: true, message: "mqtt密码不能为空", trigger: "blur" }
    ],
    mqttSecret: [
      { required: true, message: "产品秘钥不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
    thingsModelsJson: [
      { required: true, message: "物模型JSON不能为空", trigger: "blur" }
    ],
    deviceType: [
      { required: true, message: "设备类型不能为空", trigger: "change" }
    ],
    networkMethod: [
      { required: true, message: "联网方式不能为空", trigger: "blur" }
    ],
    vertificateMethod: [
      { required: true, message: "认证方式不能为空", trigger: "blur" }
    ],
    imgUrl: [
      { required: true, message: "图片地址不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询产品列表 */
const getList = async () => {
  loading.value = true;
  const res = await listProduct(queryParams.value);
  productList.value = res.rows;
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
  productFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: ProductVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加产品";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ProductVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getProduct(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改产品";
}

/** 提交按钮 */
const submitForm = () => {
  productFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateProduct(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addProduct(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ProductVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除产品编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delProduct(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/product/export', {
    ...queryParams.value
  }, `product_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
