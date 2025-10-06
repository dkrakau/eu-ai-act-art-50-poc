package io.krakau.genaifinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import io.krakau.genaifinder.preferences.DataManager
import io.krakau.genaifinder.service.api.model.view.ApiViewModel
import io.krakau.genaifinder.service.api.model.view.ApiViewModelFactory
import java.math.BigDecimal
import java.math.RoundingMode

class InsightsActivity : AppCompatActivity() {

    // Constants
    private val LOG_INSIGHTS_ACTIVITY: String = "InsightsActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // Shared preferences via data manager
    private val SHARED_PREFS_KEY = "genaifinder_shared_preferences"
    private lateinit var dataManager: DataManager

    // View variables
    private lateinit var insightBack: ImageView
    private lateinit var insightsSimularityTextView: TextView
    private lateinit var inputAssetImageView: ImageView
    private lateinit var selectedAssetImageView: ImageView
    private lateinit var contentCodeLinearLayout: LinearLayout

    // Data
    private lateinit var viewModel: ApiViewModel
    private lateinit var inputAssetUrl: String
    private lateinit var inputAssetContentCode: String
    private lateinit var selectedAssetFilename: String
    private lateinit var selectedAssetProvider: String
    private lateinit var selectedAssetContentCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load view from xml
        setContentView(R.layout.activity_insights)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Pass shared preferences to data manager
        dataManager = DataManager(getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE))
        inputAssetUrl = dataManager.getInputImageUrl()
        inputAssetContentCode = dataManager.getInputImageContentCode()
        selectedAssetFilename = dataManager.getSelectedImageFilename()
        selectedAssetProvider = dataManager.getSelectedImageProvider()
        selectedAssetContentCode = dataManager.getSelectedImageContentCode()
        Log.d(LOG_INSIGHTS_ACTIVITY, "inputAssetUrl: $inputAssetUrl")
        Log.d(LOG_INSIGHTS_ACTIVITY, "inputAssetUrl: $inputAssetContentCode")
        Log.d(LOG_INSIGHTS_ACTIVITY, "selectedAssetFilename: $selectedAssetFilename")
        Log.d(LOG_INSIGHTS_ACTIVITY, "selectedAssetProvider: $selectedAssetProvider")
        Log.d(LOG_INSIGHTS_ACTIVITY, "selectedAssetContentCode: $selectedAssetContentCode")

        // Bindings
        insightBack = findViewById<ImageView>(R.id.insightBack)
        inputAssetImageView = findViewById<ImageView>(R.id.inputAssetImageView)
        selectedAssetImageView = findViewById<ImageView>(R.id.selectedAssetImageView)
        contentCodeLinearLayout = findViewById<LinearLayout>(R.id.contentCodeLinearLayout)
        insightsSimularityTextView = findViewById<TextView>(R.id.insightsSimularityTextView)

        /*
         * View manipulation
         */
        insightBack.setOnClickListener {
            startActivity(Intent(this@InsightsActivity, FinderActivity::class.java))
        }
        insightsSimularityTextView.text = "" + calculateSimilarity() + "% similar"

        Glide.with(this)
            .load(inputAssetUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(inputAssetImageView)

        renderContentCode()

        /*
         * Fetching data
         */
        val factory = ApiViewModelFactory(
            dataManager.stringToList(dataManager.getServerProviderList()),
            dataManager.stringToList(dataManager.getServerUrlsList()))
        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]
        // Observe the LiveData
        viewModel.imageResource.observe(this) { bitmap ->
            // Update UI with the resource
            Log.d(LOG_INSIGHTS_ACTIVITY, "bitmap byteCount: " + bitmap.byteCount)
            selectedAssetImageView.setImageBitmap(bitmap)
        }
        viewModel.fetchGetImageResource(selectedAssetProvider, selectedAssetFilename)
    }

    private fun calculateSimilarity(): BigDecimal {
        var distance = 0
        for (i in inputAssetContentCode.indices) {
            if(i < 64 && inputAssetContentCode[i] != selectedAssetContentCode[i]) {
                distance++
            }
        }
        return (100.0 - (distance * 100.0 / 64.0)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
    }

    private fun renderContentCode() {
        val steps = 8
        var stepsCounter = steps
        var i = 0
        while(i < 64) {
            var flexboxLayout = FlexboxLayout(this)
            flexboxLayout.flexDirection = FlexDirection.ROW
            flexboxLayout.flexWrap = FlexWrap.NOWRAP
            flexboxLayout.justifyContent = JustifyContent.CENTER
            while(i < stepsCounter) {
                val cell = LinearLayout(this)
                cell.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                if(inputAssetContentCode[i] == selectedAssetContentCode[i]) {
                    cell.setBackgroundResource(R.drawable.border_box_primary)
                } else {
                    cell.setBackgroundResource(R.drawable.border_box_secondary)
                }
                flexboxLayout.addView(cell)
                i++
            }
            contentCodeLinearLayout.addView(flexboxLayout)
            stepsCounter += steps
        }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
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