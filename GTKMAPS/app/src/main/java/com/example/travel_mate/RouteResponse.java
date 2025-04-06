package com.example.travel_mate;

import java.util.List;

public class RouteResponse {
    private String type;
    private List<Double> bBox;
    private List<Feature> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getBBox() {
        return bBox;
    }

    public void setBBox(List<Double> bBox) {
        this.bBox = bBox;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {
        private List<Double> bBox;
        private String type;
        private Properties properties;
        private Geometry geometry;

        public List<Double> getBBox() {
            return bBox;
        }

        public void setBBox(List<Double> bBox) {
            this.bBox = bBox;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }

    public static class Properties {
        private List<Segment> segments;
        private List<Integer> wayPoints;
        private Summary summary;

        public List<Segment> getSegments() {
            return segments;
        }

        public void setSegments(List<Segment> segments) {
            this.segments = segments;
        }

        public List<Integer> getWayPoints() {
            return wayPoints;
        }

        public void setWayPoints(List<Integer> wayPoints) {
            this.wayPoints = wayPoints;
        }

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }
    }

    public static class Segment {
        private double distance;
        private double duration;
        private List<Step> steps;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    public static class Step {
        private double distance;
        private double duration;
        private int type;
        private String instruction;
        private String name;
        private List<Integer> wayPoints;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Integer> getWayPoints() {
            return wayPoints;
        }

        public void setWayPoints(List<Integer> wayPoints) {
            this.wayPoints = wayPoints;
        }
    }

    public static class Summary {
        private double distance;
        private double duration;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }
    }

    public static class Geometry {
        private List<List<Double>> coordinates;
        private String type;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}