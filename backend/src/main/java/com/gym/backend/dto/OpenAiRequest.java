package com.gym.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiRequest {
    @JsonProperty("model")
    private String model;

    @JsonProperty("input")
    private List<Message> input;

    public OpenAiRequest(String model, List<Message> input) {
        this.model = model;
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getInput() {
        return input;
    }

    // ---------- INNER CLASSES ----------
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private List<Content> content;

        public Message(String role, List<Content> content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public List<Content> getContent() {
            return content;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private String text;      // usado cuando type=input_text

        @JsonProperty("image_url")
        private String image_url; // usado cuando type=input_image

        // Constructor para texto
        public static Content text(String text) {
            Content c = new Content();
            c.type = "input_text";
            c.text = text;
            return c;
        }

        // Constructor para imagen
        public static Content image(String url) {
            Content c = new Content();
            c.type = "input_image";
            c.image_url = url;
            return c;
        }

        public String getType() { return type; }
        public String getText() { return text; }
        public String getImage_url() { return image_url; }
    }
}
