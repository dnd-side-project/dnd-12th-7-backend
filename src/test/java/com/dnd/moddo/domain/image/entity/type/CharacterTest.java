package com.dnd.moddo.domain.image.entity.type;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CharacterTest {

	@Test
	@DisplayName("rarity에 해당하는 캐릭터의 검증한다.")
	void GetByRarity() {
		// given
		List<Characters> result;

		// when
		result = Characters.getByRarity(2);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).contains(Characters.ANGEL, Characters.STRAWBERRY);
	}

	@Test
	@DisplayName("옳지않은 rarity일 경우 빈 리스트가 반환된다.")
	void GetByRarity_isEmpty() {
		// given
		List<Characters> result;

		// when
		result = Characters.getByRarity(4);

		// then
		assertThat(result).isEmpty();
	}
}
