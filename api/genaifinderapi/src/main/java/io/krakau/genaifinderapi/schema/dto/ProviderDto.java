package io.krakau.genaifinderapi.schema.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author Dominik
 */
@Schema
public class ProviderDto {
    
    @Schema(type = "string", example = "OpenAI", description = "name")
    private String name;
    
    @Schema(type = "string", example = "Create an image with flowers.", description = "prompt")
    private String prompt;
    
    @Schema(type = "number", example = "123456789", description = "timestamp")
    private Long timestamp;

    public ProviderDto() {
    }

    public ProviderDto(String name, String prompt, Long timestamp) {
        this.name = name;
        this.prompt = prompt;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ProviderDto{" + "name=" + name + ", prompt=" + prompt + ", timestamp=" + timestamp + '}';
    }
    
}
