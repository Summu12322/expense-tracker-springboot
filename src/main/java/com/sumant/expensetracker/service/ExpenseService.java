package com.sumant.expensetracker.service;

import com.sumant.expensetracker.dto.*;
import com.sumant.expensetracker.entity.Expense;
import com.sumant.expensetracker.entity.User;
import com.sumant.expensetracker.exception.ResourceNotFoundException;
import com.sumant.expensetracker.repository.ExpenseRepository;
import com.sumant.expensetracker.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest req, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Expense e = new Expense();
        apply(e, req);
        e.setUser(user);
        return ExpenseResponse.from(expenseRepository.save(e));
    }

    public List<ExpenseResponse> viewExpenses(Integer userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId)
                .stream().map(ExpenseResponse::from).toList();
    }

    @Transactional
    public ExpenseResponse updateExpense(Integer id, ExpenseRequest req, Integer userId) {
        Expense e = findOwned(id, userId);
        apply(e, req);
        return ExpenseResponse.from(e);
    }

    @Transactional
    public void deleteExpense(Integer id, Integer userId) {
        expenseRepository.delete(findOwned(id, userId));
    }

    public List<ExpenseResponse> searchExpense(String keyword, Integer userId) {
        if (keyword == null || keyword.isBlank())
            throw new IllegalArgumentException("Search cannot be empty");
        return expenseRepository.search(userId, keyword.trim())
                .stream().map(ExpenseResponse::from).toList();
    }

    public BigDecimal totalSpending(Integer userId) {
        return expenseRepository.getTotalSpending(userId);
    }

    public List<CategoryTotalResponse> categoryWiseSpending(Integer userId) {
        return expenseRepository.getCategoryWiseSpending(userId);
    }

    public BudgetStatusResponse budgetStatus(BigDecimal budget, Integer userId) {
        if (budget == null || budget.signum() <= 0)
            throw new IllegalArgumentException("Budget must be greater than zero");
        BigDecimal spent = expenseRepository.getTotalSpending(userId);
        BigDecimal remaining = budget.subtract(spent);
        double percent = spent.multiply(BigDecimal.valueOf(100))
                .divide(budget, 2, RoundingMode.HALF_UP).doubleValue();
        return new BudgetStatusResponse(budget, spent, remaining, percent,
                spent.compareTo(budget) > 0);
    }

    public DashboardSummaryResponse dashboardSummary(Integer userId) {
        return expenseRepository.getDashboardSummary(userId);
    }

    public List<ExpenseResponse> monthlyReport(int year, int month, Integer userId) {
        YearMonth ym = YearMonth.of(year, month);
        return expenseRepository
                .findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
                        userId, ym.atDay(1), ym.atEndOfMonth())
                .stream().map(ExpenseResponse::from).toList();
    }

    public String exportCsv(Integer userId) {
        List<Expense> list = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
        StringBuilder sb = new StringBuilder("ID,Title,Amount,Category,Date\n");
        BigDecimal total = BigDecimal.ZERO;
        for (Expense e : list) {
            sb.append(e.getId()).append(',')
              .append(csv(e.getTitle())).append(',')
              .append(e.getAmount()).append(',')
              .append(csv(e.getCategory())).append(',')
              .append(e.getExpenseDate()).append('\n');
            total = total.add(e.getAmount());
        }
        sb.append("TOTAL,,").append(total).append('\n');
        return sb.toString();
    }

    private Expense findOwned(Integer id, Integer userId) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
    }

    private void apply(Expense e, ExpenseRequest req) {
        e.setTitle(req.title());
        e.setAmount(req.amount());
        e.setCategory(req.category());
        e.setExpenseDate(req.expenseDate());
    }

    private String csv(String s) {
        return s == null ? "\"\"" : "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
