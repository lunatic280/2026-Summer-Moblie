package com.job.july09application

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.job.july09application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private val currentTextReceiver: AppBarTextReceiver?
        get() = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as? AppBarTextReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, MainFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Toast.makeText(this, "Back button pressed", Toast.LENGTH_SHORT).show()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun onQueryTextChange(newText: String?): Boolean {
        updateMainText(newText.takeUnless { it.isNullOrBlank() } ?: "Search Item")
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Menu Item 1")
        menu?.add("Menu Item 2")
        menu?.add("Menu Item 3")

        val searchItem: MenuItem? = menu?.add("Search Item")
        searchItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        val searchView = SearchView(this)
        searchView.queryHint = "Search Item"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return this@MainActivity.onQueryTextChange(newText)
            }
        })
        searchItem?.actionView = searchView
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return onSupportNavigateUp()
        }

        val appBarText = item.title?.toString()
        if (!appBarText.isNullOrBlank()) {
            Toast.makeText(this, "$appBarText clicked", Toast.LENGTH_SHORT).show()
            updateMainText(appBarText)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSecondFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, SecondFragment())
            .addToBackStack(null)
            .commit()
    }

    fun showMainFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun updateMainText(text: CharSequence) {
        currentTextReceiver?.updateText(text)
    }
}
