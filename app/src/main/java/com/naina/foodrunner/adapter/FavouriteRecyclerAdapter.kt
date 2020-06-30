package com.naina.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.naina.foodrunner.R
import com.naina.foodrunner.activity.RestaurantDetailActivity
import com.naina.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val itemList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_row, parent, false)

        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtFoodName.text = restaurant.name
        holder.txtRating.text = restaurant.rating
        holder.txtFoodPrice.text = restaurant.cost_for_one
        Picasso.get().load(restaurant.image_url).error(R.drawable.default_img)
            .into(holder.imgFoodImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.id,
            restaurant.name,
            restaurant.rating,
            restaurant.cost_for_one,
            restaurant.image_url
        )

        val checkFav = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()


        if (isFav) {
            val image = R.drawable.ic_red_fav
            holder.imgFav.setImageResource(image)
        } else {
            val image = R.drawable.ic_red_fav_outline
            holder.imgFav.setImageResource(image)
        }

        holder.imgFav.setOnClickListener {

            if (!HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
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
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
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
            intent.putExtra("restaurant_id", restaurant.id.toString())
            context.startActivity(intent)
        }

    }
}