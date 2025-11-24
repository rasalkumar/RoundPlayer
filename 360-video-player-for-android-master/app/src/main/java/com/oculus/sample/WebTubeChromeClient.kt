package com.oculus.sample

import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.drawerlayout.widget.DrawerLayout

class WebTubeChromeClient(
    private val webView: WebView,
    private val progress: ProgressBar,
    private val customViewContainer: FrameLayout,
    private val drawerLayout: DrawerLayout,
    private val decorView: View
) : WebChromeClient() {

    private var mCustomView: View? = null

    // Fullscreen playback
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        mCustomView = view
        webView.loadUrl("javascript:(function() { document.body.style.overflowX = 'hidden'; })();")
        webView.loadUrl("javascript:(function() { window.scrollTo(0, 0); })();")
        drawerLayout.visibility = View.GONE
        customViewContainer.visibility = View.VISIBLE
        customViewContainer.addView(view)

        // Hide the status bar.
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        if (mCustomView == null) return
        webView.loadUrl("javascript:(function() { window.scrollTo(0, 0); })();")
        webView.loadUrl("javascript:(function() { document.body.style.overflowX = 'scroll'; })();")
        drawerLayout.visibility = View.VISIBLE
        customViewContainer.visibility = View.GONE
        mCustomView!!.visibility = View.GONE
        customViewContainer.removeView(mCustomView)
        mCustomView = null

        // Show the status bar.
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    // Progressbar
    override fun onProgressChanged(view: WebView, percentage: Int) {
        progress.visibility = View.VISIBLE
        progress.progress = percentage

        // For more advnaced loading status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progress.isIndeterminate = percentage == 100
            view.evaluateJavascript(
                "(function() { return document.readyState == \"complete\"; })();"
            ) { value ->
                if (value == "true") {
                    progress.visibility = View.INVISIBLE
                } else {
                    onProgressChanged(webView, 100)
                }
            }
        } else {
            if (percentage == 100) {
                progress.visibility = View.GONE
            }
        }
    }
}
