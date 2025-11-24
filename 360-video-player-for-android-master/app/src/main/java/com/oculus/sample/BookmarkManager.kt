package com.oculus.sample

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.webkit.WebView
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BookmarkManager(private val context: Context, private val webView: WebView) {

    private lateinit var bookmarkUrls: MutableList<String>
    private lateinit var bookmarkTimelessUrls: MutableList<String>
    private lateinit var bookmarkTitles: MutableList<String>

    private lateinit var navigationView: NavigationView
    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun initalizeBookmarks(navigationView: NavigationView) {
        this.navigationView = navigationView
        bookmarkUrls = ArrayList()
        bookmarkTimelessUrls = ArrayList()
        bookmarkTitles = ArrayList()

        val menu = navigationView.menu
        menu.clear()
        val result = sp.getString("bookmarks", "[]")
        try {
            val bookmarksArray = JSONArray(result)
            for (i in 0 until bookmarksArray.length()) {
                val bookmark = bookmarksArray.getJSONObject(i)
                menu.add(bookmark.getString("title")).setIcon(R.drawable.ic_star_grey600_24dp)
                bookmarkTitles.add(bookmark.getString("title"))
                bookmarkUrls.add(bookmark.getString("url"))
                var timeless = bookmark.getString("url")
                if (timeless.contains("&t=")) {
                    timeless = timeless.substring(0, timeless.indexOf("&t="))
                }
                bookmarkTimelessUrls.add(timeless)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            var url = webView.url
            if (url != null && url.contains("&t=")) {
                url = url.substring(0, url.indexOf("&t="))
            }
            if (url != null && url.contains("/results")) {
                url = url.replace("+", "%20")
            }
            if (bookmarkUrls.contains(webView.url) || (webView.title != null && bookmarkTitles.contains(webView.title!!.replace("'", "\\'"))) || bookmarkTimelessUrls.contains(url)) {
                menu.add(context.getString(R.string.removePage)).setIcon(R.drawable.ic_close_grey600_24dp)
            } else {
                menu.add(context.getString(R.string.addPage)).setIcon(R.drawable.ic_plus_grey600_24dp)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addBookmark(title: String, url: String) {
        val result = sp.getString("bookmarks", "[]")
        try {
            val bookmarksArray = JSONArray(result)
            bookmarksArray.put(JSONObject("{'title':'" + title.replace("'", "\\'") + "','url':'" + url + "'}"))
            val editor = sp.edit()
            editor.putString("bookmarks", bookmarksArray.toString())
            editor.apply()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        initalizeBookmarks(navigationView)
    }

    fun removeBookmark(title: String) {
        val result = sp.getString("bookmarks", "[]")
        try {
            var bookmarksArray = JSONArray(result)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bookmarksArray.remove(bookmarkTitles.indexOf(title))
            } else {
                val objs = asList(bookmarksArray)
                objs.removeAt(bookmarkTitles.indexOf(title))
                val out = JSONArray()
                for (obj in objs) {
                    out.put(obj)
                }
                bookmarksArray = out
            }
            val editor = sp.edit()
            editor.putString("bookmarks", bookmarksArray.toString())
            editor.apply()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        initalizeBookmarks(navigationView)
    }

    fun getUrl(title: String): String {
        return bookmarkUrls[bookmarkTitles.indexOf(title)]
    }

    companion object {
        fun asList(ja: JSONArray): MutableList<JSONObject> {
            val len = ja.length()
            val result = ArrayList<JSONObject>(len)
            for (i in 0 until len) {
                val obj = ja.optJSONObject(i)
                if (obj != null) {
                    result.add(obj)
                }
            }
            return result
        }
    }
}
