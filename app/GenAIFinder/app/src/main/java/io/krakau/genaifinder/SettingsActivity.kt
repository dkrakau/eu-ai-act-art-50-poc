package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration

class SettingsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var settingsBack: ImageView

    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))

        settingsBack = findViewById(R.id.settingsBack)

        callingActivity = intent.getStringExtra("callingActivity")!!

        settingsBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
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