package config;

import com.google.common.base.Functions;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Environment {
    PROD,
    UAT,
    QA,
    DEV,
    LOCAL;

    private static final Environment[] VALUES = values();
    private static final Map<String, Environment> NAMES = Arrays.stream(VALUES).collect(Collectors.toMap(Environment::name, Functions.identity()));

    /**
     * Get enum value from name.
     *
     * @param name String value of the enum's name.
     * @return Enum mapped to the name provided.
     */
    public static Environment getByName(String name) {
        return name != null ? NAMES.get(name.toUpperCase()) : null;
    }
}
