package com.store.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.Result;
import com.store.service.AccountsService;
import com.store.vo.AccountsSummaryVo;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {
  private final AccountsService accountsService;

  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @GetMapping("/summary")
  @PreAuthorize("hasAuthority('ACCOUNT_VIEW')")
  public Result<AccountsSummaryVo> summary(
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate) {
    String safeBegin = (beginDate == null || beginDate.trim().isEmpty()) ? null : beginDate;
    String safeEnd = (endDate == null || endDate.trim().isEmpty()) ? null : endDate;
    return Result.ok(accountsService.summary(safeBegin, safeEnd));
  }
}
