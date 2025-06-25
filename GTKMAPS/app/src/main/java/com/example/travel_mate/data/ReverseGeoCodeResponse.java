package com.example.travel_mate.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReverseGeoCodeResponse {

    private Geocoding geocoding;

    private List<Features> features;

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    public Geocoding getGeocoding() {
        return geocoding;
    }

    public void setGeocoding(Geocoding geocoding) {
        this.geocoding = geocoding;
    }

    public static class Geocoding {

        private String version;

        private String attribution;

        private Query query;

        private Engine engine;

        private Long timestamp;

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Engine getEngine() {
            return engine;
        }

        public void setEngine(Engine engine) {
            this.engine = engine;
        }

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }

        public String getAttribution() {
            return attribution;
        }

        public void setAttribution(String attribution) {
            this.attribution = attribution;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class Query {

        private Integer size;

        @SerializedName("private")
        private Boolean isPrivate;

        @SerializedName("point.lat")
        private Double pointLat;

        @SerializedName("point.lon")
        private Double pointLon;

        @SerializedName("boundary.circle.lat")
        private Double boundaryCircleLat;

        @SerializedName("boundary.circle.lon")
        private Double boundaryCircleLon;

        private Lang lang;

        private Integer querySize;

        public Integer getQuerySize() {
            return querySize;
        }

        public void setQuerySize(Integer querySize) {
            this.querySize = querySize;
        }

        public Lang getLang() {
            return lang;
        }

        public void setLang(Lang lang) {
            this.lang = lang;
        }

        public Double getBoundaryCircleLon() {
            return boundaryCircleLon;
        }

        public void setBoundaryCircleLon(Double boundaryCircleLon) {
            this.boundaryCircleLon = boundaryCircleLon;
        }

        public Double getBoundaryCircleLat() {
            return boundaryCircleLat;
        }

        public void setBoundaryCircleLat(Double boundaryCircleLat) {
            this.boundaryCircleLat = boundaryCircleLat;
        }

        public Double getPointLon() {
            return pointLon;
        }

        public void setPointLon(Double pointLon) {
            this.pointLon = pointLon;
        }

        public Double getPointLat() {
            return pointLat;
        }

        public void setPointLat(Double pointLat) {
            this.pointLat = pointLat;
        }

        public Boolean getPrivate() {
            return isPrivate;
        }

        public void setPrivate(Boolean aPrivate) {
            isPrivate = aPrivate;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }

    public static class Lang {

        private String name;

        private String iso6391;

        private String iso6393;

        private String via;

        private Boolean defaulted;

        public Boolean getDefaulted() {
            return defaulted;
        }

        public void setDefaulted(Boolean defaulted) {
            this.defaulted = defaulted;
        }

        public String getVia() {
            return via;
        }

        public void setVia(String via) {
            this.via = via;
        }

        public String getIso6393() {
            return iso6393;
        }

        public void setIso6393(String iso6393) {
            this.iso6393 = iso6393;
        }

        public String getIso6391() {
            return iso6391;
        }

        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Engine {

        private String name;

        private String author;

        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Features {

        private String type;

        private Geometry geometry;

        private Properties properties;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Geometry {

        private String type;

        private List<Double> coordinates;

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Properties {

        private String id;

        private String gid;

        private String layer;

        private String source;

        @SerializedName("source_id")
        private String sourceId;

        private String name;

        @SerializedName("housenumber")
        private String houseNumber;

        private String street;

        @SerializedName("postalcode")
        private String postalCode;

        private Double confidence;

        private Double distance;

        private String accuracy;

        private String country;

        @SerializedName("country_gid")
        private String countryGid;

        @SerializedName("country_a")
        private String countryA;

        @SerializedName("macroregion")
        private String macroRegion;

        @SerializedName("macroregion_gid")
        private String macroRegionGid;

        private String region;

        @SerializedName("region_gid")
        private String regionGid;

        @SerializedName("region_a")
        private String regionA;

        private String locality;

        @SerializedName("locality_gid")
        private String localityGid;

        private String neighbourhood;

        @SerializedName("neighbourhood_gid")
        private String neighbourhoodGid;

        private String continent;

        @SerializedName("continent_gid")
        private String continentGid;

        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getContinentGid() {
            return continentGid;
        }

        public void setContinentGid(String continentGid) {
            this.continentGid = continentGid;
        }

        public String getContinent() {
            return continent;
        }

        public void setContinent(String continent) {
            this.continent = continent;
        }

        public String getNeighbourhoodGid() {
            return neighbourhoodGid;
        }

        public void setNeighbourhoodGid(String neighbourhoodGid) {
            this.neighbourhoodGid = neighbourhoodGid;
        }

        public String getNeighbourhood() {
            return neighbourhood;
        }

        public void setNeighbourhood(String neighbourhood) {
            this.neighbourhood = neighbourhood;
        }

        public String getLocalityGid() {
            return localityGid;
        }

        public void setLocalityGid(String localityGid) {
            this.localityGid = localityGid;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getRegionA() {
            return regionA;
        }

        public void setRegionA(String regionA) {
            this.regionA = regionA;
        }

        public String getRegionGid() {
            return regionGid;
        }

        public void setRegionGid(String regionGid) {
            this.regionGid = regionGid;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getMacroRegionGid() {
            return macroRegionGid;
        }

        public void setMacroRegionGid(String macroRegionGid) {
            this.macroRegionGid = macroRegionGid;
        }

        public String getMacroRegion() {
            return macroRegion;
        }

        public void setMacroRegion(String macroRegion) {
            this.macroRegion = macroRegion;
        }

        public String getCountryA() {
            return countryA;
        }

        public void setCountryA(String countryA) {
            this.countryA = countryA;
        }

        public String getCountryGid() {
            return countryGid;
        }

        public void setCountryGid(String countryGid) {
            this.countryGid = countryGid;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(String accuracy) {
            this.accuracy = accuracy;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getLayer() {
            return layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}