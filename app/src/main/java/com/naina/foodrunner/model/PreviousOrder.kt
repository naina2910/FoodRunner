package com.naina.foodrunner.model

import org.json.JSONArray

data class PreviousOrder(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val date: String,
    val foodItems: JSONArray
)