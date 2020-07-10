package com.example.tabnavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.tabnavigation.fragment.Fragment1
import com.example.tabnavigation.fragment.Fragment2
import com.example.tabnavigation.fragment.Fragment3
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.pager)

        viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        tabLayout.setupWithViewPager(viewPager)

        for (i in 0 until tabLayout.tabCount) {
            var tab = tabLayout.getTabAt(i)
            when(i) {
                0 -> tab?.setIcon(R.drawable.ic_home)
                1 -> tab?.setIcon(R.drawable.ic_list)
                2 -> tab?.setIcon(R.drawable.ic_feedback)
            }
        }
    }
}

class MyFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val pagers  = listOf<Fragment>(Fragment1(),Fragment2(),Fragment3())

    override fun getItem(position: Int): Fragment {
        return pagers[position]
    }

    override fun getCount(): Int {
        return pagers.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Tab ${position+1}"
    }
}
