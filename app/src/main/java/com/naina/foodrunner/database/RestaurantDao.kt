package com.naina.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant_table")
    fun getAllRestaurant(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurant_table where id=:id")
    fun getRestaurantByName(id: Int): RestaurantEntity
}