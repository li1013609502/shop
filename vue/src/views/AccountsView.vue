<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <div class="summary">
        <div>区间销售额：¥ {{ summary.salesAmount.toFixed(2) }}</div>
        <div>区间利润：¥ {{ summary.profitAmount.toFixed(2) }}</div>
      </div>
    </el-card>

    <el-card>
      <el-table :data="records">
        <el-table-column prop="createdAt" label="时间" width="180" />
        <el-table-column prop="outNo" label="单号" width="160" />
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="operatorName" label="操作员" width="120" />
        <el-table-column prop="qty" label="数量" width="80" />
        <el-table-column prop="salePrice" label="售价" width="100">
          <template #default="scope">¥ {{ scope.row.salePrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本" width="100">
          <template #default="scope">¥ {{ scope.row.costPrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="profit" label="利润" width="100">
          <template #default="scope">¥ {{ scope.row.profit.toFixed(2) }}</template>
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";

import { fetchAccountsSummary } from "../api/dashboard";
import { fetchStockOutItems } from "../api/stock";

const dateRange = ref<[string, string] | null>(null);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const records = ref<any[]>([]);

const summary = reactive({
  salesAmount: 0,
  profitAmount: 0
});

const load = async () => {
  const beginDate = dateRange.value?.[0] || "";
  const endDate = dateRange.value?.[1] || "";
  const summaryRes = await fetchAccountsSummary(beginDate, endDate);
  summary.salesAmount = summaryRes.data.data.salesAmount || 0;
  summary.profitAmount = summaryRes.data.data.profitAmount || 0;
  const listRes = await fetchStockOutItems(page.value, size.value, beginDate, endDate);
  records.value = listRes.data.data.records || [];
  total.value = listRes.data.data.total || 0;
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
  align-items: center;
}

.summary {
  margin-top: 12px;
  display: flex;
  gap: 24px;
  font-weight: 600;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
