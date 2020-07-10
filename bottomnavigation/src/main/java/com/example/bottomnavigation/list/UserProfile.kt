package com.example.bottomnavigation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bottomnavigation.R
import com.example.bottomnavigation.list.Myadapter.Companion.USERNAME_KEY

class UserProfile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_user_profile,container,false)

        val userName = arguments?.getString(USERNAME_KEY)?:"Ali Connors"

        view.findViewById<TextView>(R.id.profile_user_name).text = userName

        return view

    }
}