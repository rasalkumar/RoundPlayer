package com.oculus.sample

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ActionMenuView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.snackbar.Snackbar

class MenuHelper(
    private val context: Context,
    private val webView: WebView,
    private val backgroundPlayHelper: BackgroundPlayHelper,
    private val appWindow: View
) : ActionMenuView.OnMenuItemClickListener {

    private lateinit var actionMenu: ActionMenuView
    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bookmarksPanel: View

    fun homepageTutorial() {
        if (!sp.getBoolean("homepageLearned", false)) {
            val dialog = AlertDialog.Builder(context).create()
            dialog.setTitle(context.getString(R.string.home))
            dialog.setMessage(context.getString(R.string.homePageHelp))
            dialog.setCancelable(false)
            dialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { _, _ ->
                dialog.dismiss()
                val editor = sp.edit()
                editor.putBoolean("homepageLearned", true)
                editor.apply()
            }
            dialog.show()
        }
    }

    fun setUpMenu(actionMenu: ActionMenuView, drawerLayout: DrawerLayout, bookmarksPanel: View) {
        this.drawerLayout = drawerLayout
        this.bookmarksPanel = bookmarksPanel
        this.actionMenu = actionMenu
        actionMenu.setOnMenuItemClickListener(this)

        // Enable special buttons
        val menu = actionMenu.menu
        val pm = context.packageManager
        menu.findItem(R.id.action_backgroundPlay)
            .isChecked = sp.getBoolean(BackgroundPlayHelper.PREF_BACKGROUND_PLAY_ENABLED, true)
        menu.findItem(R.id.action_accept_cookies).isChecked = sp.getBoolean(PREF_COOKIES_ENABLED, true)

        // Add Kodi button
        try {
            pm.getPackageInfo("org.xbmc.kore", PackageManager.GET_ACTIVITIES)
            menu.findItem(R.id.action_cast_to_kodi).isEnabled = true
        } catch (e: PackageManager.NameNotFoundException) {
            /* Kodi is not installed */
        }
    }

    private fun show_noVideo_dialog() {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setTitle(context.getString(R.string.error_no_video))
        dialog.setMessage(context.getString(R.string.error_select_video_and_retry))
        dialog.setCancelable(true)
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.ok).toUpperCase()
        ) { _, _ -> dialog.dismiss() }
        dialog.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_web) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webView.url)))
            return true
        } else if (itemId == R.id.action_refresh) {
            webView.reload()
            return true
        } else if (itemId == R.id.action_home) {
            homepageTutorial()
            webView.loadUrl(sp.getString("homepage", "https://m.youtube.com/")!!)
            return true
        } else if (itemId == R.id.action_set_as_home) {
            Snackbar.make(appWindow, context.getString(R.string.homePageSet), Snackbar.LENGTH_LONG).show()
            val editor = sp.edit()
            editor.putString("homepage", webView.url)
            editor.apply()
            return true
        } else if (itemId == R.id.action_bookmarks) {
            drawerLayout.openDrawer(bookmarksPanel)
            return true
        } else if (itemId == R.id.action_share) {
            if (!webView.url!!.contains("/watch")) {
                show_noVideo_dialog()
            } else {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, webView.url)
                shareIntent.type = "text/plain"
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.resources.getText(R.string.share_with)
                    )
                )
            }
            return true
        } else if (itemId == R.id.action_cast_to_kodi) {
            if (!webView.url!!.contains("/watch")) {
                show_noVideo_dialog()
            } else {
                try {
                    /* The following code is based on an extract from the source code of NewPipe (v0.7.2) (https://github.com/theScrabi/NewPipe),
                       which is also licenced under version 3 of the GNU General Public License as published by the Free Software Foundation.
                       The copyright owner of the original code is Christian Schabesberger <chris.schabesberger@mailbox.org>.
                       All modifications were made on 06-Jan-2016 */
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setPackage("org.xbmc.kore")
                    intent.data = Uri.parse(webView.url!!.replace("https", "http"))
                    context.startActivity(intent)
                    /* End of the modified NewPipe code extract */
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return true
        } else if (itemId == R.id.action_backgroundPlay) {
            if (sp.getBoolean(BackgroundPlayHelper.PREF_BACKGROUND_PLAY_ENABLED, true)) {
                backgroundPlayHelper.disableBackgroundPlay()
                item.isChecked = false
            } else {
                backgroundPlayHelper.enableBackgroundPlay()
                item.isChecked = true
            }
            return true
        } else if (itemId == R.id.action_download) {
            if (!webView.url!!.contains("/watch")) {
                show_noVideo_dialog()
            } else {
                Downloader(context).download(webView.url)
            }
            return true
        } else if (itemId == R.id.action_accept_cookies) {
            if (sp.getBoolean(PREF_COOKIES_ENABLED, true)) {
                CookieHelper.acceptCookies(webView, false)
                CookieHelper.deleteCookies()
                item.isChecked = false
            } else {
                CookieHelper.acceptCookies(webView, true)
                item.isChecked = true
            }
            val spEdit = sp.edit()
            spEdit.putBoolean(PREF_COOKIES_ENABLED, !sp.getBoolean(PREF_COOKIES_ENABLED, true))
            spEdit.apply()
            return true
        }
        return false
    }

    companion object {
        const val PREF_COOKIES_ENABLED = "cookiesEnabled"
    }
}
