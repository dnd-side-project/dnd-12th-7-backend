package com.dnd.moddo.global.jwt.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JwtConstants {

    AUTH_ID("userId"),
    EMAIL("email"),
    TYPE("type"),
    EMPTY(" "),
    PREFIX("prefix"),
    ROLE("role"),
    ACCESS_KEY("access_token"),
    REFRESH_KEY("refresh_token");

    public final String message;

}
