package com.example.gtk_maps;

import java.util.List;

public class ResponsePhoton {
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {
        private Properties properties;

        private Geometry geometry;

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
        private String name;
        private String housenumber;
        private String city;
        private String street;
        private String country;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHouseNumber() {
            return housenumber;
        }

        public void setHouseNumber(String housenumber) {
            this.housenumber = housenumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
    public static class Geometry{
        private String[] coordinates;


        public String[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(String[] coordinates) {
            this.coordinates = coordinates;
        }
    }
}

