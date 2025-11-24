package com.oculus.sample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var time: String
    private lateinit var appWindow: View
    private lateinit var progress: ProgressBar
    private lateinit var customViewContainer: FrameLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sp: SharedPreferences
    private lateinit var mApplicationContext: Context

    private lateinit var backgroundPlayHelper: BackgroundPlayHelper
    private lateinit var bookmarkManager: BookmarkManager
    private lateinit var menuHelper: MenuHelper

    // For the snackbar with error message
    private var clickListener = View.OnClickListener {
        webView!!.loadUrl(sp.getString("homepage", "https://m.youtube.com/")!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mApplicationContext = applicationContext
        // Set HW acceleration flags
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        webView = findViewById(R.id.webView)
        appWindow = findViewById(R.id.appWindow)
        progress = findViewById(R.id.progress)
        customViewContainer = findViewById(R.id.customViewContainer)

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.bookmarks_panel)

        // Set up media button reciever
        (getSystemService(AUDIO_SERVICE) as AudioManager).registerMediaButtonEventReceiver(
            ComponentName(packageName, MediaButtonIntentReceiver::class.java.name)
        )

        // Set up WebChromeClient
        if (webView != null) {
            webView!!.webChromeClient =
                WebTubeChromeClient(webView!!, progress, customViewContainer, drawerLayout, window.decorView)
        }

        // Set up WebViewClient
        webView!!.webViewClient = WebTubeWebViewClient(
            this,
            appWindow,
            clickListener,
            findViewById(R.id.statusBarSpace),
            findViewById(R.id.menu_main)
        )

        // Set up WebView
        setUpWebview()

        // Initialize bookmarks panel
        val localWebView = webView
        if (localWebView != null) {
            bookmarkManager = BookmarkManager(this, localWebView)
            bookmarkManager.initalizeBookmarks(navigationView)
        }
        drawerLayout.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                bookmarkManager.initalizeBookmarks(navigationView)
            }

            override fun onDrawerOpened(drawerView: View) {
                bookmarkManager.initalizeBookmarks(navigationView)
            }

            override fun onDrawerClosed(drawerView: View) { /* Nothing */
            }

            override fun onDrawerStateChanged(newState: Int) { /* Nothing */
            }
        })
        if (localWebView != null) {
            navigationView.setNavigationItemSelectedListener(
                BookmarkSelectedListener(
                    this,
                    localWebView,
                    bookmarkManager,
                    drawerLayout
                )
            )
        }
        if (localWebView != null) {
            backgroundPlayHelper = BackgroundPlayHelper(mApplicationContext, localWebView)
        }

        // Menu helper
        val actionMenu = findViewById<ActionMenuView>(R.id.menu_main)
        if (localWebView != null) {
            menuHelper = MenuHelper(this, localWebView, backgroundPlayHelper, appWindow)
        }
        menuInflater.inflate(R.menu.menu_main, actionMenu.menu)
        menuHelper.setUpMenu(actionMenu, drawerLayout, findViewById(R.id.bookmarks_panel))
        actionMenu.overflowIcon = resources.getDrawable(R.drawable.ic_dots_vertical_black_24dp)

        // Load the page
        if (!loadUrlFromIntent(intent)) {
            webView!!.loadUrl(sp.getString("homepage", "https://m.youtube.com/")!!)
        }
    }

    override fun onPause() {
        super.onPause()
        if (backgroundPlayHelper.isBackgroundPlayEnabled) {
            backgroundPlayHelper.playInBackground()
        } else {
            pauseVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundPlayHelper.hideBackgroundPlaybackNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundPlayHelper.hideBackgroundPlaybackNotification()
        (getSystemService(AUDIO_SERVICE) as AudioManager).unregisterMediaButtonEventReceiver(
            ComponentName(packageName, MediaButtonIntentReceiver::class.java.name)
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        loadUrlFromIntent(intent)
    }

    private fun loadUrlFromIntent(intent: Intent): Boolean {
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            val url = intent.data.toString()
            if (url != webView!!.url) {
                webView!!.loadUrl(url)
            }
            return true
        }
        return false
    }

    fun setUpWebview() {
        // To save login info
        CookieHelper.acceptCookies(webView, true)

        // Some settings
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = false
        webSettings.allowFileAccess = false
        webSettings.databaseEnabled = true
        val cachePath = mApplicationContext
            .getDir("cache", MODE_PRIVATE).path
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webView!!.isHorizontalScrollBarEnabled = false
        webView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView!!.setBackgroundColor(Color.WHITE)
        webView!!.isScrollbarFadingEnabled = true
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            finish()
        }
    }

    companion object {
        private const val LOG_TAG = "webTube"
        private var webView: WebView? = null
        fun toggleVideo() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView!!.evaluateJavascript(
                    "(function() { return document.getElementsByTagName('video')[0].paused; })();"
                ) { value ->
                    if (value == "true") {
                        playVideo()
                    } else {
                        pauseVideo()
                    }
                }
            } else {
                pauseVideo()
            }
        }

        fun pauseVideo() {
            webView!!.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
        }

        fun playVideo() {
            webView!!.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
        }
    }
}
