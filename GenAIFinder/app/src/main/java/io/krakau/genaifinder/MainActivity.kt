package io.krakau.genaifinder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import io.krakau.genaifinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var textIntent: TextView? = null
    private var textImages: TextView? = null
    private var webview: WebView? = null
    private var searchBtn: Button? = null

    private var url: String = "https://x.com/spsth/status/1851232714399039837?t=2iKpQnarnkM3IDK8NXTVfg&s=19"
    //private var url: String = "https://pbs.twimg.com/media/GbDmULJXYAAsPBQ?format=jpg&name=small";
    //private var url: String = "https://www.spiegel.de/";
    //private var url: String = "https://www.instagram.com/andy.grote/p/DH3tiQ5MUs6/?img_index=1";
    //private var url: String = "";

    private var imageUrls: Array<String?> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // bindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        textIntent = binding.textIntent
        textImages = binding.textImages
        webview = binding.webview
        searchBtn = binding.searchBtn

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
        webview?.loadUrl(url)

        searchBtn?.setOnClickListener(View.OnClickListener {
            Log.d("BUTTONS", "User tapped the searchBtn")
            // execute js
            val javascript: String = """
            let imgTags = document.getElementsByTagName("img");
            for (let i = 0; i < imgTags.length; i++) {
                ImageAnalyser.boundMethod(imgTags[i].currentSrc, i, imgTags.length);
            }
            """.trimIndent()
            webview?.loadUrl("javascript:$javascript")
        })

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
                    textImages?.setText(images)
                })
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            Log.d("Intent", it)
            url = it
            // Update UI to reflect text being shared
            textIntent?.setText(it)
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