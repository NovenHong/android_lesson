package com.example.verticalviewpager

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import me.kaelaela.verticalviewpager.VerticalViewPager
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer
import me.kaelaela.verticalviewpager.transforms.StackTransformer
import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var verticalViewPager = findViewById<VerticalViewPager>(R.id.vertical_viewpager)

        verticalViewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager,List(10){
            var contentFragment = ContentFragment()
            contentFragment.arguments = bundleOf(Pair("title","Title ${it+1}"))
            contentFragment
        })

        verticalViewPager.overScrollMode = View.OVER_SCROLL_NEVER
        //verticalViewPager.setPageTransformer(false, DefaultTransformer())
        verticalViewPager.setPageTransformer(true, StackTransformer())

        supportActionBar!!.setCustomView(
                getLayoutInflater().inflate(R.layout.actionbar_layout,null),
                ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))

        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)

        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))

        findViewById<Button>(R.id.action_bar_button1).setOnClickListener{
            println("action_bar_button1")
        }
    }
}

class MyFragmentPagerAdapter(fragmentManager: FragmentManager,var myDataSet:List<Fragment>): FragmentPagerAdapter(fragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return myDataSet.size
    }

    override fun getItem(position: Int): Fragment {
        return myDataSet[position]
    }

}