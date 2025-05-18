package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration

class InsightActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
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
                startActivity(Intent(this@InsightActivity, SettingsActivity::class.java).apply {
                    putExtra("callingActivity", InsightActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@InsightActivity, InformationActivity::class.java).apply {
                    putExtra("callingActivity", InsightActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}