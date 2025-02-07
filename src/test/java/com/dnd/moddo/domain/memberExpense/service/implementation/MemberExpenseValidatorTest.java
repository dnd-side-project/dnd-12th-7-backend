package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;

@ExtendWith(MockitoExtension.class)
class MemberExpenseValidatorTest {
	@Mock
	private GroupMemberReader groupMemberReader;
	@InjectMocks
	private MemberExpenseValidator memberExpenseValidator;

	@DisplayName("해당 모임에 참여자 id가 존재하면 검증에 성공한다.")
	@Test
	void validateMembersArePartOfGroupSuccess() {
		//given
		Long groupId = 1L;
		List<MemberExpenseRequest> requests = List.of(new MemberExpenseRequest(1L, 15000L),
			new MemberExpenseRequest(2L, 5000L));

		List<Long> mockGroupMemberIds = List.of(1L, 2L);

		when(groupMemberReader.findIdsByGroupId(eq(groupId))).thenReturn(mockGroupMemberIds);

		//when & when
		assertThatCode(() -> {
			memberExpenseValidator.validateMembersArePartOfGroup(groupId, requests);
		}).doesNotThrowAnyException();
	}

	@DisplayName("해당 모임에 참여자 id가 존재하지 않으면 예외가 발생한다.")
	@Test
	void validateMembersArePartOfGroupFail() {
		//given
		Long groupId = 1L, invalidMemberId = 3L;
		List<MemberExpenseRequest> requests = List.of(new MemberExpenseRequest(1L, 15000L),
			new MemberExpenseRequest(invalidMemberId, 5000L));

		List<Long> mockGroupMemberIds = List.of(1L, 2L);
		when(groupMemberReader.findIdsByGroupId(eq(groupId))).thenReturn(mockGroupMemberIds);

		//when & then
		assertThatThrownBy(() -> {
			memberExpenseValidator.validateMembersArePartOfGroup(groupId, requests);
		}).hasMessage("해당 모임에 속하지 않은 참여자가 포함되어 있습니다 (Member ID: " + invalidMemberId + ")");

	}

}