<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="协议编解码名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入协议编解码名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="协议编解码描述" prop="describe">
              <el-input v-model="queryParams.describe" placeholder="请输入协议编解码描述" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="脚本内容" prop="script">
              <el-input v-model="queryParams.script" placeholder="请输入脚本内容" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="脚本语言" prop="scriptLang">
              <el-input v-model="queryParams.scriptLang" placeholder="请输入脚本语言" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:codec:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['iot:codec:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['iot:codec:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['iot:codec:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="codecList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" v-if="true" />
        <el-table-column label="协议编解码名称" align="center" prop="name" />
        <el-table-column label="协议编解码描述" align="center" prop="describe" />
        <el-table-column label="编解码类型" align="center" prop="codecType" />
        <el-table-column label="脚本内容" align="center" prop="script" />
        <el-table-column label="脚本语言" align="center" prop="scriptLang" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:codec:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:codec:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改协议编解码器对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="codecFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="协议编解码名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入协议编解码名称" />
        </el-form-item>
        <el-form-item label="协议编解码描述" prop="describe">
          <el-input v-model="form.describe" placeholder="请输入协议编解码描述" />
        </el-form-item>
        <el-form-item label="脚本内容" prop="script">
          <el-input v-model="form.script" placeholder="请输入脚本内容" />
        </el-form-item>
        <el-form-item label="脚本语言" prop="scriptLang">
          <el-input v-model="form.scriptLang" placeholder="请输入脚本语言" />
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

<script setup name="Codec" lang="ts">
import { listCodec, getCodec, delCodec, addCodec, updateCodec } from '@/api/iot/codec';
import { CodecVO, CodecQuery, CodecForm } from '@/api/iot/codec/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const codecList = ref<CodecVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const codecFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CodecForm = {
  id: undefined,
  name: undefined,
  describe: undefined,
  codecType: undefined,
  script: undefined,
  scriptLang: undefined,
  remark: undefined
}
const data = reactive<PageData<CodecForm, CodecQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    describe: undefined,
    codecType: undefined,
    script: undefined,
    scriptLang: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "ID不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "协议编解码名称不能为空", trigger: "blur" }
    ],
    describe: [
      { required: true, message: "协议编解码描述不能为空", trigger: "blur" }
    ],
    codecType: [
      { required: true, message: "编解码类型不能为空", trigger: "change" }
    ],
    script: [
      { required: true, message: "脚本内容不能为空", trigger: "blur" }
    ],
    scriptLang: [
      { required: true, message: "脚本语言不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询协议编解码器列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCodec(queryParams.value);
  codecList.value = res.rows;
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
  codecFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: CodecVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加协议编解码器";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: CodecVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getCodec(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改协议编解码器";
}

/** 提交按钮 */
const submitForm = () => {
  codecFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateCodec(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCodec(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CodecVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除协议编解码器编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCodec(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('iot/codec/export', {
    ...queryParams.value
  }, `codec_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
