<template>
  <div class="page">
    <el-card>
      <div class="scan-row">
        <el-input
          v-model="barcode"
          placeholder="扫码条码后回车"
          @keyup.enter="handleScan"
          class="scan-input"
        />
        <el-button type="primary" @click="handleScan">查找</el-button>
      </div>

      <div v-if="product" class="product-info">
        <div>商品：{{ product.name }}</div>
        <div>条码：{{ product.barcode }}</div>
        <div>当前库存：{{ product.stock }}</div>
      </div>

      <el-form v-if="product" :model="stockInForm" label-width="80px" class="stock-form">
        <el-form-item label="数量">
          <el-input-number v-model="stockInForm.qty" :min="1" />
        </el-form-item>
        <el-form-item label="进价">
          <el-input-number v-model="stockInForm.costPrice" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="售价">
          <el-input-number v-model="stockInForm.salePrice" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitStockIn">确认入库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新增商品" width="480px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="条码">
          <el-input v-model="createForm.barcode" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="createForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="createForm.category" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="createForm.qty" :min="1" />
        </el-form-item>
        <el-form-item label="进价">
          <el-input-number v-model="createForm.costPrice" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="售价">
          <el-input-number v-model="createForm.salePrice" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="createForm.status" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">保存并入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";

import { fetchProductByBarcode, Product } from "../api/product";
import { createProductAndStockIn, submitStockIn } from "../api/stock";

const barcode = ref("");
const product = ref<Product | null>(null);

const stockInForm = reactive({
  qty: 1,
  costPrice: 0,
  salePrice: 0
});

const createDialogVisible = ref(false);
const createForm = reactive({
  barcode: "",
  name: "",
  category: "",
  qty: 1,
  costPrice: 0,
  salePrice: 0,
  status: 1
});

const handleScan = async () => {
  if (!barcode.value.trim()) return;
  try {
    const { data } = await fetchProductByBarcode(barcode.value.trim());
    product.value = data.data;
    stockInForm.qty = 1;
    stockInForm.costPrice = data.data.costPrice;
    stockInForm.salePrice = data.data.salePrice;
  } catch (error) {
    createForm.barcode = barcode.value.trim();
    createForm.name = "";
    createForm.category = "";
    createForm.qty = 1;
    createForm.costPrice = 0;
    createForm.salePrice = 0;
    createForm.status = 1;
    createDialogVisible.value = true;
  }
};

const submitStockIn = async () => {
  if (!product.value) return;
  if (!validateStockForm(stockInForm.qty, stockInForm.costPrice, stockInForm.salePrice)) {
    return;
  }
  try {
    await submitStockIn([
      {
        barcode: product.value.barcode,
        qty: stockInForm.qty,
        costPrice: stockInForm.costPrice,
        salePrice: stockInForm.salePrice
      }
    ]);
    ElMessage.success("入库成功");
    barcode.value = "";
    product.value = null;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "入库失败");
  }
};

const submitCreate = async () => {
  if (!createForm.barcode || !createForm.name || !createForm.category) {
    ElMessage.warning("请填写条码、名称与分类");
    return;
  }
  if (!validateStockForm(createForm.qty, createForm.costPrice, createForm.salePrice)) {
    return;
  }
  try {
    await createProductAndStockIn({
      barcode: createForm.barcode,
      name: createForm.name,
      category: createForm.category,
      qty: createForm.qty,
      costPrice: createForm.costPrice,
      salePrice: createForm.salePrice,
      status: createForm.status
    });
    createDialogVisible.value = false;
    ElMessage.success("入库成功");
    barcode.value = "";
    product.value = null;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "入库失败");
  }
};

const validateStockForm = (qty: number, costPrice: number, salePrice: number) => {
  if (!qty || qty < 1) {
    ElMessage.warning("入库数量必须大于 0");
    return false;
  }
  if (!costPrice || costPrice <= 0) {
    ElMessage.warning("进价必须大于 0");
    return false;
  }
  if (!salePrice || salePrice <= 0) {
    ElMessage.warning("售价必须大于 0");
    return false;
  }
  return true;
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.scan-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.scan-input {
  max-width: 260px;
}

.product-info {
  margin-top: 12px;
  display: flex;
  gap: 24px;
}

.stock-form {
  margin-top: 16px;
  max-width: 400px;
}
</style>
