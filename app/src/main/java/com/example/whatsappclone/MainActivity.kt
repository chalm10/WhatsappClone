package com.example.whatsappclone

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.adapters.ScreenSliderAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //set up the viewpager for sliding between fragments(viewpager2)
        viewPager.adapter = ScreenSliderAdapter(this)

        //set up the working of viewpager with the tablayout
        TabLayoutMediator(tabs , viewPager , TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when(position){
                0 -> tab.text = "CHATS"
                1 -> tab.text = "PEOPLE"
            }
        }).attach()





    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menumain_search -> {

            }
            R.id.menumain_settings -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}