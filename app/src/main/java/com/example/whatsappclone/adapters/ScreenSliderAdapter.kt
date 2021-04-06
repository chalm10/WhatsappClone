package com.example.whatsappclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsappclone.fragments.InboxFragment
import com.example.whatsappclone.fragments.PeoplesFragment

class ScreenSliderAdapter(fa : FragmentActivity) :FragmentStateAdapter(fa){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                InboxFragment()
            }
            else -> { //basically at position 1
                PeoplesFragment()
            }
        }
    }

}
