package io.krakau.genaifinder

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import io.krakau.genaifinder.preferences.DataManager

class SettingsActivity : AppCompatActivity() {

    // Constants
    private val LOG_SETTINGS_ACTIVITY: String = "SettingsActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // Shared preferences via data manager
    private val SHARED_PREFS_KEY = "genaifinder_shared_preferences"
    private lateinit var dataManager: DataManager

    // View variables
    private lateinit var settingsBack: ImageView
    private lateinit var darkModeSwitch: Switch
    private lateinit var serverListLinearLayout: LinearLayout

    // Data
    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load view from xml
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Pass shared preferences to data manager
        dataManager = DataManager(getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE))

        // Get intent from calling activity
        callingActivity = intent.getStringExtra(CALLING_ACTIVITY)!!
        Log.d(LOG_SETTINGS_ACTIVITY, "callingActivity $callingActivity")

        // Bindings
        settingsBack = findViewById<ImageView>(R.id.settingsBack)
        darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        serverListLinearLayout = findViewById<LinearLayout>(R.id.serverListLinearLayout)

        /*
         * View manipulation
         */
        settingsBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
        }

        // Setup dark mode switch
        darkModeSwitch.isChecked = dataManager.getDarkMode() == 1
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                dataManager.setDarkMode(1)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                dataManager.setDarkMode(0)
            }
        }

        // Setup server url list
        val serverUrls = this.resources.getStringArray(R.array.serverUrls)
        for(i in serverUrls.indices) {
            val textView = TextView(this)
            textView.text = serverUrls[i]
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            textView.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
            textView.setTypeface(Typeface.MONOSPACE)
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
                    putExtra(CALLING_ACTIVITY, callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@SettingsActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}