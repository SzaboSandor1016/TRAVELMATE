package com.example.travel_mate

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject


/** [ClassCategoryManager]
 *  Legacy class to get the appropriate [Drawable] for the categories of the [Place]s
 *  For the sake of convenience the instruction images are also found here
 */
class ClassCategoryManager{
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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getMarkerIcon(category: String): Drawable {
        val resources = context!!.resources

        return when (category) {
            "landmark" -> resources.getDrawable(R.drawable.ic_landmark, context?.theme)
            "exhibition" -> resources.getDrawable(R.drawable.ic_museum, context?.theme)
            "theme_park" -> resources.getDrawable(R.drawable.ic_theme_park,context?.theme)
            "accommodation" -> resources.getDrawable(R.drawable.ic_hotel,context?.theme)
            "restaurant" -> resources.getDrawable(R.drawable.ic_restaurant,context?.theme)
            "fast_food" -> resources.getDrawable(R.drawable.ic_fast_food,context?.theme)
            "pub_bar" -> resources.getDrawable(R.drawable.ic_bar,context?.theme)
            "cafe" -> resources.getDrawable(R.drawable.ic_cafe,context?.theme)
            "casino" -> resources.getDrawable(R.drawable.ic_casino,context?.theme)
            "cinema" -> resources.getDrawable(R.drawable.ic_cinema,context?.theme)
            "theatre" -> resources.getDrawable(R.drawable.ic_theatre,context?.theme)
            "nightclub" -> resources.getDrawable(R.drawable.ic_nightclub,context?.theme)
            "beach_resort" -> resources.getDrawable(R.drawable.ic_beach,context?.theme)
            "water_park" -> resources.getDrawable(R.drawable.ic_beach_resort,context?.theme)
            "zoo" -> resources.getDrawable(R.drawable.ic_zoo,context?.theme)

            else -> resources.getDrawable(R.drawable.ic_other_marker,context?.theme)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getInstructionImage(type: Int): Drawable {
        val resources = context!!.resources
        /*
0 	Left
1 	Right
2 	Sharp left
3 	Sharp right
4 	Slight left
5 	Slight right
6 	Straight
7 	Enter roundabout
8 	Exit roundabout
9 	U-turn
10 	Goal
11 	Depart
12 	Keep left
13 	Keep right
 */
        return when(type) {

            0 ->	resources.getDrawable(R.drawable.ic_instruction_turn_left, context?.theme)
            1 -> 	resources.getDrawable(R.drawable.ic_instruction_turn_right, context?.theme)
            2 -> 	resources.getDrawable(R.drawable.ic_instruction_turn_sharp_left, context?.theme)
            3 -> 	resources.getDrawable(R.drawable.ic_instruction_turn_sharp_right, context?.theme)
            4 -> 	resources.getDrawable(R.drawable.ic_instruction_turn_slight_left, context?.theme)
            5 ->	resources.getDrawable(R.drawable.ic_instruction_turn_slight_right, context?.theme)
            6 -> 	resources.getDrawable(R.drawable.ic_instruction_straight, context?.theme)
            7 ->	resources.getDrawable(R.drawable.ic_instruction_roundabout, context?.theme)
            8 ->	resources.getDrawable(R.drawable.ic_instruction_roundabout, context?.theme)
            9 ->	resources.getDrawable(R.drawable.ic_instruction_u_turn, context?.theme)
            10 ->	resources.getDrawable(R.drawable.ic_instruction_goal, context?.theme)
            11 ->	resources.getDrawable(R.drawable.ic_instruction_depart, context?.theme)
            12 ->	resources.getDrawable(R.drawable.ic_instruction_straight, context?.theme)
            else ->	resources.getDrawable(R.drawable.ic_instruction_straight, context?.theme)
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
