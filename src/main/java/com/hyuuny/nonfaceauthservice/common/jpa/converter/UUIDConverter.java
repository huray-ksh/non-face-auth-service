package com.hyuuny.nonfaceauthservice.common.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@Converter(autoApply = true)
public class UUIDConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID attribute) {
        return attribute.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbData) {
        if (isEmpty(dbData)) {
            return null;
        }
        return UUID.fromString(dbData);
    }

}
