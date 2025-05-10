package io.krakau.genaifinder

import android.app.Dialog
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexboxLayout
import io.krakau.genaifinder.databinding.ActivityMainBinding

class ImageGalleryActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var txt: TextView

    private lateinit var scrollView: ScrollView
    private lateinit var flexboxLayout: FlexboxLayout
    private var imageUrls: Array<String>? = emptyArray()
    private var selectedImageUrl: String = "null"

    private lateinit var dialog: Dialog
    private lateinit var dialogImageView: ImageView
    private lateinit var findBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)*/
        setContentView(R.layout.activity_image_gallery)
        setSupportActionBar(findViewById(R.id.toolbar))

        txt = findViewById<TextView>(R.id.testIntent)

        scrollView = findViewById<ScrollView>(R.id.scrollview)
        flexboxLayout = findViewById<FlexboxLayout>(R.id.flexboxLayout)

        // Dialog for image url selection
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_box)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rounded_corners))
        findBtn = dialog.findViewById<Button>(R.id.findBtn)
        findBtn.setOnClickListener {
            Log.d("BUTTONS", "User tapped the findBtn")
            Log.d("DIALOG", selectedImageUrl)
            Toast.makeText(this, selectedImageUrl, Toast.LENGTH_LONG).show()
            val sendDataIntent = Intent(this@ImageGalleryActivity, FinderActivity::class.java).apply {
                //putExtra("imageUrls", filterContent(imageUrls))
            }
            startActivity(sendDataIntent)
        }

        // load image urls into flexboxLayout
        imageUrls = intent?.getStringArrayExtra("imageUrls")
        debugImageUrlTexts()
        displayImages()
    }

    private fun debugImageUrlTexts() {
        var images = ""
        imageUrls?.forEach {
            Log.d("ImageGallery", it)
            images += it + "; "
        }
        txt.text = images
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
                /*Picasso.get().load(selectedImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(dialogImageView)*/
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
            /*Picasso.get().load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(imageView)*/
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

    private fun calculateHorizontalSpacing(): Int {
        return this.resources.displayMetrics.widthPixels - ( (dpToPx(125) * 3) + (dpToPx(15) * 2) + (dpToPx(20) * 2) )
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