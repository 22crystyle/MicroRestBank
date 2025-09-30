package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Date;
import java.time.YearMonth;

/**
 * JPA AttributeConverter for converting between {@link java.time.YearMonth} and {@link java.sql.Date}.
 * This allows YearMonth objects to be stored and retrieved from a database column as SQL DATE types.
 */
@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, Date> {

    /**
     * Converts a {@link YearMonth} object to a {@link java.sql.Date} for database storage.
     * The conversion assumes the first day of the month.
     *
     * @param ym The YearMonth object to convert.
     * @return A java.sql.Date representing the first day of the given YearMonth, or null if ym is null.
     */
    @Override
    public Date convertToDatabaseColumn(YearMonth ym) {
        return ym == null
                ? null
                : Date.valueOf(ym.atDay(1));
    }

    /**
     * Converts a {@link java.sql.Date} from the database to a {@link YearMonth} object.
     *
     * @param dbDate The java.sql.Date object to convert.
     * @return A YearMonth object representing the month and year of the given Date, or null if dbDate is null.
     */
    @Override
    public YearMonth convertToEntityAttribute(Date dbDate) {
        return dbDate == null
                ? null
                : YearMonth.from(dbDate.toLocalDate());
    }
}