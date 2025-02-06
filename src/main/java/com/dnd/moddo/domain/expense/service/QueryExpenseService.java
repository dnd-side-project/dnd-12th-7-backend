package com.dnd.moddo.domain.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryExpenseService {
	private final ExpenseReader expenseReader;

	public ExpensesResponse findAllByGroupId(Long groupId) {
		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);
		return ExpensesResponse.of(expenses);
	}

	public ExpenseResponse findOneByExpenseId(Long expenseId) {
		Expense expense = expenseReader.findOneByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}
}
