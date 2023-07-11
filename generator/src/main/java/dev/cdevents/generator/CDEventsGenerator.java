package dev.cdevents.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CDEventsGenerator {

    /**
     * Event JsonSchema files location.
     */
    private static final String RESOURCES_DIR = "src/main/resources/";
    /**
     * Mustache generic event template file.
     */
    private static final String EVENT_TEMPLATE_MUSTACHE = RESOURCES_DIR + "template/event-template.mustache";

    private CDEventsGenerator() {
    }

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger log = LoggerFactory.getLogger(CDEventsGenerator.class);

    private static final int SUBJECT_INDEX = 2;
    private static final int PREDICATE_INDEX = 3;
    private static final int VERSION_INDEX = 4;
    private static final int SUBSTRING_PIPELINE_INDEX = 8;

    /**
     * Main method to generate CDEvents from Json schema files.
     * @param args [0] - base directory for the cdevents-java-sdk-generator module
     *             [1] - base directory for the cdevents-java-sdk module
     */
    public static void main(String[] args) {
        String generatorBasedir = args[0];
        String sdkBasedir = args[1];

        File folder = new File(sdkBasedir + File.separator + RESOURCES_DIR + "schema");
        System.out.println(folder.toPath().toAbsolutePath());
        if (folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (files != null) {
                //Create Mustache factory and compile event-template.mustache template
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(generatorBasedir + File.separator + EVENT_TEMPLATE_MUSTACHE);

                //Generate a class file for each Json schema file using a mustache template
                String targetDirectory = sdkBasedir + File.separator + "src/main/java/dev/cdevents/events";
                for (File file : files) {
                    SchemaData schemaData = buildCDEventDataFromJsonSchema(file);
                    generateClassFileFromSchemaData(mustache, schemaData, targetDirectory);
                }
            }
        }
    }

    private static void generateClassFileFromSchemaData(Mustache mustache, SchemaData schemaData, String targetPackage) {
        String classFileName = StringUtils.join(new String[]{schemaData.getCapitalizedSubject(), schemaData.getCapitalizedPredicate(), "CDEvent", ".java"});
        File classFile = new File(targetPackage, classFileName);
        try {
            FileWriter fileWriter = new FileWriter(classFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            mustache.execute(bufferedWriter, schemaData).flush();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Exception occurred while generating class file from Json schema {}", e.getMessage());
            throw new IllegalStateException("Exception occurred while generating class file from Json schema ", e);
        }
        log.info("Rendered event-template has been written to file - {}", classFile.getAbsolutePath());
    }

    private static SchemaData buildCDEventDataFromJsonSchema(File file) {
        SchemaData schemaData = new SchemaData();

        log.info("Processing event JsonSchema file: {}", file.getAbsolutePath());
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode contextNode = rootNode.get("properties").get("context").get("properties");

            String eventType = contextNode.get("type").get("enum").get(0).asText();
            log.info("eventType: {}", eventType);
            String[] type = eventType.split("\\.");
            String subject = type[SUBJECT_INDEX];
            String predicate = type[PREDICATE_INDEX];
            String capitalizedSubject = StringUtils.capitalize(subject);
            if (subject.equals("pipelinerun")) {
                capitalizedSubject = capitalizedSubject.substring(0, SUBSTRING_PIPELINE_INDEX)
                        + StringUtils.capitalize(subject.substring(SUBSTRING_PIPELINE_INDEX));
            }
            String capitalizedPredicate = StringUtils.capitalize(predicate);
            String version = type[VERSION_INDEX];

            //set the Schema JsonNode required values to schemaData
            schemaData.setSubject(subject);
            schemaData.setPredicate(predicate);
            schemaData.setCapitalizedSubject(capitalizedSubject);
            schemaData.setCapitalizedPredicate(capitalizedPredicate);
            schemaData.setSchemaFileName(file.getName());
            schemaData.setUpperCaseSubject(subject.toUpperCase());
            schemaData.setVersion(version);

            JsonNode subjectNode = rootNode.get("properties").get("subject").get("properties");
            JsonNode subjectContentNode = subjectNode.get("content").get("properties");
            updateSubjectContentProperties(schemaData, subjectContentNode);
        } catch (IOException e) {
            log.error("Exception occurred while building schema data from Json schema {}", e.getMessage());
            throw new IllegalStateException("Exception occurred while building schema data from Json schema ", e);
        }
        return schemaData;
    }

    private static void updateSubjectContentProperties(SchemaData schemaData, JsonNode subjectContentNode) {
        Iterator<Map.Entry<String, JsonNode>> contentProps = subjectContentNode.fields();
        List<SchemaData.ContentField> contentFields = new ArrayList<>();
        List<SchemaData.ContentObjectField> contentObjectFields = new ArrayList<>();
        while (contentProps.hasNext()) {
            Map.Entry<String, JsonNode> contentMap = contentProps.next();
            String contentField = contentMap.getKey();
            String capitalizedContentField = StringUtils.capitalize(contentField);
            JsonNode contentNode = contentMap.getValue();
            if (!contentNode.get("type").asText().equals("object")) {
                contentFields.add(new SchemaData.ContentField(contentField, capitalizedContentField, "String"));
            } else {
                schemaData.setObjectName(contentField);
                schemaData.setCapitalizedObjectName(capitalizedContentField);
                JsonNode contentObjectNode = contentNode.get("properties");
                Iterator<String> contentObjectProps = contentObjectNode.fieldNames();
                while (contentObjectProps.hasNext()) {
                    String contentObjField = contentObjectProps.next();
                    String capitalizedContentObjField = StringUtils.capitalize(contentObjField);
                    contentObjectFields.add(new SchemaData.ContentObjectField(contentObjField,
                            capitalizedContentObjField, contentField, capitalizedContentField, "String"));
                }
            }
        }
        schemaData.setContentFields(contentFields);
        schemaData.setContentObjectFields(contentObjectFields);
    }

    private static String getFieldsDataType(String fieldName) {
        if (fieldName.equalsIgnoreCase("url")) {
            return "URI";
        } else {
            return "String";
        }
    }
}
