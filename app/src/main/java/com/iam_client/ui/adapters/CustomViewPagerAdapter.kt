package com.iam_client.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by msi on 9/11/2017.
 */
class CustomViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentItems: MutableList<Fragment> = ArrayList()
    private val fragmentTitles: MutableList<String> = ArrayList()

    //we need to newInstance function to add fragments

    fun addFragment(fragmentItem:Fragment,fragmentTitle:String){
        fragmentItems.add(fragmentItem)
        fragmentTitles.add(fragmentTitle)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentItems[position]
    }

    override fun getCount(): Int {
        return fragmentItems.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }

}