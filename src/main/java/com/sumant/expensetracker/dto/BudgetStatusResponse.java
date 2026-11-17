package com.sumant.expensetracker.dto;

import java.math.BigDecimal;

public record BudgetStatusResponse(BigDecimal budget, BigDecimal spent, BigDecimal remaining,
                                   double percentageUsed, boolean exceeded) {}
