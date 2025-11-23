package ctu.cict.khanhtypo.utils;

import com.google.common.base.Preconditions;

import java.util.function.Consumer;

public final class MathUtils {
    public static int clampInclusive(int value, int min, int max) {
        Preconditions.checkArgument(min <= max, "min cannot be greater than max");
        return Math.max(min, Math.min(value, max));
    }

    public static <T> T make(T obj, Consumer<T> actions) {
        actions.accept(obj);
        return obj;
    }
}
