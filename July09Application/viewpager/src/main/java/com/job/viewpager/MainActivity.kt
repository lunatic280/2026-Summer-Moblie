package com.job.viewpager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.job.viewpager.databinding.ActivityMainBinding

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstFragment()
            else -> SecondFragment()
        }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "첫 번째"
                else -> "두 번째"
            }
        }.attach()
    }

    fun showNextPage() {
        val nextPage = binding.viewPager.currentItem + 1
        if (nextPage < (binding.viewPager.adapter?.itemCount ?: 0)) {
            binding.viewPager.currentItem = nextPage
        }
    }

    fun showPreviousPage() {
        val previousPage = binding.viewPager.currentItem - 1
        if (previousPage >= 0) {
            binding.viewPager.currentItem = previousPage
        }
    }
}
