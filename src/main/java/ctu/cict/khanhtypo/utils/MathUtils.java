package ctu.cict.khanhtypo.utils;

import com.google.common.base.Preconditions;

public final class MathUtils {
    public static int clampInclusive(int value, int min, int max) {
        Preconditions.checkArgument(min <= max, "min cannot be greater than max");
        return Math.max(min, Math.min(value, max));
    }
}
