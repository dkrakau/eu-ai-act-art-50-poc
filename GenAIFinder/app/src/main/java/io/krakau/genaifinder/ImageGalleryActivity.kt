package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import io.krakau.genaifinder.databinding.ActivityMainBinding
import android.util.Log
import android.widget.TextView
import org.w3c.dom.Text

class ImageGalleryActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var txt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)*/
        setContentView(R.layout.activity_image_gallery)
        setSupportActionBar(findViewById(R.id.toolbar))

        txt = findViewById<TextView>(R.id.testIntent)

        var imageUrls: Array<String>? = intent?.getStringArrayExtra("imageUrls")
        var images = ""
        imageUrls?.forEach {
            Log.d("ImageGallery", it)
            images += it + "; "
        }
        txt.text = images
    }

    fun handleIntent(intent: Intent?) {
        var imageUrls: Array<String>? = intent?.getStringArrayExtra("imageUrls")
        var images = ""
        imageUrls?.forEach {
            Log.d("ImageGallery", it)
            images += it + "; "
        }
        txt.text = images
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Set the new intent so getIntent() will return the latest one
        setIntent(intent)
        // Process the new intent
        intent?.let { handleIntent(it) }
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}