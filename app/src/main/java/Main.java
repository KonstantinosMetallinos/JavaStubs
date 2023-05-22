package src;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import config.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static config.ConfigurationRecords.MainConfig;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String CONFIG_DIR_LOCATION = "/prefix/in/box/location/";
    private static final String CURRENT_DIR = System.getProperty("user.dir") + "/app/src/main/resources/";
    private static final String YML_EXTENSION = ".yml";
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private Main() { }

    /**
     * Running for example with "-Denv=local -Xms1G -Xmx1G -XX:+AlwaysPreTouch".
     * <br>Note: this code requires to pass in a  VM variable of "-Denv=local"
     *
     * @param args Arguments passed in
     * @throws IOException If Config file is not found.
     */
    public static void main(String[] args) throws IOException {
        final String argsString = Arrays.toString(args);
        LOGGER.info("Starting process with args={}", argsString);

        MainConfig config = loadConfig();
        LOGGER.info("Running process with config inputs: {}", config);
    }

    private static MainConfig loadConfig() throws IOException {
        final PropertyManager pm = PropertyManager.getInstance();
        final String configFileName = pm.getEnvironment().name().toLowerCase() + YML_EXTENSION;
        final String configFilePath = pm.isLocal()
            ? CURRENT_DIR + configFileName
            : CONFIG_DIR_LOCATION + configFileName;

        return YAML_MAPPER.readValue(new File(configFilePath), MainConfig.class);
    }
}
