package com.silva.industries.microservices.util;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Utility class to group functions related with Collections.
 */
@UtilityClass
public final class CollectionUtils {

    public static <T> List<T> toList(Iterable<T> items) {
        List<T> list = new ArrayList<>();
        items.forEach(list::add);
        return list;
    }
}
