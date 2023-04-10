package com.odeyalo.sonata.authentication.support.converter;

import com.odeyalo.sonata.authentication.exceptions.IllegalMfaMethodTypeException;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import org.springframework.core.convert.converter.Converter;

/**
 * Specific {@link Converter} that converts MfaMethodInfo to DTO class
 * @param <S> - source type
 * @param <T> = target type
 *
 * @see Converter
 */
public interface MfaMethodInfoConverter<S extends MfaMethodInfo, T> extends Converter<S, T> {
    /**
     * Convert {@link MfaMethodInfo} to {@link T target}
     * @param source - source value to convert
     * @return - conterted value
     * @throws IllegalMfaMethodTypeException - if the converter received not supported mfa method type
     */
    @Override
    T convert(S source) throws IllegalMfaMethodTypeException;

    /**
     * Method to check if the implementation supports the given method to convert
     * @param method - method to check support of the given method
     * @return true if this implementation supports the given method, false otherwise
     */
    boolean supports(String method);
}
