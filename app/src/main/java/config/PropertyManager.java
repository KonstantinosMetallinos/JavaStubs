package config;

import exception.StartupException;

public final class PropertyManager {

    private static final String ENV = "env";

    private final Environment environment;
    private final boolean isLocal;

    private PropertyManager() {
        this.environment = Environment.getByName(System.getProperty(ENV));
        System.out.println(System.getProperty(ENV));
        System.out.println(this.environment);

        if (environment == null) {
            throw new StartupException(String.format("Unknown environment provided: [%s]. Exiting application.", System.getProperty(ENV)));
        }

        this.isLocal = Environment.LOCAL == this.environment;
    }

    /**
     * Instantiation on demand - Holder pattern.
     *
     * @return Instantiated PropertyManager object.
     */
    public static PropertyManager getInstance() {
        return Holder.INSTANCE;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isLocal() {
        return isLocal;
    }

    private static class Holder {
        public static final PropertyManager INSTANCE = new PropertyManager();
    }

}
