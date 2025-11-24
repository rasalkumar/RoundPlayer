package com.oculus.sample

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Main2Activity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var jsonString: String? = null
    private var jsonObject: JSONObject? = null
    private var jsonArray: JSONArray? = null
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        listView = findViewById(R.id.getlist)
        contactsAdapter = ContactsAdapter(this, R.layout.row_layout)
        BackgroundTask().execute()
    }

    inner class BackgroundTask : AsyncTask<Void, Void, String>() {

        private var jsonUrl: String? = null

        override fun onPreExecute() {
            jsonUrl = "https://prashant8267851475.000webhostapp.com/json_get.php"
        }

        override fun doInBackground(vararg params: Void): String? {
            try {
                val url = URL(jsonUrl)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream = httpURLConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { jsonString = it } != null) {
                    stringBuilder.append(jsonString).append("\n")
                }
                bufferedReader.close()
                inputStream.close()
                httpURLConnection.disconnect()
                return stringBuilder.toString().trim()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        private val URL = arrayOfNulls<String>(10)
        private val NAME = arrayOfNulls<String>(10)

        override fun onProgressUpdate(vararg values: Void) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String) {
            try {
                listView.adapter = null
                jsonObject = JSONObject(result)
                jsonArray = jsonObject!!.getJSONArray("server_response")
                var count = 0
                var url: String
                var name: String
                while (count < jsonArray!!.length()) {
                    val JO = jsonArray!!.getJSONObject(count)
                    url = JO.getString("url")
                    name = JO.getString("name")
                    URL[count] = url
                    NAME[count] = name
                    count++
                }
                val custom = Custom(this@Main2Activity, URL, NAME)
                listView.adapter = custom
                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, i, _ ->
                        val intent = Intent(this@Main2Activity, SphericalPlayerActivity::class.java)
                        intent.putExtra("url", URL[i])
                        startActivity(intent)
                    }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
