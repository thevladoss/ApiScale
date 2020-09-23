package ru.paradox.studio.apiscale

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_graph, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        pref = getSharedPreferences("settings", MODE_PRIVATE)

        if (!pref.contains("maxHive")) {
            pref.edit().putInt("maxHive", 1).putInt("hiveNow", 1).putString("name1", "Без названия").apply()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_beehive -> {
                pref.edit().putInt("maxHive", pref.getInt("maxHive", 0)+1).putInt("hiveNow", pref.getInt("maxHive", 0)+1).putString("name"+(pref.getInt("maxHive", 0)+1), "Без названия "+(pref.getInt("maxHive", 0)+1)).apply()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.choose_beehive -> {
                val popupMenu = PopupMenu(this, findViewById(R.id.choose_beehive))
                repeat(pref.getInt("maxHive", 0)) { it ->
                    val item1 = it + 1
                    popupMenu.menu.add(item1, resources.getIdentifier("menu$item1", "id", packageName), item1, pref.getString("name$item1", ""))
                }
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
                    pref.edit().putInt("hiveNow", menuItem.groupId).apply()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                    return@OnMenuItemClickListener true
                })
                popupMenu.show()
            }
        }
        return true
    }
}