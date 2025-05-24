package io.krakau.genaifinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    // constants
    private val LOG_SETTINGS_ACTIVITY: String = "SettingsActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"
    private val PREF_APP_SETTINGS: String = "app_settings"
    private val PREF_APP_SETTINGS_DARK_MODE: String = "dark_mode"
    private val PREF_APP_SETTINGS_SERVER: String = "server"
    private val PREF_APP_SETTINGS_SERVER_SELECTION_ID: String = "server_selection_id"

    // shared preferences
    private lateinit var prefs: SharedPreferences

    // view binding
    private lateinit var settingsBack: ImageView
    private lateinit var darkModeSwitch: Switch
    private lateinit var serverUrlSpinner: Spinner

    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        val applicationContext = this;

        settingsBack = findViewById(R.id.settingsBack)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        serverUrlSpinner = findViewById(R.id.serverUrlSpinner)

        // Get calling activity
        callingActivity = intent.getStringExtra(CALLING_ACTIVITY)!!
        Log.d(LOG_SETTINGS_ACTIVITY, "callingActivity $callingActivity")

        settingsBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
        }

        // get shared prefs
        prefs = getSharedPreferences(PREF_APP_SETTINGS, Context.MODE_PRIVATE)

        // Setup dark mode switch
        darkModeSwitch.isChecked = prefs.getBoolean(PREF_APP_SETTINGS_DARK_MODE, false)
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs.edit() { putBoolean(PREF_APP_SETTINGS_DARK_MODE, true) }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs.edit() { putBoolean(PREF_APP_SETTINGS_DARK_MODE, false) }
            }
            recreate()
        }

        // Setup server url spinner
        val adapter = ArrayAdapter.createFromResource(applicationContext, R.array.serverUrls, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serverUrlSpinner.adapter = adapter
        serverUrlSpinner.setSelection(prefs.getInt(PREF_APP_SETTINGS_SERVER_SELECTION_ID, 0))
        serverUrlSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                prefs.edit() { putString(PREF_APP_SETTINGS_SERVER, selectedItem) }
                prefs.edit() { putInt(PREF_APP_SETTINGS_SERVER_SELECTION_ID, position) }
                Toast.makeText(applicationContext, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {/* Another interface callback */}
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