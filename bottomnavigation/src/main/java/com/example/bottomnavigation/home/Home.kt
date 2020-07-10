package com.example.bottomnavigation.home

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bottomnavigation.R
import me.kaelaela.verticalviewpager.VerticalViewPager
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer

class Home : Fragment() {

    private lateinit var list: List<Content>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home,container,false)

        var verticalViewPager = view.findViewById<VerticalViewPager>(R.id.vertical_viewpager)

        list = List(3){
            var contentFragment = Content(it)
            contentFragment.arguments = bundleOf(Pair("index",it))
            contentFragment
        }

        verticalViewPager.adapter = MyFragmentPagerAdapter(childFragmentManager,list)

        verticalViewPager.overScrollMode = View.OVER_SCROLL_NEVER
        verticalViewPager.setPageTransformer(false, DefaultTransformer())
        //verticalViewPager.setPageTransformer(true, StackTransformer())

        var prePosition = 0
        verticalViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                //LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(PageChangedAction))
                if(position > prePosition){
                    var myPosition = position - 1
                    if(myPosition < 0) {
                        myPosition = 0
                    }
                    list.get(myPosition).stopVideo()
                    list.get(position).prepareVideo()
                }else{
                    list.get(position+1).stopVideo()
                    list.get(position).prepareVideo()
                }
                prePosition = position
            }

        })

        return view
    }

}

class MyFragmentPagerAdapter(fragmentManager: FragmentManager, private var myDataSet:List<Fragment>): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return myDataSet.size
    }

    override fun getItem(position: Int): Fragment {
        return myDataSet[position]
    }

}