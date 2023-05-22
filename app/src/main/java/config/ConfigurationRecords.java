package config;

import java.util.List;

/**
 * Class to consolidate all configuration records.
 */
public final class ConfigurationRecords {

    private ConfigurationRecords() { }

    /**
     * Record Pojo to store immutable Redis configurations.
     *
     * @param database Database config for redis.
     * @param servers  Servers that Redis will run on.
     */
    public record RedisConfig(int database, List<String> servers) { }

    /**
     * Record Pojo to store immutable Port to Name configurations.
     *
     * @param name Name of the service
     * @param port Port where serivce is running in
     */
    public record PortMappings(String name, int port) { }

    /**
     * Record POJO class to store configurations.
     *
     * @param serverHost   Host that this service will run on
     * @param serverPort   Port which this service will run on
     * @param redisConfig  Redis Configurations
     * @param portMappings Map of what runs on which port
     */
    public record MainConfig(String serverHost, int serverPort, RedisConfig redisConfig, List<PortMappings> portMappings) { }

}
