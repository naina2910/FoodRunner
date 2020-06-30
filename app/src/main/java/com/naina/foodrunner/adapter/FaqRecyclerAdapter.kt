package com.naina.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.naina.foodrunner.R
import com.naina.foodrunner.model.Faq


class FaqRecyclerAdapter(val context: Context, val itemList: ArrayList<Faq>) :
    RecyclerView.Adapter<FaqRecyclerAdapter.FaqViewHolder>() {
    class FaqViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        var txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_faq_singler_row, parent, false)
        return FaqViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val faq = itemList[position]
        holder.txtQuestion.text = "Q${position + 1}. " + faq.question
        holder.txtAnswer.text = "A${position + 1}. " + faq.answer
    }
}