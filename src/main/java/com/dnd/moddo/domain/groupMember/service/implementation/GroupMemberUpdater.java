package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.exception.PaymentConcurrencyException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberReader groupMemberReader;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupReader groupReader;

	@Transactional
	public GroupMember addToGroup(Long groupId, GroupMemberSaveRequest request) {
		Group group = groupReader.read(groupId);
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		List<String> existingNames = new ArrayList<>(groupMembers.stream().map(GroupMember::getName).toList());
		existingNames.add(request.name());

		groupMemberValidator.validateMemberNamesNotDuplicate(existingNames);

		List<Integer> usedProfiles = groupMembers.stream()
			.filter(member -> !member.isManager())
			.map(GroupMember::getProfileId)
			.toList();

		Integer newProfileId = findAvailableProfileId(usedProfiles);

		GroupMember newMember = request.toEntity(group, newProfileId, ExpenseRole.PARTICIPANT);
		newMember = groupMemberRepository.save(newMember);

		return newMember;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public GroupMember updatePaymentStatus(Long groupMemberId, PaymentStatusUpdateRequest request) {
		try {
			GroupMember groupMember = groupMemberRepository.getById(groupMemberId);
			if (groupMember.isPaid() != request.isPaid()) {
				groupMember.updatePaymentStatus(request.isPaid());
				groupMemberRepository.save(groupMember);
			}
			return groupMember;
		} catch (OptimisticLockingFailureException e) {
			throw new PaymentConcurrencyException();
		}
	}

	private Integer findAvailableProfileId(List<Integer> usedProfiles) {
		for (int i = 1; i <= 8; i++) {
			if (!usedProfiles.contains(i)) {
				return i;
			}
		}

		return (usedProfiles.size() % 8) + 1;
	}
}



