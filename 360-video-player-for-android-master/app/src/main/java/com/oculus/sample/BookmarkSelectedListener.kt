package com.oculus.sample

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.webkit.WebView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class BookmarkSelectedListener(
    private val context: Context,
    private val webView: WebView,
    private val bookmarkManager: BookmarkManager,
    private val drawerLayout: DrawerLayout
) : NavigationView.OnNavigationItemSelectedListener {

    private var time: String = "0"

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val menuItemTitle = menuItem.title.toString()
        if (menuItemTitle == context.getString(R.string.addPage)) {
            if (webView.title != "YouTube") {
                if (webView.url!!.contains("/watch") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    time = "0"
                    webView.evaluateJavascript(
                        "(function() { return document.getElementsByTagName('video')[0].currentTime; })();"
                    ) { value ->
                        Log.i("VALUE", value)
                        time = value
                        var url = webView.url
                        try {
                            time = time.substring(0, time.indexOf("."))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            time = "0"
                        }
                        if (url!!.contains("&t=")) {
                            url = url.substring(0, url.indexOf("&t="))
                        }
                        bookmarkManager.addBookmark(webView.title!!.replace(" - YouTube", ""), "$url&t=$time")
                    }
                } else {
                    bookmarkManager.addBookmark(webView.title!!.replace(" - YouTube", ""), webView.url!!)
                }
            } else if (webView.url!!.contains("/results")) {
                val startPosition = webView.url!!.indexOf("q=") + "q=".length
                val endPosition = webView.url!!.indexOf("&", startPosition)
                var title = webView.url!!.substring(startPosition, endPosition)
                title = try {
                    URLDecoder.decode(title, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    URLDecoder.decode(title)
                }
                bookmarkManager.addBookmark("$title - Search", webView.url!!)
            }
        } else if (menuItemTitle == context.getString(R.string.removePage)) {
            if (webView.url!!.contains("/results")) {
                val startPosition = webView.url!!.indexOf("q=") + "q=".length
                val endPosition = webView.url!!.indexOf("&", startPosition)
                var title = webView.url!!.substring(startPosition, endPosition)
                title = try {
                    URLDecoder.decode(title, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    URLDecoder.decode(title)
                }
                bookmarkManager.removeBookmark("$title - Search")
            } else {
                try {
                    bookmarkManager.removeBookmark(webView.title!!.replace(" - YouTube", ""))
                } catch (e: Exception) {
                    // To prevent crashing when page is not loaded
                }
            }
        } else {
            webView.loadUrl(bookmarkManager.getUrl(menuItemTitle))
            drawerLayout.closeDrawers()
        }
        return true
    }
}
