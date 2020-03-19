package com.silva.industries.microservices.util;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

@UtilityClass
public final class ObjectMapperUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static <S, D> D map(final S entityToMap, Class<D> expectedClass) {
        return modelMapper.map(entityToMap, expectedClass);
    }

    public static <S, D> List<D> mapAll(final Iterable<S> entityList, Class<D> expectedClass) {
        return CollectionUtils.toList(entityList).stream()
                .map(entity -> map(entity, expectedClass))
                .collect(Collectors.toList());
    }
}
