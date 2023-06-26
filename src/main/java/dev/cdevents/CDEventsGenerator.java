package dev.cdevents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import dev.cdevents.constants.CDEventConstants;
import dev.cdevents.exception.CDEventsException;
import dev.cdevents.models.SchemaData;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CDEventsGenerator {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        File folder = new File(CDEventConstants.SCHEMA_FOLDER);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (files != null) {
                //Create Mustache factory and compile event-template.mustache template
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(CDEventConstants.EVENT_TEMPLATE_MUSTACHE);

                //Generate a class file for each Json schema file using a mustache template
                for (File file : files) {
                    SchemaData schemaData = buildCDEventDataFromJsonSchema(file);
                    generateClassFileFromSchemaData(mustache, schemaData);
                }
            }
        }
    }

    private static void generateClassFileFromSchemaData(Mustache mustache, SchemaData schemaData) {
        String classFileName = StringUtils.join(new String[]{schemaData.getCapitalizedSubject(), schemaData.getCapitalizedPredicate(), "CDEvent", ".java"});
        File classFile = new File("src/main/java/dev/cdevents/events/generated", classFileName);
        try {
            FileWriter fileWriter = new FileWriter(classFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            mustache.execute(bufferedWriter, schemaData).flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new CDEventsException("Exception occurred while generating class file from Json schema ", e);
        }
        System.out.println("Rendered event-template has been written to file - "+classFile.getAbsolutePath());
    }

    private static SchemaData buildCDEventDataFromJsonSchema(File file) {
        SchemaData schemaData = new SchemaData();

        System.out.println("Processing event JsonSchema file: " + file.getAbsolutePath());
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode contextNode = rootNode.get("properties").get("context").get("properties");

            String eventType = contextNode.get("type").get("enum").get(0).asText();
            System.out.println("eventType: " + eventType);
            String[] type = eventType.split("\\.");
            String subject = type[2];
            String predicate = type[3];
            String capitalizedSubject = StringUtils.capitalize(subject);
            if(subject.equals("pipelinerun")){
                capitalizedSubject = subject.substring(0, 1).toUpperCase() + subject.substring(1, 8) + subject.substring(8, 9).toUpperCase() + subject.substring(9);
            }
            String capitalizedPredicate = StringUtils.capitalize(predicate);
            String version = type[4];

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
            throw new CDEventsException("Exception occurred while building schema data from Json schema ", e);
        }
        return schemaData;
    }

    private static void updateSubjectContentProperties(SchemaData schemaData, JsonNode subjectContentNode) {
        Iterator<Map.Entry<String, JsonNode>> contentProps = subjectContentNode.fields();
        List<SchemaData.ContentField> contentFields = new ArrayList<>();
        List<SchemaData.ContentObjectField> contentObjectFields = new ArrayList<>();
        for (Iterator<Map.Entry<String, JsonNode>> entryProps = contentProps; entryProps.hasNext(); ) {
            Map.Entry<String, JsonNode> contentMap = entryProps.next();
            String contentField = contentMap.getKey();
            String capitalizedContentField = StringUtils.capitalize(contentField);
            JsonNode contentNode = contentMap.getValue();
            if(!contentNode.get("type").asText().equals("object")){
                contentFields.add(new SchemaData.ContentField(contentField, capitalizedContentField, "String"));
            }else{
                schemaData.setObjectName(contentField);
                schemaData.setCapitalizedObjectName(capitalizedContentField);
                JsonNode contentObjectNode = contentNode.get("properties");
                Iterator<String> contentObjectProps = contentObjectNode.fieldNames();
                for (Iterator<String> objectProps = contentObjectProps; objectProps.hasNext(); ) {
                    String contentObjField = objectProps.next();
                    String capitalizedContentObjField = StringUtils.capitalize(contentObjField);
                    contentObjectFields.add(new SchemaData.ContentObjectField(contentObjField, capitalizedContentObjField, contentField, capitalizedContentField, "String"));
                }
            }
        }
        schemaData.setContentFields(contentFields);
        schemaData.setContentObjectFields(contentObjectFields);
    }

    private static String getFieldsDataType(String fieldName) {
        if (fieldName.equalsIgnoreCase("url")){
            return "URI";
        }else{
            return "String";
        }
    }
}