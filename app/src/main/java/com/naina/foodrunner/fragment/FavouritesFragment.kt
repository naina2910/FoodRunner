package com.naina.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.naina.foodrunner.R
import com.naina.foodrunner.adapter.FavouriteRecyclerAdapter
import com.naina.foodrunner.database.RestaurantDatabase
import com.naina.foodrunner.database.RestaurantEntity

class FavouritesFragment : Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()
    lateinit var rlNoFav: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        init(view)

        progressLayout.visibility = View.VISIBLE

        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if (dbRestaurantList.isEmpty()) {
            rlNoFav.visibility = View.VISIBLE
            progressLayout.visibility = View.GONE
        } else {
            rlNoFav.visibility = View.GONE
            progressLayout.visibility = View.VISIBLE
            if (activity != null) {
                progressLayout.visibility = View.GONE
                recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
                recyclerFav.layoutManager = layoutManager
                recyclerFav.adapter = recyclerAdapter

            }
        }

        return view
    }

    fun init(view: View) {
        recyclerFav = view.findViewById(R.id.recyclerFav)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = LinearLayoutManager(activity as Context)

        rlNoFav = view.findViewById(R.id.rlNoFav)
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                .build()

            return db.restaurantDao().getAllRestaurant()
        }

    }

}
