package com.curve.delivery.response

data class AutocompleteResponse(val predictions: List<AutocompletePrediction>)
data class AutocompletePrediction(val description: String, val place_id: String) // Add more fields as necessary
