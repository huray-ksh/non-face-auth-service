package com.hyuuny.nonfaceauthservice.common.security;

import com.hyuuny.nonfaceauthservice.application.CustomOAuthUserServiceV2;
import com.hyuuny.nonfaceauthservice.application.ExchangeCodeService;
import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.ExchangeCodeDto;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.OAuth2Adapter;
import com.hyuuny.nonfaceauthservice.common.security.filters.JwtAuthenticationFilter;
import com.hyuuny.nonfaceauthservice.common.security.provider.JwtProvider;
import com.hyuuny.nonfaceauthservice.domain.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfigV2 {

    private final UserDetailsServiceImpl userDetailService;

    private final JwtProvider jwtProvider;

    private final CustomOAuthUserServiceV2 customOAuthUserServiceV2;

    private final ExchangeCodeService exchangeCodeService;

    private final String[] webSecurityIgnoring = {
            "/",
            "/favicon.ico",
            "/auth/login",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/users/applicants/validate",
            "/users/accounts"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService);
        http.csrf().disable()
                .authorizeHttpRequests(
                        auth -> {
                            try {
                                auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS, "*").permitAll()
                                        .requestMatchers(HttpMethod.GET,
                                                "/",
                                                "/login",
                                                "/code",
                                                "/oAuth2-user-profile/**"
                                        ).permitAll()
                                        .requestMatchers(HttpMethod.POST,
                                                "/auth",
                                                "/oauth2/**",
                                                "/exchange-code",
                                                "/sign-up"
                                        ).permitAll()
                                        .requestMatchers(this.webSecurityIgnoring).permitAll()
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .anyRequest().authenticated();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {
                    log.error("Unauthorized error: {}", e.getMessage());
                    rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .httpBasic(withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(this.jwtProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        http
                .oauth2Login()
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuthUserServiceV2))
                .defaultSuccessUrl("/")
                .successHandler(new OAuth2SuccessHandler(exchangeCodeService))
        ;

        return http.build();
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    public static class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final ExchangeCodeService exchangeCodeService;

        @Override
        public void onAuthenticationSuccess(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication
        ) throws IOException, ServletException {
            if (authentication.getPrincipal() instanceof OAuth2Adapter oAuth2Adapter) {
                String username = oAuth2Adapter.getUsername();
                log.info("username={}", username);

                String redirectUrl = makeAccountProfileRedirectUrl(oAuth2Adapter);
                log.info("redirectUrl={}", redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                return;
            }
            AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
            log.info("username={}", accountAdapter.getUsername());


            ExchangeCodeDto.Create dto = toExchangeCodeDto(accountAdapter);
            ExchangeCodeDto.Response savedExchangeCode = exchangeCodeService.create(dto);
            String redirectUrl = makeRedirectUrl(savedExchangeCode.getCode());
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

            super.onAuthenticationSuccess(request, response, authentication);
            super.handle(request, response, authentication);
        }

        private ExchangeCodeDto.Create toExchangeCodeDto(AccountAdapter adapter) {
            return ExchangeCodeDto.Create.builder()
                    .code(UUID.randomUUID().toString())
                    .userId(adapter.getUserId())
                    .build();
        }

        private String makeRedirectUrl(String code) {
            return UriComponentsBuilder.fromUriString("http://localhost:9000/index")
                    .queryParam("code", code)
                    .build().toUriString();
        }

        private String makeAccountProfileRedirectUrl(OAuth2Adapter oAuth2Adapter) throws UnsupportedEncodingException {
            return UriComponentsBuilder.fromUriString("http://localhost:9000/index")
//                    .queryParam("oAuth2UserProfileId", oAuth2UserProfileId)
                    .queryParam("id", oAuth2Adapter.getId())
                    .queryParam("username", oAuth2Adapter.getUsername())
                    .queryParam("name", URLEncoder.encode(oAuth2Adapter.getName(), "UTF-8"))
                    .queryParam("mobilePhoneNumber", oAuth2Adapter.getMobilePhoneNumber())
                    .queryParam("gender", oAuth2Adapter.getGender().name())
                    .queryParam("profileImageUrl", oAuth2Adapter.getProfileImageUrl())
                    .queryParam("providerId", oAuth2Adapter.getProviderId())
                    .build().toUriString();
        }

    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER", "ACTUATOR")
                .build();
        return new InMemoryUserDetailsManager(user);
    }


}
