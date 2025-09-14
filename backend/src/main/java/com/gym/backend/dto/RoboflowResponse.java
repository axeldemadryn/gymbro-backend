package com.gym.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoboflowResponse {
    private List<Prediction> predictions;

    public List<Prediction> getPredictions() { return predictions; }
    public void setPredictions(List<Prediction> predictions) { this.predictions = predictions; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prediction {
        @JsonProperty("class")
        private String className; // Roboflow suele devolver "class"
        private double confidence;
        private int x, y, width, height;

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
    }
}

