package ctu.cict.khanhtypo.utils;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;

public class ResourceUtils {
    private ResourceUtils() {
    }

    @Nullable
    public static URL getResourcePath(String resourceName) {
        return ResourceUtils.class.getResource("/" + resourceName);
    }


    @NonNull
    public static URL getResourcePathOrThrow(String resourceName) {
        URL resourcePath = getResourcePath(resourceName);
        Preconditions.checkNotNull(resourcePath);
        return resourcePath;
    }

    @Nullable
    public static <T> T getResource(String resourceLocation, Function<@NonNull InputStream, T> factory) {
        InputStream input = ResourceUtils.class.getResourceAsStream("/" + resourceLocation);
        if (input == null) {
            System.out.println("Resource not found: " + resourceLocation);
            return null;
        }
        return factory.apply(input);
    }

    @NonNull
    public static <T> T getResourceOrThrow(String resourceLocation, Function<@NonNull InputStream, T> factory) {
        T result = getResource(resourceLocation, factory);
        if (result == null) {
            throw new RuntimeException("Resource : " + resourceLocation + " is not present.");
        }
        return result;
    }

    public static Image getImage(String resourceLocation) {
        return ResourceUtils.getResourceOrThrow(resourceLocation,
                input -> {
                    try {
                        return ImageIO.read(input);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
