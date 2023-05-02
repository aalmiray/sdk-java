package dev.cdevents.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.cdevents.constants.CDEventConstants;
import dev.cdevents.models.CDEvent;
import dev.cdevents.models.PipelineRunFinishedSubject;

import java.net.URI;

public class PipelineRunFinishedCDEvent extends CDEvent {

    private static final String CDEVENT_VERSION = "0.1.0";
    @JsonProperty(required = true)
    private PipelineRunFinishedSubject subject;

    /**
     * Constructor to init CDEvent and set the Subject for {@link PipelineRunFinishedCDEvent}.
     */
    public PipelineRunFinishedCDEvent() {
        initCDEvent(currentCDEventType());
        setSubject(new PipelineRunFinishedSubject(CDEventConstants.SubjectType.PIPELINERUN));
    }

    /**
     * @return subject
     */
    public PipelineRunFinishedSubject getSubject() {
        return subject;
    }

    /**
     * @param subject
     */
    public void setSubject(PipelineRunFinishedSubject subject) {
        this.subject = subject;
    }

    /**
     * @return the current CDEvent type
     */
    @Override
    public String currentCDEventType() {
        return CDEventConstants.CDEventTypes.PipelineRunFinishedEvent.getEventType().concat(CDEVENT_VERSION);
    }

    /**
     * @return the pipeline-run-finished-event schema URL
     */
    @Override
    public String schemaURL() {
        return String.format("https://cdevents.dev/%s/schema/pipeline-run-finished-event", CDEventConstants.CDEVENTS_SPEC_VERSION);
    }

    /**
     * @return the pipeline-run-finished-event schema Json
     */
    @Override
    public String eventSchema() {
        return "{\n" +
                "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"$id\": \"https://cdevents.dev/0.1.0/schema/pipeline-run-finished-event\",\n" +
                "  \"properties\": {\n" +
                "    \"context\": {\n" +
                "      \"properties\": {\n" +
                "        \"version\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\n" +
                "            \"0.1.0\"\n" +
                "          ],\n" +
                "          \"default\": \"0.1.0\"\n" +
                "        },\n" +
                "        \"id\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1\n" +
                "        },\n" +
                "        \"source\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1\n" +
                "        },\n" +
                "        \"type\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1\n" +
                "        },\n" +
                "        \"timestamp\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"format\": \"date-time\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"additionalProperties\": false,\n" +
                "      \"type\": \"object\",\n" +
                "      \"required\": [\n" +
                "        \"version\",\n" +
                "        \"id\",\n" +
                "        \"source\",\n" +
                "        \"type\",\n" +
                "        \"timestamp\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"subject\": {\n" +
                "      \"properties\": {\n" +
                "        \"id\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1\n" +
                "        },\n" +
                "        \"source\": {\n" +
                "          \"type\": \"string\"\n" +
                "        },\n" +
                "        \"type\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1\n" +
                "        },\n" +
                "        \"content\": {\n" +
                "          \"properties\": {\n" +
                "            \"pipelineName\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"url\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"outcome\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"errors\": {\n" +
                "              \"type\": \"string\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"additionalProperties\": false,\n" +
                "          \"type\": \"object\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"additionalProperties\": false,\n" +
                "      \"type\": \"object\",\n" +
                "      \"required\": [\n" +
                "        \"id\",\n" +
                "        \"type\",\n" +
                "        \"content\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"customData\": {\n" +
                "      \"oneOf\": [\n" +
                "        {\n" +
                "          \"type\": \"object\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"string\",\n" +
                "          \"contentEncoding\": \"base64\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"customDataContentType\": {\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"additionalProperties\": false,\n" +
                "  \"type\": \"object\",\n" +
                "  \"required\": [\n" +
                "    \"context\",\n" +
                "    \"subject\"\n" +
                "  ]\n" +
                "}";
    }

    /**
     * @param subjectId
     * sets the subject Id
     */
    public void setSubjectId(String subjectId) {
        getSubject().setId(subjectId);
    }

    /**
     * @param subjectSource
     * sets the pipeline source
     */
    public void setSubjectSource(URI subjectSource) {
        getSubject().setSource(subjectSource);
    }

    /**
     * @param pipelineName
     * sets the pipeline name
     */
    public void setSubjectPipelineName(String pipelineName) {
        getSubject().getContent().setPipelineName(pipelineName);
    }

    /**
     * @param subjectUrl
     * sets the pipeline URL
     */
    public void setSubjectUrl(URI subjectUrl) {
        getSubject().getContent().setUrl(subjectUrl);
    }

    /**
     * @param subjectOutcome
     * sets the {@link PipelineRunFinishedCDEvent} outcome
     */
    public void setSubjectOutcome(CDEventConstants.Outcome subjectOutcome) {
        getSubject().getContent().setOutcome(subjectOutcome);
    }

    /**
     * @param subjectErrors
     * sets the {@link PipelineRunFinishedCDEvent} errors
     */
    public void setSubjectErrors(String subjectErrors) {
        getSubject().getContent().setErrors(subjectErrors);
    }
}