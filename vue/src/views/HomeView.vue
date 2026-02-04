<template>
  <div class="home">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <div class="card-title">今日营业额</div>
          <div class="card-value">¥ {{ revenue.todayRevenue.toFixed(2) }}</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div class="card-title">当月营业额</div>
          <div class="card-value">¥ {{ revenue.monthRevenue.toFixed(2) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="scan-card" v-if="hasStockOut">
      <template #header>
        <div class="scan-header">
          <span>扫码出库</span>
          <el-input
            ref="scanInputRef"
            v-model="barcodeInput"
            placeholder="扫码条码后回车"
            @keyup.enter="handleScan"
            class="scan-input"
          />
        </div>
      </template>

      <el-table :data="cartItems" :row-class-name="rowClassName" style="width: 100%">
        <el-table-column prop="name" label="商品" />
        <el-table-column prop="barcode" label="条码" width="160" />
        <el-table-column prop="salePrice" label="售价" width="100">
          <template #default="scope">¥ {{ scope.row.salePrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="数量" width="160">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.qty"
              :min="1"
              :max="9999"
              @change="updateQty(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="scope">¥ {{ (scope.row.qty * scope.row.salePrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="text" @click="removeItem(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="summary">
        <div class="summary-info">
          <span>总件数：{{ totalQty }}</span>
          <span>总价：¥ {{ totalAmount.toFixed(2) }}</span>
        </div>
        <div class="summary-actions">
          <el-button @click="clearCart">清空列表</el-button>
          <el-button type="primary" :disabled="cartItems.length === 0 || hasStockError" @click="confirmStockOut">
            确认出库
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";

import { fetchRevenue } from "../api/dashboard";
import { fetchProductByBarcode } from "../api/product";
import { submitStockOut } from "../api/stock";
import { useAuthStore } from "../store/auth";

interface CartItem {
  id: number;
  barcode: string;
  name: string;
  salePrice: number;
  stock: number;
  qty: number;
}

const authStore = useAuthStore();
const hasStockOut = computed(() => authStore.permissions.includes("STOCK_OUT"));

const revenue = reactive({
  todayRevenue: 0,
  monthRevenue: 0
});

const barcodeInput = ref("");
const scanInputRef = ref<{ focus: () => void } | null>(null);
const cartItems = ref<CartItem[]>([]);

const totalAmount = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.qty * item.salePrice, 0)
);
const totalQty = computed(() => cartItems.value.reduce((sum, item) => sum + item.qty, 0));
const hasStockError = computed(() => cartItems.value.some((item) => item.qty > item.stock));

const loadRevenue = async () => {
  const { data } = await fetchRevenue();
  revenue.todayRevenue = data.data.todayRevenue || 0;
  revenue.monthRevenue = data.data.monthRevenue || 0;
};

const handleScan = async () => {
  const barcode = barcodeInput.value.trim();
  if (!barcode) {
    return;
  }
  try {
    const { data } = await fetchProductByBarcode(barcode);
    const product = data.data;
    const existing = cartItems.value.find((item) => item.barcode === product.barcode);
    if (existing) {
      existing.qty += 1;
    } else {
      cartItems.value.push({
        id: product.id,
        barcode: product.barcode,
        name: product.name,
        salePrice: product.salePrice,
        stock: product.stock,
        qty: 1
      });
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "商品不存在，请先入库/新增");
  } finally {
    barcodeInput.value = "";
    focusScan();
  }
};

const updateQty = (item: CartItem) => {
  if (item.qty < 1) {
    item.qty = 1;
  }
};

const removeItem = (index: number) => {
  cartItems.value.splice(index, 1);
};

const clearCart = () => {
  cartItems.value = [];
};

const confirmStockOut = async () => {
  try {
    const items = cartItems.value.map((item) => ({
      barcode: item.barcode,
      qty: item.qty
    }));
    const { data } = await submitStockOut(items);
    ElMessage.success(`出库成功，单号：${data.data.outNo}`);
    clearCart();
    await loadRevenue();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || "出库失败");
  }
};

const rowClassName = ({ row }: { row: CartItem }) => (row.qty > row.stock ? "row-error" : "");

const focusScan = () => {
  scanInputRef.value?.focus();
};

const handleClick = () => {
  const active = document.activeElement as HTMLElement | null;
  if (active && active.closest(".el-input-number")) {
    return;
  }
  focusScan();
};

onMounted(() => {
  loadRevenue();
  window.addEventListener("click", handleClick);
  setTimeout(() => focusScan(), 300);
});

onBeforeUnmount(() => {
  window.removeEventListener("click", handleClick);
});
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-title {
  font-size: 14px;
  color: #888;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  margin-top: 8px;
}

.scan-card {
  min-height: 360px;
}

.scan-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.scan-input {
  max-width: 260px;
}

.summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
}

.summary-info {
  display: flex;
  gap: 24px;
  font-weight: 600;
}

.row-error {
  background-color: #fff1f0;
}
</style>
