package com.filomenadeveloper.durgente_app.Mensagem.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.filomenadeveloper.durgente_app.R

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

var listFragment = ArrayList<Fragment>()
var listItem = ArrayList<String>()

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return listFragment[position];
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listItem[position];
    }

    override fun getCount(): Int {
        return listItem.size
    }

    fun AddFragment(fragment: Fragment?, title: String?) {
        listFragment.add(fragment!!)
        listItem.add(title!!)
    }
}