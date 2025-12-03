package com.oculus.sample

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Main3Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        listView = findViewById(R.id.listview)
        val url = arrayOf(
            "http://sherly.mobile9.com/trRQo5ZbL4isJpIRB-Xnsw/1490562525/vsdl/585/closerftha_ATgMYxL0.mp4",
            "http://www.hdjumbo.com/video/down/26434590/2444375/OGM4MzNhSWZZTkZKd2FlUlVRK2NlVnNOeW9Jc2JCT3JpZkJBQjh5MDJEd05xbWpmUnc=/Sugar+%28Maroon+5%29+HD+%28HDJumbo.Com%29.mp4",
            "http://f1.videoming.com/files/sfd68/33706/Tajdar-e-Haram%20-%20Atif%20Aslam%20Full%20HD(videoming).mp4"
        )
        val pics = arrayOf(R.drawable.closer, R.drawable.maroon, R.drawable.taz)
        val customAdapter = CustomAdapter(this, pics, url)

        listView.adapter = customAdapter

        listView.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(this@Main3Activity, SphericalPlayerActivity::class.java)
            intent.putExtra("url", url[i])
            startActivity(intent)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_drive) {
            val i = Intent(this, Main2Activity::class.java)
            startActivity(i)
        } else if (id == R.id.nav_gallery) {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO)
        } else if (id == R.id.nav_slideshow) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                val selectedImageUri = data?.data
                val filemanagerstring = selectedImageUri?.path
                val selectedImagePath = getPath(selectedImageUri)

                Toast.makeText(this, filemanagerstring, Toast.LENGTH_LONG).show()

                if (filemanagerstring != null) {
                    val intent = Intent(this@Main3Activity, SphericalPlayerActivity::class.java)
                    intent.putExtra("url", filemanagerstring)
                    startActivity(intent)
                }
            }
        }
    }

    // UPDATED!
    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
        return null
    }

    companion object {
        private const val REQUEST_TAKE_GALLERY_VIDEO = 100
    }
}
