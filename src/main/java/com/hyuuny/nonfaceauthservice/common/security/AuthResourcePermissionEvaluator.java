package com.hyuuny.nonfaceauthservice.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
public class AuthResourcePermissionEvaluator {

    public boolean hasPrivilege(String targetType, String permission) {
        if ((isEmpty(targetType)) || (isEmpty(permission))) {
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> {
                    if (hasText(permission)) {
                        return grantedAuthority.getAuthority().startsWith(targetType)
                                && grantedAuthority.getAuthority().endsWith(permission);
                    }
                    return Objects.equals(grantedAuthority.getAuthority(), targetType);
                });
    }

}
