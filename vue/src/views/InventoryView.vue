<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="条码/商品名" class="search-input" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>

      <el-table :data="products" style="width: 100%">
        <el-table-column prop="barcode" label="条码" width="160" />
        <el-table-column prop="name" label="商品名" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="costPrice" label="进价" width="100">
          <template #default="scope">¥ {{ scope.row.costPrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="salePrice" label="售价" width="100">
          <template #default="scope">¥ {{ scope.row.salePrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">{{ scope.row.status === 1 ? "启用" : "禁用" }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" @click="openPrice(scope.row)">修改售价</el-button>
            <el-button size="small" @click="openStock(scope.row)">库存调整</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="priceDialogVisible" title="修改售价" width="360px">
      <el-form :model="priceForm" label-width="80px">
        <el-form-item label="售价">
          <el-input-number v-model="priceForm.salePrice" :min="0.01" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="priceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPrice">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="stockDialogVisible" title="库存调整" width="360px">
      <el-form :model="stockForm" label-width="80px">
        <el-form-item label="调整数量">
          <el-input-number v-model="stockForm.changeStock" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="stockForm.reason" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStock">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";

import {
  adjustProductStock,
  fetchProducts,
  Product,
  updateProductPrice
} from "../api/product";

const keyword = ref("");
const page = ref(1);
const size = ref(10);
const total = ref(0);
const products = ref<Product[]>([]);

const priceDialogVisible = ref(false);
const stockDialogVisible = ref(false);
const currentProduct = ref<Product | null>(null);

const priceForm = reactive({
  salePrice: 0
});

const stockForm = reactive({
  changeStock: 0,
  reason: ""
});

const load = async () => {
  const { data } = await fetchProducts(page.value, size.value, keyword.value);
  products.value = data.data.records;
  total.value = data.data.total;
};

const openPrice = (product: Product) => {
  currentProduct.value = product;
  priceForm.salePrice = product.salePrice;
  priceDialogVisible.value = true;
};

const openStock = (product: Product) => {
  currentProduct.value = product;
  stockForm.changeStock = 0;
  stockForm.reason = "";
  stockDialogVisible.value = true;
};

const submitPrice = async () => {
  if (!currentProduct.value) return;
  try {
    await updateProductPrice(currentProduct.value.id, priceForm.salePrice);
    ElMessage.success("售价已更新");
    priceDialogVisible.value = false;
    load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "售价更新失败");
  }
};

const submitStock = async () => {
  if (!currentProduct.value) return;
  if (!stockForm.changeStock) {
    ElMessage.warning("请输入调整数量");
    return;
  }
  if (!stockForm.reason.trim()) {
    ElMessage.warning("请输入调整原因");
    return;
  }
  try {
    await adjustProductStock(currentProduct.value.id, stockForm.changeStock, stockForm.reason);
    ElMessage.success("库存已调整");
    stockDialogVisible.value = false;
    load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "库存调整失败");
  }
};

onMounted(load);
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.search-input {
  max-width: 260px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
