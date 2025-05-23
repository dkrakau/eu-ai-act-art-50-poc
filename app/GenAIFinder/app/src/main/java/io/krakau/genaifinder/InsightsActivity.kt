package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.krakau.genaifinder.service.api.model.view.ApiViewModel

class InsightsActivity : AppCompatActivity() {

    // constants
    private val LOG_INSIGHTS_ACTIVITY: String = "InsightsActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"
    private val API_IMAGE_RESOURCE_ENDPOINT: String ="http://10.35.1.167/resources/images"

    private lateinit var insightBack: ImageView
    private lateinit var inputAssetImageView: ImageView
    private lateinit var selectedAssetImageView: ImageView

    private lateinit var viewModel: ApiViewModel

    private lateinit var inputAssetUrl: String
    private lateinit var inputAssetContentCode: String
    private lateinit var selectedAssetFilename: String
    private lateinit var selectedAssetContentCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)
        setSupportActionBar(findViewById(R.id.toolbar))

        insightBack = findViewById<ImageView>(R.id.insightBack)
        inputAssetImageView = findViewById<ImageView>(R.id.inputAssetImageView)
        selectedAssetImageView = findViewById<ImageView>(R.id.selectedAssetImageView)

        insightBack.setOnClickListener {
            startActivity(Intent(this@InsightsActivity, FinderActivity::class.java))
        }

        inputAssetUrl = intent.getStringExtra("inputAssetUrl")!!
        inputAssetContentCode = intent.getStringExtra("inputAssetContentCode")!!
        selectedAssetFilename = intent.getStringExtra("selectedAssetFilename")!!
        selectedAssetContentCode = intent.getStringExtra("selectedAssetContentCode")!!

        Log.d(LOG_INSIGHTS_ACTIVITY, "inputAssetUrl: $inputAssetUrl")
        Log.d(LOG_INSIGHTS_ACTIVITY, "inputAssetUrl: $inputAssetContentCode")
        Log.d(LOG_INSIGHTS_ACTIVITY, "selectedAssetFilename: $selectedAssetFilename")
        Log.d(LOG_INSIGHTS_ACTIVITY, "selectedAssetContentCode: $selectedAssetContentCode")

        Glide.with(this)
            .load(inputAssetUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(inputAssetImageView)

        viewModel = ViewModelProvider(this).get(ApiViewModel::class.java)
        // Observe the LiveData
        viewModel.imageResource.observe(this) { bitmap ->
            // Update UI with the resource
            Log.d(LOG_INSIGHTS_ACTIVITY, "bitmap byteCount: " + bitmap.byteCount)
            selectedAssetImageView.setImageBitmap(bitmap)
        }
        viewModel.fetchGetImageResource(selectedAssetFilename)


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
                startActivity(Intent(this@InsightsActivity, SettingsActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, InsightsActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@InsightsActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, InsightsActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}