package com.dnd.moddo.domain.groupMember.dto.request;

import static com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole.*;

import java.util.List;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;

public record GroupMembersSaveRequest(
	List<GroupMemberSaveRequest> members
) {
	public List<GroupMember> toEntity(Group group) {
		return members.stream()
			.map(m -> m.toEntity(group, getRoleByString(m.role())))
			.toList();
	}
}
