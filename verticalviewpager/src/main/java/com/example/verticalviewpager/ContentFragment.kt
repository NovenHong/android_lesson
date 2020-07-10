package com.example.verticalviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.verticalviewpager.R.layout

class ContentFragment():Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(layout.fragment_content,container,false)

        arguments?.let {
            var title = it.getString("title")
            view.findViewById<TextView>(R.id.text_view).text = title
        }

        return view
    }
}