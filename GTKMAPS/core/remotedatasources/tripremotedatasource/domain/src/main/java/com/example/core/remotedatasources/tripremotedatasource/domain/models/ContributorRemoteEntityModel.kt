package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class ContributorRemoteEntityModel(
    val canUpdate:Boolean
) {
    constructor(): this(false)
}