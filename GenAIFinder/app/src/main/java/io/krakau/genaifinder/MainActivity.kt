package io.krakau.genaifinder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.krakau.genaifinder.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import androidx.core.net.toUri
import io.krakau.genaifinder.picassso.Base64RequestHandler

class MainActivity : AppCompatActivity() {

    private lateinit var picasso: Picasso

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // tasks
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var mainScope: CoroutineScope
    private lateinit var mJob: Job

    // bindings
    private var previewImage: ImageView? = null
    private var previewTitle: TextView? = null
    private var previewDescription: TextView? = null
    private var previewUrl: TextView? = null
    private var textIntent: TextView? = null
    private var textImages: TextView? = null
    private var discoverBtn: Button? = null
    private var webview: WebView? = null

    // data
    private var imageUrls: Array<String?> = emptyArray()

    private var url: String = "https://x.com/spsth/status/1851232714399039837?t=2iKpQnarnkM3IDK8NXTVfg&s=19"
    //private var url: String = "https://pbs.twimg.com/media/GbDmULJXYAAsPBQ?format=jpg&name=small";
    //private var url: String = "https://www.spiegel.de/";
    //private var url: String = "https://www.instagram.com/andy.grote/p/DH3tiQ5MUs6/?img_index=1";
    //private var url: String = "https://www.tagesschau.de/"
    //private var url: String = "https://web.de/"
    //private var url: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Picasso with SVG and Base64 support
        picasso = Picasso.Builder(this)
            //.addRequestHandler(SvgRequestHandler())
            .addRequestHandler(Base64RequestHandler())
            .build()
            // Set this instance as the singleton
        Picasso.setSingletonInstance(picasso)

        // bindings
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

        coroutineScope = CoroutineScope(Dispatchers.IO)
        mainScope = CoroutineScope(Dispatchers.Main)

        // check intent
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                }
            }
        }

        //webview?.setVisibility(View.GONE);
        webview?.settings?.javaScriptEnabled = true
        webview?.settings?.domStorageEnabled = true
        webview?.addJavascriptInterface(MyJavaScriptInterface(this),"ImageAnalyser")
        webview?.webViewClient = object : WebViewClient()  {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
            private fun loadScript(javascript: String) {
                webview?.loadUrl("javascript:$javascript")
            }
            private fun getImages(): String {
                return """
                    let imgTags = document.getElementsByTagName("img");
                    for (let i = 0; i < imgTags.length; i++) {
                        ImageAnalyser.boundMethod(imgTags[i].currentSrc, i, imgTags.length);
                    }
                    """.trimIndent()
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

        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/
    }

    private inner class MyJavaScriptInterface(private val ctx: Context) {
        @JavascriptInterface
        fun boundMethod(imageSrc: String, i: Int, size: Int) {
            if (i == 0) {
                imageUrls = arrayOfNulls(size)
            }
            imageUrls[i] = imageSrc
            if (i == size - 1) {
                runOnUiThread(Runnable {
                    var images = ""
                    for (imageUrl in imageUrls) {
                        Log.d("imageUrls", imageUrl!!)
                        images += "$imageUrl; "
                    }
                    // Stuff that updates the UI
                    textImages?.text = images

                    val sendDataIntent = Intent(this@MainActivity, ImageGalleryActivity::class.java).apply {
                        //putExtra("imageUrls", imageUrls)
                        putExtra("imageUrls", filterContent(imageUrls))
                    }
                    /* If set, and the activity being launched is already running in the current task,
                    then instead of launching a new instance of that activity, all of the other
                    activities on top of it will be closed and this Intent will be delivered
                    to the (now on top) old activity as a new Intent. */
                    sendDataIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(sendDataIntent)
                })
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
            discoverBtn?.isEnabled = true
            startParsing()
        }
    }


    private fun startParsing() {
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
            .followRedirects(true)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
            .get()

        // Get metadata from Open Graph tags or regular HTML tags
        var title = doc.select("meta[property=og:title]").attr("content").ifEmpty {
            doc.title()
        }
        if(title.equals("")) {
            title = "Titleless"
        }

        var description = doc.select("meta[property=og:description]").attr("content").ifEmpty {
            doc.select("meta[name=description]").attr("content") ?: ""
        }
        if(description.equals("")) {
            description = "Descriptionless"
        }

        var imageUrl = doc.select("meta[property=og:image]").attr("content").ifEmpty {
            // Try to find first image as fallback
            doc.select("img").firstOrNull()?.attr("src") ?: ""
        }
        var uri = imageUrl.toUri()
        if(imageUrl.contains("data:image") && imageUrl.contains("base64")) {
            uri = Uri.parse("${Base64RequestHandler.SCHEME_BASE64}://$imageUrl")
            Log.d("OG:IMAGEURL:BASE64", imageUrl)
        } else {
            if(uri.host == null) {
                imageUrl = "https://" + url.toUri().authority + imageUrl
            }
        }

        Log.d("OG:IMAGEURL", uri.toString())
        Log.d("OG:IMAGEURLAF", imageUrl)

        runOnUiThread(Runnable {
            picasso.load(uri)
                .placeholder(R.drawable.placeholder_image)
                .into(previewImage)
            previewTitle?.text = title
            previewDescription?.text = description
            previewUrl?.text = url
        })



        Log.d("JSOUP", title)
        Log.d("JSOUP", description)
        Log.d("JSOUP", imageUrl)

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