package com.store.service;

import com.store.vo.AccountsSummaryVo;

public interface AccountsService {
  AccountsSummaryVo summary(String beginDate, String endDate);
}
