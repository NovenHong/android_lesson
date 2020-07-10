package com.example.ws

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bottomNavButtonList: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())
        setupBottomNavigation()
    }

    abstract fun getLayoutView(): Int

    override fun onClick(v: View?) {
        var animation = AnimationUtils.loadAnimation(this,R.anim.bottom_nav_button)
        v?.startAnimation(animation)
        when(v?.id){
            R.id.bottom_nav_button1 -> {
                selected(v as Button)
            }
            R.id.bottom_nav_button2 -> {
                selected(v as Button)
            }
            R.id.bottom_nav_button4 -> {
                selected(v as Button)
            }
            R.id.bottom_nav_button5 -> {
                selected(v as Button)
            }
        }
        bottomNavigationSelected(v)
    }

    fun bottomNavigationSelected(v: View?){

    }

    private fun selected(button: Button){
        bottomNavButtonList.forEach {
            it.compoundDrawables[1].setColorFilter(resources.getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN)
            it.setTextColor(resources.getColor(android.R.color.darker_gray))
        }

        button.compoundDrawables[1].setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        button.setTextColor(Color.WHITE)

    }

    protected fun setupBottomNavigation(){
        var bottomNavButton1 = findViewById<Button>(R.id.bottom_nav_button1)
        var bottomNavButton2 = findViewById<Button>(R.id.bottom_nav_button2)
        var bottomNavButton3 = findViewById<ImageButton>(R.id.bottom_nav_button3)
        var bottomNavButton4 = findViewById<Button>(R.id.bottom_nav_button4)
        var bottomNavButton5 = findViewById<Button>(R.id.bottom_nav_button5)

        var icPlay =bottomNavButton1.compoundDrawables[1]
        var icVideo = bottomNavButton2.compoundDrawables[1]
        var icRing = bottomNavButton4.compoundDrawables[1]
        var icUser = bottomNavButton5.compoundDrawables[1]

        icPlay.setBounds(0,9,90,90)
        icVideo.setBounds(0,9,90,90)
        icRing.setBounds(0,9,90,90)
        icUser.setBounds(0,9,90,90)

        bottomNavButton1.setCompoundDrawables(null,icPlay,null,null)
        bottomNavButton2.setCompoundDrawables(null,icVideo,null,null)
        bottomNavButton4.setCompoundDrawables(null,icRing,null,null)
        bottomNavButton5.setCompoundDrawables(null,icUser,null,null)

        bottomNavButtonList = listOf(bottomNavButton1,bottomNavButton2,bottomNavButton4,bottomNavButton5)

        bottomNavButton1.setOnClickListener(this)
        bottomNavButton2.setOnClickListener(this)
        bottomNavButton3.setOnClickListener(this)
        bottomNavButton4.setOnClickListener(this)
        bottomNavButton5.setOnClickListener(this)

        //default select one
        selected(bottomNavButton1)
    }

}