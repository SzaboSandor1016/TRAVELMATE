package com.example.features.search.domain.models.searchoptionsmodels

data class SearchOptionsStateSearchDomainModel(
        var transportMode: String? = null,
        var minute: Int = 0
    ) {

        private var speed = when (transportMode) {
            "walk" -> 3500 // walk
            "car" -> 50000 // car
            else -> 0
        }

        var distance = (this.speed.times(this.minute)) / 60.0
    }