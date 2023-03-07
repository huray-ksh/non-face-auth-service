package com.hyuuny.nonfaceauthservice.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글");

    private final String title;
}
