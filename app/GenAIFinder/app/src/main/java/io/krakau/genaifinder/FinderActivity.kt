package io.krakau.genaifinder

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.krakau.genaifinder.service.api.model.data.Asset
import io.krakau.genaifinder.service.api.model.view.AssetViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.core.graphics.toColorInt
import java.math.BigDecimal
import java.math.RoundingMode


class FinderActivity : AppCompatActivity() {

    // constants
    private val LOG_FINDER_ACTIVITY: String = "FinderActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // view variables
    private lateinit var finderLinearLayout: LinearLayout
    private lateinit var itemImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var simularityTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var originTagCardView: CardView
    private lateinit var originTagBackgroundLayout: LinearLayout
    private lateinit var originTagTextView: TextView
    private lateinit var resultLinearLayout: LinearLayout
    private lateinit var loadingConstraintLayout: ConstraintLayout
    private lateinit var loadingImageView: ImageView

    private lateinit var selectedImageUrl: String

    private lateinit var viewModel: AssetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finder)
        setSupportActionBar(findViewById(R.id.toolbar))

        // bindings
        finderLinearLayout = findViewById<LinearLayout>(R.id.finderLinearLayout)
        itemImageView = findViewById<ImageView>(R.id.itemImageView)
        titleTextView = findViewById<TextView>(R.id.titleTextView)
        descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        simularityTextView = findViewById<TextView>(R.id.simularityTextView)
        dateTextView = findViewById<TextView>(R.id.dateTextView)
        timeTextView = findViewById<TextView>(R.id.timeTextView)
        originTagCardView = findViewById<CardView>(R.id.originTagCardView)
        originTagBackgroundLayout = findViewById<LinearLayout>(R.id.originTagBackgroundLayout)
        originTagTextView = findViewById<TextView>(R.id.originTagTextView)
        resultLinearLayout = findViewById<LinearLayout>(R.id.resultLinearLayout)
        loadingConstraintLayout = findViewById<ConstraintLayout>(R.id.loadingConstraintLayout)
        loadingImageView = findViewById<ImageView>(R.id.loadingImageView)

        loadingConstraintLayout.visibility = View.VISIBLE;
        finderLinearLayout.visibility = View.GONE;

        val rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.animation_rotate)
        loadingImageView.animation = rotateAnimation
        loadingImageView.animation.start()

        selectedImageUrl = intent.getStringExtra("selectedImageUrl")!!
        Log.d(LOG_FINDER_ACTIVITY, "SelectedImageUrl: $selectedImageUrl")

        viewModel = ViewModelProvider(this).get(AssetViewModel::class.java)
        // Observe the LiveData
        viewModel.assets.observe(this) { assets ->
            // Update UI with the list of assets
            renderInputAsset(assets[0])
            if(assets.size > 1) {
                renderFoundAssets(assets.subList(1, assets.size))
            }
            loadingConstraintLayout.visibility = View.GONE;
            finderLinearLayout.visibility = View.VISIBLE;
        }
        // Fetch assets data
        viewModel.fetchImageAssets(selectedImageUrl)

    }

    private fun renderInputAsset(inputAsset: Asset) {
        val metadata = inputAsset.metadata
        val provider = inputAsset.metadata.provider
        val iscc = inputAsset.metadata.iscc

        var thumbnail = inputAsset.metadata.iscc.data.thumbnail
        var title = inputAsset.metadata.iscc.data.filename
        var description = inputAsset.metadata.iscc.data.name

        var currentTimestamp = System.currentTimeMillis()

        Glide.with(this)
            .load(thumbnail)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(itemImageView)
        titleTextView.text = selectedImageUrl.substring(8)
        descriptionTextView.text = title
        simularityTextView.text = ""
        dateTextView.text = getDate(currentTimestamp)
        timeTextView.text = getTime(currentTimestamp)
        originTagTextView.text = "Unknown"
        originTagTextView.setTextColor(getTagTextColor("Unknown"))
        originTagBackgroundLayout.setBackgroundColor(getTagBackgroundColor("Unknown"))
    }

    private fun renderFoundAssets(assets: List<Asset>) {
        for (i in assets.indices) {

            val metadata = assets[i].metadata
            val provider = assets[i].metadata.provider
            val iscc = assets[i].metadata.iscc
            val distance = assets[i].distance

            var thumbnail = assets[i].metadata.iscc.data.thumbnail
            var title = assets[i].metadata.iscc.data.filename
            var description = assets[i].metadata.iscc.data.name

            var listItem = createListItem(
                    thumbnail,
                    title,
                    description,
                "${calculateSimularity(distance)}% simular",
                    provider.name,
                    getDate(provider.timestamp),
                    getTime(provider.timestamp),
                )
            listItem.setOnClickListener {
                Log.d(LOG_FINDER_ACTIVITY, "BUTTONS: User tapped item in list")
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@FinderActivity, InsightActivity::class.java).apply {
                    //putExtra(CALLING_ACTIVITY, FinderActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }

            if(i == assets.size - 1) {
                listItem.background = null
            }

            resultLinearLayout.addView(listItem)
        }
    }

    private fun calculateSimularity(distance: Int): BigDecimal {
        return (100.0 - (distance * 100.0 / 64.0)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
    }

    fun createListItem(imageData: String, title: String, description: String, simularity: String, origin: String, date: String, time: String): LinearLayout {
        // Load LinearLayout for items
        val inflater = LayoutInflater.from(this) // or getLayoutInflater()
        var listItemLinearLayout = inflater.inflate(R.layout.content_list_item, resultLinearLayout, false) as LinearLayout
        // Find elements within LinearLayout by id
        var itemImageView = listItemLinearLayout.findViewById<ImageView>(R.id.itemImageView)
        var titleTextView = listItemLinearLayout.findViewById<TextView>(R.id.titleTextView)
        var descriptionTextView = listItemLinearLayout.findViewById<TextView>(R.id.descriptionTextView)
        var simularityTextView = listItemLinearLayout.findViewById<TextView>(R.id.simularityTextView)
        var dateTextView = listItemLinearLayout.findViewById<TextView>(R.id.dateTextView)
        var timeTextView = listItemLinearLayout.findViewById<TextView>(R.id.timeTextView)
        var originTagCardView = listItemLinearLayout.findViewById<CardView>(R.id.originTagCardView)
        var originTagBackgroundLayout = listItemLinearLayout.findViewById<LinearLayout>(R.id.originTagBackgroundLayout)
        var originTagTextView = listItemLinearLayout.findViewById<TextView>(R.id.originTagTextView)

        // Set values
        /*Picasso.get().load(imageData)
                    .placeholder(R.drawable.placeholder_image)
                    .into(itemImageView)*/
        Glide.with(this)
            .load(imageData)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
            .into(itemImageView)
        titleTextView.text = title
        descriptionTextView.text = description
        simularityTextView.text = simularity

        dateTextView.hint = date
        timeTextView.hint = time

        originTagTextView.text = origin
        originTagTextView.setTextColor(getTagTextColor(origin))
        originTagBackgroundLayout.setBackgroundColor(getTagBackgroundColor(origin))

        return listItemLinearLayout
    }

    fun getDate(currentTime: Long): String {
        val instant = Instant.ofEpochMilli(currentTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateTime.format(formatter)
    }

    fun getTime(currentTime: Long): String {
        val instant = Instant.ofEpochMilli(currentTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return dateTime.format(formatter)
    }

    fun getTagTextColor(provider: String): Int {
        return when (provider) {
            "Unknown" -> "#ffffff".toColorInt()
            "OpenAI" -> "#ffffff".toColorInt()
            "LeonardoAI"-> "#ffffff".toColorInt()
            else -> "#ffffff".toColorInt()
        }
    }

    fun getTagBackgroundColor(provider: String): Int {
        return when (provider) {
            "Unknown" -> "#636363".toColorInt()
            "OpenAI" -> "#0F9E7B".toColorInt()
            "LeonardoAI"-> "#3A1059".toColorInt()
            else -> "#707070".toColorInt()
        }
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
                startActivity(Intent(this@FinderActivity, SettingsActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, FinderActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@FinderActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, FinderActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}