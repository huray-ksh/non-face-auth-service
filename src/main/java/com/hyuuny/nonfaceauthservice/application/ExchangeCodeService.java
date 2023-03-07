package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.application.dto.ExchangeCodeDto;
import com.hyuuny.nonfaceauthservice.application.dto.ExchangeCodeDto.Response;
import com.hyuuny.nonfaceauthservice.domain.entity.ExchangeCode;
import com.hyuuny.nonfaceauthservice.domain.service.ExchangeCodeDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ExchangeCodeService {

    private final ExchangeCodeDomainService exchangeCodeDomainService;

    @Transactional
    public Response create(ExchangeCodeDto.Create dto) {
        ExchangeCode newExchangeCode = dto.toEntity();
        ExchangeCode savedExchangeCode = exchangeCodeDomainService.save(newExchangeCode);
        return new Response(savedExchangeCode);
    }

    public Response getExchangeCode(String code) {
        ExchangeCode existingExchangeCode = exchangeCodeDomainService.findCode(code);
        return new Response(existingExchangeCode);
    }

    @Transactional
    public Long delete(final Long id) {
        exchangeCodeDomainService.delete(id);
        return id;
    }

}
