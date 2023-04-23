package org.zunker.gpt.enmus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Getter
@RequiredArgsConstructor
public enum UserType {
    USER("Q:%s\n"), BOT("A: %s\n\n\n");
    private final String code;
}
