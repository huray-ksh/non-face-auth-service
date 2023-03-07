package com.hyuuny.nonfaceauthservice.domain.service;

import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import com.hyuuny.nonfaceauthservice.domain.entity.ExchangeCode;
import com.hyuuny.nonfaceauthservice.domain.repository.ExchangeCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeCodeDomainService {

    private final ExchangeCodeRepository exchangeCodeRepository;


    public ExchangeCode save(ExchangeCode entity) {
        return exchangeCodeRepository.save(entity);
    }

    public ExchangeCode findCode(final String code) {
        return exchangeCodeRepository.findByCode(code).orElseThrow(
                () -> new HttpStatusMessageException(ResponseCode.BAD_REQUEST, "교환 코드가 일치하지 않습니다.")
        );
    }

    public void delete(final Long id) {
        exchangeCodeRepository.deleteById(id);
    }

}
