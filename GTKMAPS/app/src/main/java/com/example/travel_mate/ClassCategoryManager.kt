package com.example.travel_mate

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject


/** [ClassCategoryManager]
 *  Legacy class to get the appropriate [Drawable] for the categories of the [Place]s
 */
class ClassCategoryManager : AppCompatActivity {
    var context: Context? = null

    internal constructor(context: Context?) {
        this.context = context
    }

    /*internal constructor()

    fun getCategoryFromTags(tags: JSONObject): String {
        val categories = arrayOf(
            "castle_type",
            "highway",
            "tourism",
            "historic",
            "building",
            "amenity",
            "leisure",
            "boundary",
            "shop",
            "route",
            "sport",
            "boat",
            "landuse"
        )

        for (categoryKey in categories) {
            if (tags.has(categoryKey)) {
                var value: String
                try {
                    value = tags.getString(categoryKey)
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                if (value.isNotEmpty()) {
                    if ("building" == categoryKey && ("retail" == value || "commercial" == value || "civic" == value || "public" == value)) {
                    } else if ("boat" != categoryKey && "yes" == value) {
                    } else if ("boat" == categoryKey && "yes" == value) {
                        return "boat"
                    } else if (categoryKey == "tourism" && value == "attraction") {
                    } else if (categoryKey == "historic" && value == "heritage") {
                    } else {
                        return value
                    }
                }
            }
        }

        return "null"
    }*/

    fun getMarkerIcon(category: String): Drawable {
        val resources = context!!.resources

        return when (category) {
            "landmark" -> resources.getDrawable(R.drawable.ic_landmark)
            "exhibition" -> resources.getDrawable(R.drawable.ic_museum)
            "theme_park" -> resources.getDrawable(R.drawable.ic_theme_park)
            "accommodation" -> resources.getDrawable(R.drawable.ic_hotel)
            "restaurant" -> resources.getDrawable(R.drawable.ic_restaurant)
            "fast_food" -> resources.getDrawable(R.drawable.ic_fast_food)
            "pub_bar" -> resources.getDrawable(R.drawable.ic_bar)
            "cafe" -> resources.getDrawable(R.drawable.ic_cafe)
            "casino" -> resources.getDrawable(R.drawable.ic_casino)
            "cinema" -> resources.getDrawable(R.drawable.ic_cinema)
            "theatre" -> resources.getDrawable(R.drawable.ic_theatre)
            "nightclub" -> resources.getDrawable(R.drawable.ic_nightclub)
            "beach_resort" -> resources.getDrawable(R.drawable.ic_beach)
            "water_park" -> resources.getDrawable(R.drawable.ic_beach_resort)
            "zoo" -> resources.getDrawable(R.drawable.ic_zoo)

            else -> resources.getDrawable(R.drawable.ic_other_marker)
        }
    }

    /*fun getMarkerFullCategory(category: String): String {
        val resources = context!!.resources

        return when (category) {
            "viewpoint" -> resources.getString(R.string.viewpoint)
            "monument", "temple", "church", "basilica", "monastery", "place_of_worship", "cathedral", "memorial" -> resources.getString(
                R.string.church
            )

            "museum", "gallery", "arts_centre", "exhibition_centre" -> resources.getString(R.string.museum)
            "park", "fountain", "recreation_ground", "pedestrian", "zoo" -> resources.getString(R.string.park)
            "national_park" -> resources.getString(R.string.national_park)
            "theme_park" -> resources.getString(R.string.adventure_park)
            "fort", "castle", "palace" -> resources.getString(R.string.castle)
            "camp_site", "summer_camp", "caravan_site", "hotel", "hostel", "motel", "guest_house" -> resources.getString(
                R.string.accommodation
            )

            "department_store", "mall", "boutique", "clothes", "shoes" -> resources.getString(R.string.shopping)
            "farm", "marketplace" -> resources.getString(R.string.farmers_market)
            "hiking" -> resources.getString(R.string.hiking)
            "cycling" -> resources.getString(R.string.bicycle)
            "sailing" -> resources.getString(R.string.sailing)
            "boat", "marina" -> resources.getString(R.string.sailing)
            "surfing", "wakeboarding", "water_ski", "water_polo", "swimming", "swimming_pool" -> resources.getString(
                R.string.aquatics
            )

            "restaurant", "fast_food", "cafe", "pub", "bar", "biergarten", "confectionery", "pastry" -> resources.getString(
                R.string.hospitality
            )

            "casino", "cinema", "nightclub" -> resources.getString(R.string.programs)
            "theatre" -> resources.getString(R.string.theatre)
            "water_park", "massage" -> resources.getString(R.string.spa)
            "beach_resort" -> resources.getString(R.string.beach)
            "events_venue", "stadium", "sports_hall" -> resources.getString(R.string.sport)
            "music_venue", "community_centre", "sports_centre" -> resources.getString(R.string.festival)

            else -> resources.getString(R.string.other)
        }
    }*/
}
