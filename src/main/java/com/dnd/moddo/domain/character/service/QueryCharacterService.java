package com.dnd.moddo.domain.character.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.character.service.implementation.CharacterReader;
import com.dnd.moddo.domain.image.dto.CharacterResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryCharacterService {

	private final CharacterReader characterReader;

	public CharacterResponse findCharacterByGroupId(Long groupId) {
		CharacterResponse response = characterReader.getCharacterByGroupId(groupId);
		return response;
	}
}
