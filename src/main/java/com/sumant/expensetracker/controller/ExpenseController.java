package com.sumant.expensetracker.controller;

import com.sumant.expensetracker.dto.*;
import com.sumant.expensetracker.service.ExpenseService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // TEMPORARY: userId comes from a request param until JWT is added

    @PostMapping
    public ResponseEntity<ExpenseResponse> add(@RequestParam Integer userId,
                                               @Valid @RequestBody ExpenseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.addExpense(req, userId));
    }

    @GetMapping
    public List<ExpenseResponse> viewAll(@RequestParam Integer userId) {
        return expenseService.viewExpenses(userId);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Integer id,
                                  @RequestParam Integer userId,
                                  @Valid @RequestBody ExpenseRequest req) {
        return expenseService.updateExpense(id, req, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @RequestParam Integer userId) {
        expenseService.deleteExpense(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ExpenseResponse> search(@RequestParam Integer userId,
                                        @RequestParam String keyword) {
        return expenseService.searchExpense(keyword, userId);
    }

    @GetMapping("/summary/total")
    public Map<String, BigDecimal> total(@RequestParam Integer userId) {
        return Map.of("totalSpending", expenseService.totalSpending(userId));
    }

    @GetMapping("/summary/category")
    public List<CategoryTotalResponse> byCategory(@RequestParam Integer userId) {
        return expenseService.categoryWiseSpending(userId);
    }

    @GetMapping("/summary/budget")
    public BudgetStatusResponse budget(@RequestParam Integer userId,
                                       @RequestParam BigDecimal budget) {
        return expenseService.budgetStatus(budget, userId);
    }

    @GetMapping("/summary/dashboard")
    public DashboardSummaryResponse dashboard(@RequestParam Integer userId) {
        return expenseService.dashboardSummary(userId);
    }

    @GetMapping("/report/monthly")
    public List<ExpenseResponse> monthly(@RequestParam Integer userId,
                                         @RequestParam int year,
                                         @RequestParam int month) {
        return expenseService.monthlyReport(year, month, userId);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam Integer userId) {
        byte[] csv = expenseService.exportCsv(userId).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"expenses_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
