package com.example.features.trips.presentation.models

sealed interface TripIdentifierTripsPresentationModel {

    fun creatorUsername(): String

    fun getContributorsUsernames(): List<String>

    fun hasPermissionToUpdate(): Boolean

    data object Default: TripIdentifierTripsPresentationModel {
        override fun creatorUsername(): String {
            //TODO NOTE: TEMPORARY SOLUTION
            return ""
        }

        override fun getContributorsUsernames(): List<String> {
            return emptyList()
        }

        override fun hasPermissionToUpdate(): Boolean {
            //NOTE THIS ACTUALLY DOES NOT MATTER
            return false
        }
    }

    data class Local(
        val uuid: String,
        val title: String,
        val permissionToUpdate: Boolean = true
    ) : TripIdentifierTripsPresentationModel {
        override fun creatorUsername(): String {
            //TODO NOTE: TEMPORARY SOLUTION
            return ""
        }

        override fun getContributorsUsernames(): List<String> {
            return emptyList()
        }

        override fun hasPermissionToUpdate(): Boolean {
            return true
        }
    }

    data class Remote(
        val uuid: String,
        //val location: String,
        val title: String,
        val contributorUIDs: Map<String, Boolean>,
        val contributors: Map<String, ContributorTripsPresentationModel>,
        val permissionToUpdate: Boolean = false,
        val creatorUID: String,
        val creatorUsername: String
    ): TripIdentifierTripsPresentationModel {
        override fun creatorUsername(): String {
            return this.creatorUsername
        }

        override fun getContributorsUsernames(): List<String> {
            return this.contributors.values.map { it.username }
        }

        override fun hasPermissionToUpdate(): Boolean {
            return this.permissionToUpdate
        }
    }
}