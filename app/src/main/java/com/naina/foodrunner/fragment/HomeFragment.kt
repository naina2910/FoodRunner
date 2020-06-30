package com.naina.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.naina.foodrunner.R
import com.naina.foodrunner.adapter.HomeRecyclerAdapter
import com.naina.foodrunner.model.Restaurant
import com.naina.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var restaurantList = arrayListOf<Restaurant>()
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var ratingComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        Restaurant1.rating.compareTo(Restaurant2.rating, true)
    }
    var priceComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        init(view)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener
                {

                    //here we handle the response
                    try {

                        progressLayout.visibility = View.GONE

                        val jsonObject = it.getJSONObject("data")

                        val success = jsonObject.getBoolean("success")

                        if (success) {

                            val data = jsonObject.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)

                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurantObject)

                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantList)

                                recyclerHome.layoutManager = layoutManager

                                recyclerHome.adapter = recyclerAdapter
                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occur",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener
                {
                    //here we handle the error
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "88732c2e99834c"
                        return headers
                    }

                }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Coonection Not Found")
            dialog.setPositiveButton("open setting")
            { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("exit")
            { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }

        return view
    }

    fun init(view: View) {
        recyclerHome = view.findViewById(R.id.recyclerHome)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.sort) {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By-")
            val options = arrayOf("Cost(Low-High)", "Cost(High-Low)", "Rating")

            dialog.setSingleChoiceItems(options, -1) { dialog, which ->
                when (which) {
                    0 -> {
                        Collections.sort(restaurantList, priceComparator)
                    }
                    1 -> {
                        Collections.sort(restaurantList, priceComparator)
                        restaurantList.reverse()
                    }
                    2 -> {
                        Collections.sort(restaurantList, ratingComparator)
                        restaurantList.reverse()
                    }
                }
            }

            dialog.setPositiveButton("Ok") { text, listner ->
                recyclerAdapter.notifyDataSetChanged()
            }

            dialog.create()
            dialog.show()

        }

        return super.onOptionsItemSelected(item)
    }

}
