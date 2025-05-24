package io.krakau.genaifinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    // constants
    private val LOG_SETTINGS_ACTIVITY: String = "SettingsActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // shared preferences via data manager
    private val SHARED_PREFS_KEY = "genaifinder_shared_preferences"
    private lateinit var dataManager: DataManager

    // view binding
    private lateinit var settingsBack: ImageView
    private lateinit var darkModeSwitch: Switch
    private lateinit var serverListLinearLayout: LinearLayout

    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        val applicationContext = this;

        settingsBack = findViewById<ImageView>(R.id.settingsBack)
        darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        serverListLinearLayout = findViewById<LinearLayout>(R.id.serverListLinearLayout)

        // Pass shared preferences to data manager
        dataManager = DataManager(getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE))

        // Get calling activity
        callingActivity = intent.getStringExtra(CALLING_ACTIVITY)!!
        Log.d(LOG_SETTINGS_ACTIVITY, "callingActivity $callingActivity")

        settingsBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
        }

        // Setup dark mode switch
        darkModeSwitch.isChecked = dataManager.getDarkMode()
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                dataManager.setDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                dataManager.setDarkMode(false)
            }
            recreate()
        }

        // Setup server url list
        var serverUrls = dataManager.getServerUrls().toList()
        for(i in serverUrls.indices) {
            val textView = TextView(this)
            textView.text = serverUrls[i]
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            serverListLinearLayout.addView(textView)
        }
    }

    fun goBackIntent(sourceActivity: String): Intent {
        var intent = Intent(this@SettingsActivity, MainActivity::class.java)
        if(sourceActivity.equals("io.krakau.genaifinder.ImageGalleryActivity")) {
            intent = Intent(this@SettingsActivity, ImageGalleryActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.FinderActivity")) {
            intent = Intent(this@SettingsActivity, FinderActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.InsightsActivity")) {
            intent = Intent(this@SettingsActivity, InsightsActivity::class.java)
        }
        return intent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@SettingsActivity, SettingsActivity::class.java).apply {
                    putExtra("callingActivity", callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@SettingsActivity, InformationActivity::class.java).apply {
                    putExtra("callingActivity", callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}