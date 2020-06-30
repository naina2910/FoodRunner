package com.naina.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

import com.naina.foodrunner.R
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtPhoneNo: TextView
    lateinit var txtEmail: TextView
    lateinit var txtDeliveryAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtPhoneNo = view.findViewById(R.id.txtPhoneNo)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress)
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        txtName.text = sharedPreferences.getString("name", "Name")
        txtPhoneNo.text = sharedPreferences.getString("mobile_number", "Phone No")
        txtEmail.text = sharedPreferences.getString("email", "Email Address")
        txtDeliveryAddress.text = sharedPreferences.getString("address", "Delivery Address")

        return view
    }

}
