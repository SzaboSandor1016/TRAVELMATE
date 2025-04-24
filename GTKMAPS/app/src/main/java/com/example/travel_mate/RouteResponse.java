package com.example.travel_mate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteResponse {
    private List<Double> bBox;
    private List<Routes> routes;

    public List<Double> getBBox() {
        return bBox;
    }

    public void setBBox(List<Double> bBox) {
        this.bBox = bBox;
    }

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }

    public static class Routes {
        private List<Double> bBox;
        private List<Double> wayPoints;
        private List<Segment> segments;
        private String geometry;
        private Summary summary;

        public List<Double> getBBox() {
            return bBox;
        }

        public void setBBox(List<Double> bBox) {
            this.bBox = bBox;
        }

        public List<Double> getWayPoints() {
            return wayPoints;
        }

        public void setWayPoints(List<Double> wayPoints) {
            this.wayPoints = wayPoints;
        }

        public List<Segment> getSegments() {
            return segments;
        }

        public void setSegments(List<Segment> segments) {
            this.segments = segments;
        }

        public String getGeometry() {
            return geometry;
        }

        public void setGeometry(String geometry) {
            this.geometry = geometry;
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
        @SerializedName("way_points")
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
}