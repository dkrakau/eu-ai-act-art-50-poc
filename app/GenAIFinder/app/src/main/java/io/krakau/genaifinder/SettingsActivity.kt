package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.ui.AppBarConfiguration

class SettingsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

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
        callingActivity = intent.getStringExtra("callingActivity")!!
        settingsBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
        }

        // Setup dark mode switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(applicationContext, "DarkMode: $isChecked", Toast.LENGTH_SHORT).show()
            } else {
                // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(applicationContext, "DarkMode: $isChecked", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup server url spinner
        val adapter = ArrayAdapter.createFromResource(applicationContext, R.array.serverUrls, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serverUrlSpinner.adapter = adapter
        serverUrlSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(applicationContext, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {/* Another interface callback */}
        }
    }

    fun goBackIntent(sourceActivity: String): Intent {
        var intent = Intent(this@SettingsActivity, FinderActivity::class.java)
        if(sourceActivity.equals("io.krakau.genaifinder.MainActivity")) {
            intent = Intent(this@SettingsActivity, MainActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.ImageGalleryActivity")) {
            intent = Intent(this@SettingsActivity, ImageGalleryActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.FinderActivity")) {
            intent = Intent(this@SettingsActivity, FinderActivity::class.java)
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