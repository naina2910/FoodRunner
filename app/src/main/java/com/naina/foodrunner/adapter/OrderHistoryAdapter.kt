package com.naina.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naina.foodrunner.R
import com.naina.foodrunner.model.Food
import com.naina.foodrunner.model.PreviousOrder
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context: Context, val itemList: ArrayList<PreviousOrder>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNameOfRestaurant: TextView = view.findViewById(R.id.txtNameOfRestaurant)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerListOfOrder: RecyclerView = view.findViewById(R.id.recyclerListOfOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = itemList[position]
        holder.txtNameOfRestaurant.text = order.restaurantName
        holder.txtDate.text = order.date.subSequence(0, 8)
        setUpList(holder.recyclerListOfOrder, order)
    }

    fun setUpList(recyclerListOfOrder: RecyclerView, order: PreviousOrder) {
        val foodList = ArrayList<Food>()

        for (i in 0 until order.foodItems.length()) {
            val foodItems = order.foodItems.getJSONObject(i)
            foodList.add(
                Food(
                    foodItems.getString("food_item_id"),
                    foodItems.getString("name"),
                    foodItems.getString("cost")
                )
            )
        }

        val cartRecyclerAdapter = CartRecyclerAdapter(context, foodList)
        val layoutManager = LinearLayoutManager(context)
        recyclerListOfOrder.layoutManager = layoutManager
        recyclerListOfOrder.adapter = cartRecyclerAdapter
    }

}