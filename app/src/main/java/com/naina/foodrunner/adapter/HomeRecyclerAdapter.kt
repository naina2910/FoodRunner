package com.naina.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.naina.foodrunner.R
import com.naina.foodrunner.activity.RestaurantDetailActivity
import com.naina.foodrunner.database.RestaurantDatabase
import com.naina.foodrunner.database.RestaurantEntity
import com.naina.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_row, parent, false)
        return HomeViewHolder(view)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val restaurant = itemList[position]
        holder.txtFoodName.text = restaurant.name
        holder.txtRating.text = restaurant.rating
        holder.txtFoodPrice.text = restaurant.cost_for_one
        Picasso.get().load(restaurant.image_url).error(R.drawable.default_img)
            .into(holder.imgFoodImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.id.toInt(),
            restaurant.name,
            restaurant.rating,
            restaurant.cost_for_one,
            restaurant.image_url
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()


        if (isFav) {
            val image = R.drawable.ic_red_fav
            holder.imgFav.setImageResource(image)
        } else {
            val image = R.drawable.ic_red_fav_outline
            holder.imgFav.setImageResource(image)
        }

        holder.imgFav.setOnClickListener {

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    val image = R.drawable.ic_red_fav
                    holder.imgFav.setImageResource(image)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    val image = R.drawable.ic_red_fav_outline
                    holder.imgFav.setImageResource(image)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        holder.llcontent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailActivity::class.java)
            intent.putExtra("restaurant_name", restaurant.name)
            intent.putExtra("restaurant_id", restaurant.id)
            context.startActivity(intent)
        }
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode 1 -> Check DB if the restaurant favourite or not
        Mode 2 -> Save the restaurant into DB as favourite
        Mode 3 -> Remove the favourite restaurant
        * */

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    // Check DB if the restaurant favourite or not
                    val restaurantEntity: RestaurantEntity? =
                        db.restaurantDao().getRestaurantByName(restaurantEntity.id)
                    db.close()
                    return restaurantEntity != null

                }

                2 -> {

                    // Save the restaurant into DB as fav
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }

                3 -> {

                    // Remove the favourite restaurant
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
}