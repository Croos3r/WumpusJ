package fr.crooser.wumpusj.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

public class DataManager {

    private static final YAMLFactory FACTORY = new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    private static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);

    public static <T> T current(File file, Class<T> objectClass) throws IOException {

        return MAPPER.readValue(file, objectClass);
    }

    public static void save(File file, Object object) throws IOException {

        MAPPER.writeValue(file, object);
    }
}
