package com.example.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import com.example.core.ui.R

/** [ClassCategoryManager]
 *  Legacy class to get the appropriate [Drawable] for the categories of the [com.example.model.Place]s
 *  For the sake of convenience the instruction images are also found here
 */
class ClassCategoryManager{
    var context: Context? = null

    constructor(context: Context?) {
        this.context = context
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getMarkerIcon(category: String?): Drawable {
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
}