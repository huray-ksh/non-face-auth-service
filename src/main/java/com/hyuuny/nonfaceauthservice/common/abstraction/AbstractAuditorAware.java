package com.hyuuny.nonfaceauthservice.common.abstraction;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public abstract class AbstractAuditorAware implements AuditorAware<String> {

    public abstract Optional<String> getCurrentAuditor();

}
