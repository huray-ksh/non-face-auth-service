package com.hyuuny.nonfaceauthservice.presentation;

import com.hyuuny.nonfaceauthservice.application.AccountService;
import com.hyuuny.nonfaceauthservice.application.AccountSignUpService;
import com.hyuuny.nonfaceauthservice.application.ExchangeCodeService;
import com.hyuuny.nonfaceauthservice.application.dto.*;
import com.hyuuny.nonfaceauthservice.common.abstraction.AbstractController;
import com.hyuuny.nonfaceauthservice.common.dto.ResultResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController extends AbstractController {

    private final AccountService accountService;

    private final AccountSignUpService accountSignUpService;

    private final ExchangeCodeService exchangeCodeService;

    private final AuthService authService;


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/code")
    public String code() {
        return "code";
    }

    @PostMapping("/exchange-code")
    public ResponseEntity<ResultResponseDto<AuthenticationDto.UserWithToken>> exchangeCode(
            @RequestBody @Valid ExchangeCodeDto.Request request
    ) {
        ExchangeCodeDto.Response existingExchangeCode = exchangeCodeService.getExchangeCode(request.getCode());
        AccountAdapter authenticatedUser = authService.authentication(existingExchangeCode.getUserId());
        AuthenticationDto.AccessToken accessToken = authService.generateToken(authenticatedUser.getUserId());
        exchangeCodeService.delete(existingExchangeCode.getId());
        return ok(
                new AuthenticationDto.UserWithToken(
                        accessToken,
                        new AuthenticationDto.UserInfo(authenticatedUser)
                ));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResultResponseDto<AccountDto.Response>> signUp(
            @RequestBody @Valid AccountDto.SignUpRequest dto
    ) {
        AccountDto.Response signUpAccount = accountSignUpService.signUp(dto);
        return created(signUpAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponseDto<AccountDto.Response>> getAccount(@PathVariable final Long id) {
        AccountDto.Response existingAccount = accountService.getAccount(id);
        return ok(existingAccount);
    }


}
