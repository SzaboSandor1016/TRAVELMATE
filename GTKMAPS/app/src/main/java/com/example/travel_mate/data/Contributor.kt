package com.example.travel_mate.data

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Contributor(
    @get:Exclude
    val uid: String? = null,
    @get:Exclude
    val username: String? = null,
    val canUpdate: Boolean = false,
    @get:Exclude
    val selected: Boolean? = null
): Serializable {

    companion object {

    }

    fun setUID(uid: String): Contributor {

        return this.copy(
            uid = uid
        )
    }
    fun setUsername(username: String): Contributor {

        return this.copy(
            username = username
        )
    }
    fun setPermissionToUpdate(canUpdate: Boolean): Contributor {

        return this.copy(
            canUpdate = canUpdate
        )
    }
    fun setSelected(isSelected: Boolean ): Contributor {

        return this.copy(

            selected = isSelected
        )
    }
}