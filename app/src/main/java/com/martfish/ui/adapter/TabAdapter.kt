package com.martfish.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.martfish.ui.nelayan.pesanan.belumTerkirim.BelumTerkirimFragment
import com.martfish.ui.nelayan.pesanan.terkirim.TerkirimFragment

class TabAdapter(fm: Fragment): FragmentStateAdapter(fm) {

    val listFrgament = arrayOf(BelumTerkirimFragment(), TerkirimFragment())

    override fun getItemCount(): Int = listFrgament.size

    override fun createFragment(position: Int): Fragment {
        return listFrgament[position]
    }
}