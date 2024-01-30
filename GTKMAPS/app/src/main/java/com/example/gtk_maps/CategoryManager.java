package com.example.gtk_maps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.overlay.Marker;

public class CategoryManager extends AppCompatActivity {

    Context context;

    CategoryManager(Context context){
        this.context=context;
    }
    CategoryManager(){
    }

    public String getCategoryFromTags(JSONObject tags) {
        String[] categories = {"tourism", "historic", "building", "amenity", "leisure", "boundary", "shop", "route", "sport", "boat"};

        for (String categoryKey : categories) {
            if (tags.has(categoryKey)) {
                String value;
                try {
                    value = tags.getString(categoryKey);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (!value.isEmpty()) {
                    if ("building".equals(categoryKey) && ("retail".equals(value)|| "commercial".equals(value))) {

                    }else if (!"boat".equals(categoryKey) && "yes".equals(value)) {

                    }else if ("boat".equals(categoryKey) && "yes".equals(value)) {
                        return "boat";
                    } else {
                        return value;
                    }
                }
            }
        }

        return "null";
    }
    public Drawable getMarkerIcon(String category){
        Resources resources = context.getResources();

        switch (category){
            case "viewpoint":
                return resources.getDrawable(R.drawable.viewpoint);
            case "monument":
            case "temple":
            case "church":
                return resources.getDrawable(R.drawable.church);
            case "museum":
                return resources.getDrawable(R.drawable.museum);
            case "gallery":
            case "arts_centre":
            case "exhibition_centre":
                return resources.getDrawable(R.drawable.palette);
            case "park":
                return resources.getDrawable(R.drawable.park);
            case "national_park":
                return resources.getDrawable(R.drawable.national_park);
            case "theme_park":
                return resources.getDrawable(R.drawable.theme_park);
            case "fort":
            case "castle":
                return resources.getDrawable(R.drawable.castle);
            case "camp_site":
            case "summer_camp":
                return resources.getDrawable(R.drawable.camp_site);
            case "caravan_site":
                return resources.getDrawable(R.drawable.caravan_site);
            case "hotel":
            case "hostel":
            case "motel":
                return resources.getDrawable(R.drawable.hotel);
            case "guest_house":
                return resources.getDrawable(R.drawable.guest_house);
            case "department_store":
                return resources.getDrawable(R.drawable.store);
            case "mall":
                return resources.getDrawable(R.drawable.mall);
            case "boutique":
            case "clothes":
            case "shoes":
                return resources.getDrawable(R.drawable.shop);
            case "farm":
            case "marketplace":
                return resources.getDrawable(R.drawable.farm);
            case "hiking":
                return resources.getDrawable(R.drawable.hiking);
            case "cycling":
                return resources.getDrawable(R.drawable.cycling);
            case "sailing":
                return resources.getDrawable(R.drawable.sailing);
            case "boat":
            case "marina":
                return resources.getDrawable(R.drawable.boat);
            case "surfing":
            case "wakeboarding":
            case "water_ski":
                return resources.getDrawable(R.drawable.surfing);
            case "restaurant":
            case "fast_food":
            case "food_court":
                return resources.getDrawable(R.drawable.restaurant);
            case "pub":
            case "bar":
            case "biergarten":
                return resources.getDrawable(R.drawable.bar);
            case "cafe":
                return resources.getDrawable(R.drawable.cafe);
            case "casino":
                return resources.getDrawable(R.drawable.casino);
            case "cinema":
                return resources.getDrawable(R.drawable.cinema);
            case "theatre":
                return resources.getDrawable(R.drawable.theatre);
            case "nightclub":
                return resources.getDrawable(R.drawable.nightclub);
            case "massage":
                return resources.getDrawable(R.drawable.massage);
            case "beach_resort":
            case "water_polo":
            case "swimming":
            case "swimming_pool":
            case "water_park":
                return resources.getDrawable(R.drawable.beach_resort);
            case "music_venue":
                return resources.getDrawable(R.drawable.music);
            case "events_venue":
            case "community_centre":
                return resources.getDrawable(R.drawable.community);
            case "stadium":
            case "sports_hall":
            case "sports_centre":
                return resources.getDrawable(R.drawable.sports);

            default: return resources.getDrawable(R.drawable.green_marker);
        }

    }
    public String getMarkerFullCategory(String category){
        Resources resources = context.getResources();

        switch (category){
            case "viewpoint":
                return resources.getString(R.string.viewpoint);
            case "monument":
            case "temple":
            case "church":
                return resources.getString(R.string.church);
            case "museum":
            case "gallery":
            case "arts_centre":
            case "exhibition_centre":
                return resources.getString(R.string.museum);
            case "park":
                return resources.getString(R.string.park);
            case "national_park":
                return resources.getString(R.string.national_park);
            case "theme_park":
                return resources.getString(R.string.adventure_park);
            case "fort":
            case "castle":
                return resources.getString(R.string.castle);
            case "camp_site":
            case "summer_camp":
            case "caravan_site":
            case "hotel":
            case "hostel":
            case "motel":
            case "guest_house":
                return resources.getString(R.string.accommodation);
            case "department_store":
            case "mall":
            case "boutique":
            case "clothes":
            case "shoes":
                return resources.getString(R.string.shopping);
            case "farm":
            case "marketplace":
                return resources.getString(R.string.farmers_market);
            case "hiking":
                return resources.getString(R.string.hiking);
            case "cycling":
                return resources.getString(R.string.bicycle);
            case "sailing":
                return resources.getString(R.string.sailing);
            case "boat":
            case "marina":
                return resources.getString(R.string.sailing);
            case "surfing":
            case "wakeboarding":
            case "water_ski":
            case "water_polo":
            case "swimming":
            case "swimming_pool":
                return resources.getString(R.string.aquatics);
            case "restaurant":
            case "fast_food":
            case "cafe":
            case "pub":
            case "bar":
            case "biergarten":
                return resources.getString(R.string.hotel);
            case "casino":
            case "cinema":
            case "nightclub":
                return resources.getString(R.string.nightclub);
            case "theatre":
                return resources.getString(R.string.theatre);
            case "water_park":
            case "massage":
                return resources.getString(R.string.spa);
            case "beach_resort":
                return resources.getString(R.string.beach);
            case "events_venue":
                return resources.getString(R.string.sport_event);
            case "music_venue":
            case "community_centre":
            case "stadium":
            case "sports_hall":
            case "sports_centre":
                return resources.getString(R.string.festival);

            default: return resources.getString(R.string.other);
        }

    }
}
