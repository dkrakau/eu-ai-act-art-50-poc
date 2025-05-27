package io.krakau.genaifinder

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexboxLayout

class ImageGalleryActivity : AppCompatActivity() {

    // Constants
    private val LOG_IMAGE_GALLERY_ACTIVITY: String = "ImageGalleryActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // Shared preferences via data manager
    private val SHARED_PREFS_KEY = "genaifinder_shared_preferences"
    private lateinit var dataManager: DataManager

    // View variables
    private lateinit var scrollView: ScrollView
    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var dialog: Dialog
    private lateinit var dialogImageView: ImageView
    private lateinit var findBtn: Button

    // Data
    private var imageUrls: List<String>? = emptyList()
    private var selectedImageUrl: String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load view from xml
        setContentView(R.layout.activity_image_gallery)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Pass shared preferences to data manager
        dataManager = DataManager(getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE))
        // Get image urls
        imageUrls = dataManager.getImageUrls().toList()

        // Bindings
        scrollView = findViewById<ScrollView>(R.id.scrollview)
        flexboxLayout = findViewById<FlexboxLayout>(R.id.flexboxLayout)

        /*
         * View manipulation
         */
        // Load image urls into flexboxLayout
        displayImages()

        // Dialog for image url selection
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_box)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rounded_corners))
        findBtn = dialog.findViewById<Button>(R.id.findBtn)
        findBtn.setOnClickListener {
            Log.d(LOG_IMAGE_GALLERY_ACTIVITY,"BUTTONS: User tapped the findBtn")
            Log.d(LOG_IMAGE_GALLERY_ACTIVITY,"DIALOG: $selectedImageUrl")
            dataManager.setInputImageUrl(selectedImageUrl)
            startActivity(Intent(this@ImageGalleryActivity, FinderActivity::class.java))
        }
    }

    private fun displayImages() {
        val layoutParams = LinearLayout.LayoutParams(
            dpToPx(125), // width
            dpToPx(125)  // height
        )
        layoutParams.setMargins(0, 0, 0, dpToPx(10))

        imageUrls?.forEachIndexed { index, imageUrl ->
            val imageCardView = CardView(this)
            imageCardView.radius = this.resources.displayMetrics.density * (15f) // radius in dp
            imageCardView.setOnClickListener {
                selectedImageUrl = imageUrls?.get(index)!!
                dialogImageView = dialog.findViewById<ImageView>(R.id.dialogImageView)
                Glide.with(this)
                    .load(selectedImageUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
                    .into(dialogImageView)
                dialog.show()
            }
            val imageView = ImageView(this)
            imageView.adjustViewBounds = true
            val imageLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            imageView.layoutParams = imageLayoutParams
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
                .into(imageView)
            imageCardView.addView(imageView, layoutParams)
            flexboxLayout.addView(imageCardView, layoutParams)
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
                startActivity(Intent(this@ImageGalleryActivity, SettingsActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, ImageGalleryActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@ImageGalleryActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, ImageGalleryActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}