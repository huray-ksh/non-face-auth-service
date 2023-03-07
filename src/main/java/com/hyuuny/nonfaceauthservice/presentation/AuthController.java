package com.hyuuny.nonfaceauthservice.presentation;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.AuthService;
import com.hyuuny.nonfaceauthservice.application.dto.AuthenticationDto;
import com.hyuuny.nonfaceauthservice.common.abstraction.AbstractController;
import com.hyuuny.nonfaceauthservice.common.dto.ResultResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController extends AbstractController {

    private final AuthService authService;

    @PostMapping(value = "/login", name = "로그인(JWT 발급)")
    public ResponseEntity<ResultResponseDto<AuthenticationDto.UserWithToken>> login(
            @RequestBody @Valid AuthenticationDto.Credential dto
    ) {
        AccountAdapter authenticatedUser = authService.authentication(dto);
        AuthenticationDto.AccessToken accessToken = authService.generateToken(authenticatedUser.getUserId());
        return ok(
                new AuthenticationDto.UserWithToken(
                        accessToken,
                        new AuthenticationDto.UserInfo(authenticatedUser)
                ));
    }

}
