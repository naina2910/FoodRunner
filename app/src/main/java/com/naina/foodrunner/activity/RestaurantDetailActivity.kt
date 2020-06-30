package com.naina.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.naina.foodrunner.R
import com.naina.foodrunner.adapter.RestaurantDetailRecyclerAdapter
import com.naina.foodrunner.database.OrderEntity
import com.naina.foodrunner.database.RestaurantDatabase
import com.naina.foodrunner.model.Food
import com.naina.foodrunner.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailActivity : AppCompatActivity() {

    lateinit var recyclerRestaurantDetail: RecyclerView
    lateinit var recyclerRecyclerAdapter: RestaurantDetailRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var foodList = arrayListOf<Food>()
    lateinit var progressLayout: RelativeLayout
    var orderList = arrayListOf<Food>()
    lateinit var btnProceedToCart: Button
    lateinit var imgFav: ImageView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    var restaurantName: String? = null
    var restaurantId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        init()

        progressLayout.visibility = View.VISIBLE
        btnProceedToCart.visibility = View.GONE

        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurant_name")
            restaurantId = intent.getStringExtra("restaurant_id")
        } else {
            finish()
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (restaurantId == null || restaurantName == null) {
            finish()
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some error occur!!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName

        setImgIcon()

        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }

        val queue = Volley.newRequestQueue(this@RestaurantDetailActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this@RestaurantDetailActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener {
                    try {

                        val data = it.getJSONObject("data")

                        val success = data.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE
                            val foodData = data.getJSONArray("data")
                            for (i in 0 until foodData.length()) {
                                val foodJsonObject = foodData.getJSONObject(i)
                                val food = Food(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one")
                                )
                                foodList.add(food)

                                recyclerRecyclerAdapter =
                                    RestaurantDetailRecyclerAdapter(this@RestaurantDetailActivity,
                                        foodList,
                                        object :
                                            RestaurantDetailRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(food: Food) {
                                                orderList.add(food)
                                                if (orderList.size > 0) {
                                                    btnProceedToCart.visibility = View.VISIBLE
                                                    RestaurantDetailRecyclerAdapter.isCartEmpty =
                                                        false
                                                }
                                            }

                                            override fun onRemoveItemClick(food: Food) {

                                                orderList.remove(food)
                                                if (orderList.size == 0) {
                                                    btnProceedToCart.visibility = View.GONE
                                                    RestaurantDetailRecyclerAdapter.isCartEmpty =
                                                        true
                                                }

                                            }

                                        })

                                recyclerRestaurantDetail.adapter = recyclerRecyclerAdapter

                                recyclerRestaurantDetail.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(
                                this@RestaurantDetailActivity,
                                "Some error occur!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailActivity,
                            "Some error occur!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    if (this@RestaurantDetailActivity != null) {
                        Toast.makeText(
                            this@RestaurantDetailActivity,
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
            val dialog = AlertDialog.Builder(this@RestaurantDetailActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("open setting")
            { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("exit")
            { text, listner ->
                ActivityCompat.finishAffinity(this@RestaurantDetailActivity)
            }

            dialog.create()
            dialog.show()
        }
    }

    fun init() {
        layoutManager = LinearLayoutManager(this@RestaurantDetailActivity)
        recyclerRestaurantDetail = findViewById(R.id.recyclerRestaurantDetail)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        progressLayout = findViewById(R.id.progressLayout)
        toolbar = findViewById(R.id.toolbar)
        imgFav = findViewById(R.id.imgFav)
    }

    private fun setImgIcon() {
        val async = GetAllFavAsyncTask(this).execute()
        val result = async.get()

        if (restaurantId in result) {
            imgFav.setImageResource(R.drawable.ic_red_fav)
        } else {
            imgFav.setImageResource(R.drawable.ic_red_fav_outline)
        }
    }

    class GetAllFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurant()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }

    private fun proceedToCart() {

        val gson = Gson()

        val orderItems = gson.toJson(orderList)

        val async = CartItems(
            this@RestaurantDetailActivity,
            restaurantId.toString(),
            orderItems,
            1
        ).execute()
        val result = async.get()

        if (result) {
            val bundle = Bundle()
            bundle.putString("restaurant_name", restaurantName)
            bundle.putString("restaurant_id", restaurantId)
            val intent = Intent(this@RestaurantDetailActivity, CartActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        } else {
            Toast.makeText(
                this@RestaurantDetailActivity,
                "Some unexpected error",
                Toast.LENGTH_SHORT
            )
                .show()
        }


    }

    class CartItems(val context: Context, val resId: String, val orderItem: String, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(resId, orderItem))
                    db.close()
                    return true
                }
                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(resId, orderItem))
                    db.close()
                    return true
                }
            }

            return false
        }


    }

    override fun onBackPressed() {
        if (orderList.size != 0) {
            val dialog = AlertDialog.Builder(this@RestaurantDetailActivity)
            dialog.setTitle("Are you sure")
            dialog.setMessage("All items in cart will be reset")
            dialog.setPositiveButton("Yes")
            { text, listner ->
                RestaurantDetailRecyclerAdapter.isCartEmpty = true
                super.onBackPressed()
            }
            dialog.setNegativeButton("No")
            { text, listner ->
                text.cancel()
            }
            dialog.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }
    }
}
