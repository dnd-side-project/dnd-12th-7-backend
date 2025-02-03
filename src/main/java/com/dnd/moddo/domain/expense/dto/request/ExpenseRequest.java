package com.dnd.moddo.domain.expense.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

public record ExpenseRequest(
	Double amount,
	String content,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate date,
	List<MemberExpenseRequest> memberExpenses

) {

	public Expense toEntity(Long meetId) {
		return new Expense(meetId, amount(), content(), date());
	}
}
