
package dev.cdevents.models.taskrun.started;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "taskName",
    "url",
    "pipelineRun"
})
@Generated("jsonschema2pojo")
public class Content {

    @JsonProperty("taskName")
    private String taskName;
    @JsonProperty("url")
    private String url;
    @JsonProperty("pipelineRun")
    private PipelineRun pipelineRun;

    @JsonProperty("taskName")
    public String getTaskName() {
        return taskName;
    }

    @JsonProperty("taskName")
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("pipelineRun")
    public PipelineRun getPipelineRun() {
        return pipelineRun;
    }

    @JsonProperty("pipelineRun")
    public void setPipelineRun(PipelineRun pipelineRun) {
        this.pipelineRun = pipelineRun;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.taskName == null)? 0 :this.taskName.hashCode()));
        result = ((result* 31)+((this.url == null)? 0 :this.url.hashCode()));
        result = ((result* 31)+((this.pipelineRun == null)? 0 :this.pipelineRun.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Content) == false) {
            return false;
        }
        Content rhs = ((Content) other);
        return ((((this.taskName == rhs.taskName)||((this.taskName!= null)&&this.taskName.equals(rhs.taskName)))&&((this.url == rhs.url)||((this.url!= null)&&this.url.equals(rhs.url))))&&((this.pipelineRun == rhs.pipelineRun)||((this.pipelineRun!= null)&&this.pipelineRun.equals(rhs.pipelineRun))));
    }

}
