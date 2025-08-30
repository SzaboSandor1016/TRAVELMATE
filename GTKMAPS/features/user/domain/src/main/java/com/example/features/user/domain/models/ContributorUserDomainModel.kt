package com.example.features.user.domain.models

import com.google.firebase.database.Exclude
import java.io.Serializable

data class ContributorUserDomainModel(
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

    fun setUID(uid: String): ContributorUserDomainModel {

        return this.copy(
            uid = uid
        )
    }
    fun setUsername(username: String): ContributorUserDomainModel {

        return this.copy(
            username = username
        )
    }
    fun setPermissionToUpdate(canUpdate: Boolean): ContributorUserDomainModel {

        return this.copy(
            canUpdate = canUpdate
        )
    }
    fun setSelected(isSelected: Boolean ): ContributorUserDomainModel {

        return this.copy(

            selected = isSelected
        )
    }
}