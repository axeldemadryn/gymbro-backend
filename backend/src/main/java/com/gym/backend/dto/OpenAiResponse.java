package com.gym.backend.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiResponse {
    private String id;
    private String object;
    private String model;
    private Long created;
    private Usage usage;

    // "output" es lo que devuelve la Responses API moderna; puede contener varios items
    private List<Output> output;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }
    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
    public List<Output> getOutput() { return output; }
    public void setOutput(List<Output> output) { this.output = output; }

    /**
     * Conveniencia: concatena todo el texto disponible dentro de output -> content -> text.
     * Devuelve cadena vacía si no hay texto.
     */
    public String getText() {
        if (output == null) return "";
        return output.stream()
                .filter(o -> o.getContent() != null)
                .flatMap(o -> o.getContent().stream())
                .map(Content::getText)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.joining("\n"));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        private String id;
        private String type;
        private List<Content> content;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<Content> getContent() { return content; }
        public void setContent(List<Content> content) { this.content = content; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        // en la Responses API suele haber objetos de contenido con "type" y "text" (o "items")
        private String type;

        // Muchas respuestas de texto usan "text" como campo principal
        @JsonProperty("text")
        private String text;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;

        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    }
}
