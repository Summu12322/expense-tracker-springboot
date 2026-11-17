package com.sumant.expensetracker.dto;

import java.math.BigDecimal;

public record CategoryTotalResponse(String category, BigDecimal total) {}
