package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Date;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, Date> {

    @Override
    public Date convertToDatabaseColumn(YearMonth ym) {
        return ym == null
                ? null
                : Date.valueOf(ym.atDay(1));
    }

    @Override
    public YearMonth convertToEntityAttribute(Date dbDate) {
        return dbDate == null
                ? null
                : YearMonth.from(dbDate.toLocalDate());
    }
}