package io.krakau.genaifinder

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import io.krakau.genaifinder.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.krakau.genaifinder.preferences.DataManager

class MainActivity : AppCompatActivity() {

    // Constants
    private val LOG_MAIN_ACTIVITY: String = "MainActivity"
    private val CALLING_ACTIVITY: String = "callingActivity"

    // Shared preferences via data manager
    private val SHARED_PREFS_KEY = "genaifinder_shared_preferences"
    private lateinit var dataManager: DataManager
    /*private lateinit var dataRepository: DataRepository*/

    // View variables
    private lateinit var binding: ActivityMainBinding
    private var previewImage: ImageView? = null
    private var previewTitle: TextView? = null
    private var previewDescription: TextView? = null
    private var previewUrl: TextView? = null
    private var textIntent: TextView? = null
    private var textImages: TextView? = null
    private var discoverBtn: Button? = null
    private var webview: WebView? = null

    // Tasks
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var mainScope: CoroutineScope
    private lateinit var mJob: Job

    // Data
    private var imageUrls: Array<String?> = emptyArray()
    private var url: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pass shared preferences to data manager
        dataManager = DataManager(getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE))
        //dataManager.clearPrefs() // Clear all stored information in shared preferences
        // Check if app is using night mode resources
        dataManager.setDarkMode(initDarkMode())
        // Set server provider list string
        dataManager.setServerProviderList(dataManager.arrayToString(this.resources.getStringArray(R.array.serverProvider)))
        // Set server urls list string
        dataManager.setServerUrlsList(dataManager.arrayToString(this.resources.getStringArray(R.array.serverUrls)))

        /*dataRepository = DataRepository(this)
        val serverProviderList = this.resources.getStringArray(R.array.serverProvider)
        val serverUrlList = this.resources.getStringArray(R.array.serverUrls)
        lifecycleScope.launch {
            dataRepository.setDarkMode(isUsingNightModeResources())
            dataRepository.setServerProviderList(dataRepository.arrayToString(serverProviderList))
            dataRepository.setServerUrlsList(dataRepository.arrayToString(serverUrlList))
        }*/

        // Bindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        previewImage = binding.previewImage
        previewTitle = binding.previewTitle
        previewDescription = binding.previewDescription
        previewUrl = binding.previewUrl
        textIntent = binding.textIntent
        textImages = binding.textImages
        discoverBtn = binding.discoverBtn
        webview = binding.webview

        // Tasks
        coroutineScope = CoroutineScope(Dispatchers.IO)
        mainScope = CoroutineScope(Dispatchers.Main)

        // Check intent
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                }
            }
        }

        /*
         * View manipulation
         */
        // Load dark mode settings
        if (dataManager.getDarkMode() == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if(dataManager.getDarkMode() == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        discoverBtn?.isEnabled = false
        webview?.visibility = View.GONE;

        webview?.settings?.javaScriptEnabled = true
        webview?.settings?.domStorageEnabled = true
        webview?.addJavascriptInterface(MyJavaScriptInterface(this),"ImageAnalyser")
        webview?.webViewClient = object : WebViewClient()  {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WEBVIEW", webview!!.url!!)
                // ###
                // ChromeWebDriver will redirect to url which jsoup can handle
                // ###
                startParsing(webview!!.url!!)
            }
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url == null) {
                    return true
                }
                if (!TextUtils.isEmpty(request.url.host) && request.url.toString().startsWith("fb://")) {
                    return true
                } else if (!TextUtils.isEmpty(request.url.host) && request.url.toString().startsWith("instagram://")) {
                    return true
                } else {
                    view.loadUrl(request.url.toString())
                }
                return true
            }
        }

        discoverBtn?.setOnClickListener {
            Log.d("BUTTONS", "User tapped the discoverBtn")
            // execute js
            val javascript: String = """
            let imgTags = document.getElementsByTagName("img");
            for (let i = 0; i < imgTags.length; i++) {
                ImageAnalyser.boundMethod(imgTags[i].currentSrc, i, imgTags.length);
            }
            """.trimIndent()
            webview?.loadUrl("javascript:$javascript")
        }
    }

    private inner class MyJavaScriptInterface(private val ctx: Context) {
        @JavascriptInterface
        fun boundMethod(imageSrc: String, i: Int, size: Int) {
            if (i == 0) {
                imageUrls = arrayOfNulls(size)
            }
            imageUrls[i] = imageSrc
            if (i == size - 1) {
                runOnUiThread {
                    var images = ""
                    for (imageUrl in imageUrls) {
                        Log.d("imageUrls", imageUrl!!)
                        images += "$imageUrl; "
                    }
                    // Update the UI
                    textImages?.text = images
                    // Add data to shared preferences
                    dataManager.setImageUrls(filterContent(imageUrls).toList())
                    // Start activity
                    startActivity(Intent(this@MainActivity, ImageGalleryActivity::class.java))
                }
            }
        }
    }

    private fun filterContent(imageUrls: Array<String?>): Array<String> {
        var result: Array<String> = emptyArray()
        imageUrls.forEach { url ->
            if (
                    url != null
                    && url != ""
                    //&& !url.contains("svg")
                ) {
                result = result.plus(url)
            }
        }
        return result
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // clear imageUrls
            imageUrls = emptyArray()
            Log.d("Intent", it)
            url = it
            // load url into webview
            webview?.loadUrl(url)
            // Update UI to reflect text being shared
            textIntent?.setText(it)
        }
    }


    private fun startParsing(url: String) {
        cancelJob()
        mJob = coroutineScope.launch {
            extractUrlData(url)
        }
    }

    private fun cancelJob() {
        if (::mJob.isInitialized) {
            mJob.cancel()
        }
    }

    // Extract preview data from the URL
    private fun extractUrlData(url: String) {

        var doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
            .ignoreHttpErrors(true)
            .timeout(2000)
            .followRedirects(true)
            .get()

        // Get metadata from Open Graph tags or regular HTML tags
        var title = doc.select("meta[property=og:title]").attr("content").ifEmpty {
            doc.title()
        }
        if(title.equals("")) {
            title = "No title provided"
        }

        var description = doc.select("meta[property=og:description]").attr("content").ifEmpty {
            doc.select("meta[name=description]").attr("content") ?: ""
        }
        if(description.equals("")) {
            description = "No description provided"
        }

        var imageUrl = doc.select("meta[property=og:image]").attr("content").ifEmpty {
            // Try to find first image as fallback
            doc.select("img").firstOrNull()?.attr("src") ?: ""
        }
        var uri = imageUrl.toUri()
        if(imageUrl.contains("data:image") && imageUrl.contains("base64")) {
            uri = "base64://$imageUrl".toUri()
            Log.d(LOG_MAIN_ACTIVITY,"OG:IMAGEURL:BASE64 $imageUrl")
        } else {
            if(uri.host == null) {
                imageUrl = "https://" + url.toUri().authority + imageUrl
            }
        }

        Log.d(LOG_MAIN_ACTIVITY, "extractUrlData: $url")
        Log.d(LOG_MAIN_ACTIVITY,"OG:IMAGEURL " + uri.toString())
        Log.d(LOG_MAIN_ACTIVITY,"OG:IMAGEURLAF $imageUrl")

        runOnUiThread {
            Log.d("GLIDE", imageUrl)
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error))
                .into(previewImage!!)
            previewTitle?.text = title
            previewDescription?.text = description
            previewUrl?.text = url
            discoverBtn?.isEnabled = true
        }

        Log.d(LOG_MAIN_ACTIVITY,"JSOUP $title")
        Log.d(LOG_MAIN_ACTIVITY,"JSOUP $description")
        Log.d(LOG_MAIN_ACTIVITY,"JSOUP $imageUrl")

    }

    private fun initDarkMode(): Int {
        return if(dataManager.getDarkMode() == -1) isUsingNightModeResources() else dataManager.getDarkMode()
    }
    private fun isUsingNightModeResources(): Int {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> 1
            Configuration.UI_MODE_NIGHT_NO -> 0
            Configuration.UI_MODE_NIGHT_UNDEFINED -> -1
            else -> -1
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Set the new intent so getIntent() will return the latest one
        setIntent(intent)
        // Process the new intent
        intent?.let { handleSendText(it) }
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
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, MainActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                /* If set setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), and the activity being
                    launched is already running in the current task, then instead of launching
                    a new instance of that activity, all of the other activities on top of it
                    will be closed and this Intent will be delivered to the (now on top) old
                    activity as a new Intent. */
                true
            }
            R.id.action_information -> {
                startActivity(Intent(this@MainActivity, InformationActivity::class.java).apply {
                    putExtra(CALLING_ACTIVITY, MainActivity::class.java.name)
                }.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}