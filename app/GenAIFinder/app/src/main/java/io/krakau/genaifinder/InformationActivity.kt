package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.net.toUri


class InformationActivity  : AppCompatActivity() {

    // constants
    private val LOG_INFORMATION_ACTIVITY: String = "InformationActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    private lateinit var informationBack: ImageView
    private lateinit var fabReddit: FloatingActionButton
    private lateinit var fabFacebook: FloatingActionButton
    private lateinit var fabInstagram: FloatingActionButton
    private lateinit var fabTikTok: FloatingActionButton
    private lateinit var fabX: FloatingActionButton

    private lateinit var callingActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        setSupportActionBar(findViewById(R.id.toolbar))

        informationBack = findViewById(R.id.informationBack)
        fabReddit = findViewById(R.id.fabReddit)
        fabFacebook = findViewById(R.id.fabFacebook)
        fabInstagram = findViewById(R.id.fabInstagram)
        fabTikTok = findViewById(R.id.fabBluesky)
        fabX = findViewById(R.id.fabX)

        callingActivity = intent.getStringExtra(CALLING_ACTIVITY)!!
        Log.d(LOG_INFORMATION_ACTIVITY, "callingActivity $callingActivity")

        informationBack.setOnClickListener {
            startActivity(goBackIntent(callingActivity))
        }
        fabReddit.setOnClickListener {
            toAnotherAppOpen("https://www.reddit.com/user/genaifinder", "com.reddit.android")
        }
        fabFacebook.setOnClickListener {
            toAnotherAppOpen("https://www.facebook.com/profile.php?id=61576267299269", "com.facebook.android")
        }
        fabInstagram.setOnClickListener {
            toAnotherAppOpen("https://www.instagram.com/genai.finder", "com.instagram.android")
        }
        fabTikTok.setOnClickListener {
            toAnotherAppOpen("https://bsky.app/profile/genaifinder.bsky.social", "com.bluesky.android")
        }
        fabX.setOnClickListener {
            toAnotherAppOpen("https://x.com/genaifinder", "com.x.android")
        }
    }

    private fun toAnotherAppOpen(profilePath: String, openAppPackageName: String) {
        val uri = profilePath.toUri()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage(openAppPackageName))
        } catch(e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun goBackIntent(sourceActivity: String): Intent {
        var intent = Intent(this@InformationActivity, MainActivity::class.java)
        if(sourceActivity.equals("io.krakau.genaifinder.ImageGalleryActivity")) {
            intent = Intent(this@InformationActivity, ImageGalleryActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.FinderActivity")) {
            intent = Intent(this@InformationActivity, FinderActivity::class.java)
        }
        if(sourceActivity.equals("io.krakau.genaifinder.InsightsActivity")) {
            intent = Intent(this@InformationActivity, InsightsActivity::class.java)
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
                startActivity(Intent(this@InformationActivity, SettingsActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@InformationActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, callingActivity)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}