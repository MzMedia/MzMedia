<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="产品分类名称" prop="categoryName">
            <el-input v-model="queryParams.categoryName" placeholder="请输入产品分类名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否系统通用" prop="isSys">
            <el-input v-model="queryParams.isSys" placeholder="请输入是否系统通用" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="父级ID" prop="parentId">
            <el-input v-model="queryParams.parentId" placeholder="请输入父级ID" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="显示顺序" prop="orderNum">
            <el-input v-model="queryParams.orderNum" placeholder="请输入显示顺序" clearable style="width: 240px" @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:category:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:category:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:category:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:category:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="产品分类ID" align="center" prop="categoryId" v-if="true" />
        <el-table-column label="产品分类名称" align="center" prop="categoryName" />
        <el-table-column label="是否系统通用" align="center" prop="isSys" />
        <el-table-column label="父级ID" align="center" prop="parentId" />
        <el-table-column label="显示顺序" align="center" prop="orderNum" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:category:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:category:remove']"></el-button>
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
    <!-- 添加或修改产品分类对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="categoryFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产品分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入产品分类名称" />
        </el-form-item>
        <el-form-item label="是否系统通用" prop="isSys">
          <el-input v-model="form.isSys" placeholder="请输入是否系统通用" />
        </el-form-item>
        <el-form-item label="父级ID" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父级ID" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input v-model="form.orderNum" placeholder="请输入显示顺序" />
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

<script setup name="Category" lang="ts">
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from '@/api/iot/category';
import { CategoryVO, CategoryQuery, CategoryForm } from '@/api/iot/category/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const categoryList = ref<CategoryVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const categoryFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CategoryForm = {
  categoryId: undefined,
  categoryName: undefined,
  isSys: undefined,
  parentId: undefined,
  orderNum: undefined,
  remark: undefined
}
const data = reactive<PageData<CategoryForm, CategoryQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryName: undefined,
    isSys: undefined,
    parentId: undefined,
    orderNum: undefined,
    params: {
    }
  },
  rules: {
    categoryId: [
      { required: true, message: "产品分类ID不能为空", trigger: "blur" }
    ],
    categoryName: [
      { required: true, message: "产品分类名称不能为空", trigger: "blur" }
    ],
    isSys: [
      { required: true, message: "是否系统通用不能为空", trigger: "blur" }
    ],
    parentId: [
      { required: true, message: "父级ID不能为空", trigger: "blur" }
    ],
    orderNum: [
      { required: true, message: "显示顺序不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询产品分类列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCategory(queryParams.value);
  categoryList.value = res.rows;
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
  categoryFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: CategoryVO[]) => {
  ids.value = selection.map(item => item.categoryId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加产品分类";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: CategoryVO) => {
  reset();
  const _categoryId = row?.categoryId || ids.value[0]
  const res = await getCategory(_categoryId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改产品分类";
}

/** 提交按钮 */
const submitForm = () => {
  categoryFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.categoryId) {
        await updateCategory(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCategory(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CategoryVO) => {
  const _categoryIds = row?.categoryId || ids.value;
  await proxy?.$modal.confirm('是否确认删除产品分类编号为"' + _categoryIds + '"的数据项？').finally(() => loading.value = false);
  await delCategory(_categoryIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/category/export', {
    ...queryParams.value
  }, `category_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
