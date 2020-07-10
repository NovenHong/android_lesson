package com.example.bottomnavigation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        //setupCustomActionBar()
    }

    private fun setupCustomActionBar() {
        var actionbar = supportActionBar!!
        actionbar.setCustomView(
                getLayoutInflater().inflate(R.layout.actionbar_layout,null),
                ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        actionbar.setDisplayShowCustomEnabled(true)
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)

        actionbar.setDisplayShowHomeEnabled(false)
        actionbar.setDisplayShowTitleEnabled(false)

        actionbar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(R.navigation.home, R.navigation.list, R.navigation.form)

        val fragmentManager = supportFragmentManager

        val containerId = R.id.nav_host_container

        // Map of tags
        val graphIdToTagMap = SparseArray<String>()
        // Result. Mutable live data with the selected controlled
        val selectedNavController = MutableLiveData<NavController>()

        var firstFragmentGraphId = 0

        // First create a NavHostFragment for each NavGraph ID
        navGraphIds.forEachIndexed { index, navGraphId ->
            val fragmentTag = getFragmentTag(index)

            // Find or create the Navigation host fragment
            val navHostFragment = obtainNavHostFragment(
                    fragmentManager,
                    fragmentTag,
                    navGraphId,
                    containerId
            )

            // Obtain its id
            val graphId = navHostFragment.navController.graph.id

            if (index == 0) {
                firstFragmentGraphId = graphId
            }

            // Save to the map
            graphIdToTagMap[graphId] = fragmentTag

            // Attach or detach nav host fragment depending on whether it's the selected item.
            if(bottomNavigationView.selectedItemId == graphId){
                selectedNavController.value = navHostFragment.navController
                fragmentManager.beginTransaction()
                        .attach(navHostFragment)
                        .apply {
                            if(index == 0){
                                setPrimaryNavigationFragment(navHostFragment)
                            }
                        }
                        .commitNow()
            }else{
                fragmentManager.beginTransaction()
                        .detach(navHostFragment)
                        .commitNow()
            }
        }

        // Now connect selecting an item with swapping Fragments
        var selectedItemTag = graphIdToTagMap[bottomNavigationView.selectedItemId]
        val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
        var isOnFirstFragment = selectedItemTag == firstFragmentTag

        // When a navigation item is selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            //ViewCompat.animate(item).setDuration(100).scaleX(2f).scaleY(2f).start()

            // Don't do anything if the state is state has already been saved.
            if (fragmentManager.isStateSaved) {
                false
            } else {
                val newlySelectedItemTag = graphIdToTagMap[item.itemId]

                if (selectedItemTag != newlySelectedItemTag) {
                    // Pop everything above the first fragment (the "fixed start destination")
                    fragmentManager.popBackStack(firstFragmentTag,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                            as NavHostFragment

                    // Exclude the first fragment tag because it's always in the back stack.
                    if (firstFragmentTag != newlySelectedItemTag) {
                        // Commit a transaction that cleans the back stack and adds the first fragment
                        // to it, creating the fixed started destination.
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(
                                        R.anim.nav_default_enter_anim,
                                        R.anim.nav_default_exit_anim,
                                        R.anim.nav_default_pop_enter_anim,
                                        R.anim.nav_default_pop_exit_anim)
                                .attach(selectedFragment)
                                .setPrimaryNavigationFragment(selectedFragment)
                                .apply {
                                    // Detach all other Fragments
                                    graphIdToTagMap.forEach { _, fragmentTag ->
                                        if (fragmentTag != newlySelectedItemTag) {
                                            detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                                        }
                                    }
                                }
                                .addToBackStack(firstFragmentTag)
                                .setReorderingAllowed(true)
                                .commit()
                    }
                    selectedItemTag = newlySelectedItemTag
                    isOnFirstFragment = selectedItemTag == firstFragmentTag
                    selectedNavController.value = selectedFragment.navController
                    true
                } else {
                    false
                }
            }
        }

        // Finally, ensure that we update our BottomNavigationView when the back stack changes
        fragmentManager.addOnBackStackChangedListener {
            if (!isOnFirstFragment && !isOnBackStack(firstFragmentTag)) {
                bottomNavigationView.selectedItemId = firstFragmentGraphId
            }

            // Reset the graph if the currentDestination is not valid (happens when the back
            // stack is popped after using the back button).
            selectedNavController.value?.let { controller ->
                if (controller.currentDestination == null) {
                    controller.navigate(controller.graph.id)
                }
            }
        }

        selectedNavController.observe(this, Observer { navController->
            setupActionBarWithNavController(navController)
        })


        currentNavController = selectedNavController
    }

    private fun isOnBackStack(backStackName:String) :Boolean {
        val backStackCount = supportFragmentManager.backStackEntryCount
        for (index in 0 until backStackCount) {
            if(supportFragmentManager.getBackStackEntryAt(index).name == backStackName){
                return true
            }
        }
        return false
    }

    private fun getFragmentTag(index: Int) = "bottomNavigation#$index"

    private fun obtainNavHostFragment(fragmentManager: FragmentManager, fragmentTag: String, navGraphId: Int, containerId: Int): NavHostFragment{
        // If the Nav Host fragment exists, return it
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
        existingFragment?.let { return it }

        // Otherwise, create it and return it.
        val navHostFragment = NavHostFragment.create(navGraphId)
        fragmentManager.beginTransaction()
                .add(containerId, navHostFragment, fragmentTag)
                .commitNow()
        return navHostFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
