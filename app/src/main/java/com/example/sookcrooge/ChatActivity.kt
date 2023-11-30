package com.example.sookcrooge

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sookcrooge.databinding.ActivityMainBinding
import com.example.sookcrooge.databinding.ChatListBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChatActivity : AppCompatActivity() {
    class MyFragmentPagerAdapter(activity: FragmentActivity):
            FragmentStateAdapter(activity){
            val fragments: List<Fragment>
            init {
                fragments= listOf(ChatFragment1(),ChatFragment2())
            }
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tabTitles = listOf("모든 채팅방","내 채팅방")

        setSupportActionBar(binding.allChatToolbar)
        //viewPager 코드
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)

        //tablayout
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.tabs.setTabTextColors(Color.parseColor("#FFFFFFFF"),Color.parseColor("#FFC61A"))
    }


}