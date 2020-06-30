package com.naina.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.naina.foodrunner.R
import com.naina.foodrunner.model.Food


class RestaurantDetailRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Food>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<RestaurantDetailRecyclerAdapter.RestaurantDetailViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    interface OnItemClickListener {
        fun onAddItemClick(food: Food)
        fun onRemoveItemClick(food: Food)
    }

    class RestaurantDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNo: TextView = view.findViewById(R.id.txtNo)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_detail_single_row, parent, false)
        return RestaurantDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RestaurantDetailViewHolder, position: Int) {
        val food = itemList[position]
        holder.txtNo.text = (position + 1).toString()
        holder.txtFoodName.text = food.name
        holder.txtFoodPrice.text = "Rs. ${food.cost_for_one}"

        holder.btnAdd.setOnClickListener {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
            listener.onAddItemClick(food)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnAdd.visibility = View.VISIBLE
            holder.btnRemove.visibility = View.GONE
            listener.onRemoveItemClick(food)
        }
    }
}