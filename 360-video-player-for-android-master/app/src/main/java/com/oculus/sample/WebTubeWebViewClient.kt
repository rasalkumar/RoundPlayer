package com.oculus.sample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class WebTubeWebViewClient(
    private val context: Context,
    private val appWindow: View,
    private val clickListener: View.OnClickListener,
    private val statusBarSpace: View,
    private val bottomBar: View
) : WebViewClient() {

    // Open links in a browser window (except for sign-in dialogs and YouTube URLs)
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("http") && !url.contains("accounts.google.") && !url.contains("youtube.")) {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            return true
        }
        return false
    }

    override fun onLoadResource(view: WebView, url: String) {
        if (!url.contains(".jpg") && !url.contains(".ico") && !url.contains(".css") && !url.contains(".js") && !url.contains("complete/search")) {
            // Remove all iframes (to prevent WebRTC exploits)
            view.loadUrl(
                "javascript:(function() {" +
                        "var iframes = document.getElementsByTagName('iframe');" +
                        "for(i=0;i<=iframes.length;i++){" +
                        "if(typeof iframes[0] != 'undefined')" +
                        "iframes[0].outerHTML = '';" +
                        "}})()"
            )

            // Gets rid of orange outlines
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val css = "*, *:focus { " +
                        " outline: none !important; -webkit-tap-highlight-color: rgba(255,255,255,0) !important; -webkit-tap-highlight-color: transparent !important; }" +
                        " ._mfd { padding-top: 2px !important; } "
                view.loadUrl(
                    "javascript:(function() {" +
                            "if(document.getElementById('webTubeStyle') == null){" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "var style = document.createElement('style');" +
                            "style.id = 'webTubeStyle';" +
                            "style.type = 'text/css';" +
                            "style.innerHTML = '" + css + "';" +
                            "parent.appendChild(style);" +
                            "}})()"
                )
            }

            // To adapt the statusbar color
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                statusBarSpace.visibility = View.VISIBLE
                view.evaluateJavascript(
                    "(function() { if(document.getElementById('player').style.visibility == 'hidden' || document.getElementById('player').innerHTML == '') { return 'not_video'; } else { return 'video'; } })();"
                ) { value ->
                    val colorId = if (value.contains("not_video")) R.color.colorPrimary else R.color.colorWatch
                    statusBarSpace.setBackgroundColor(ContextCompat.getColor(context, colorId))
                    bottomBar.setBackgroundColor(ContextCompat.getColor(context, colorId))
                }
            }
        }
    }

    // Deal with error messages
    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        if (description.contains("NETWORK_CHANGED")) {
            view.loadUrl(PreferenceManager.getDefaultSharedPreferences(view.context).getString("homepage", "https://m.youtube.com/")!!)
        } else if (description.contains("NAME_NOT_RESOLVED")) {
            Snackbar.make(appWindow, context.getString(R.string.errorNoInternet), Snackbar.LENGTH_INDEFINITE)
                .setAction(context.getString(R.string.refresh), clickListener).show()
        } else if (description.contains("PROXY_CONNECTION_FAILED")) {
            Snackbar.make(appWindow, context.getString(R.string.errorTor), Snackbar.LENGTH_INDEFINITE)
                .setAction(context.getString(R.string.refresh), clickListener).show()
        } else {
            Snackbar.make(appWindow, context.getString(R.string.error) + " " + description, Snackbar.LENGTH_INDEFINITE)
                .setAction(context.getString(R.string.refresh), clickListener).show()
        }
    }
}
