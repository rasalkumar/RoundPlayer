package com.oculus.sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    String JSON_STRING;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ContactsAdapter contactsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView =(ListView)findViewById(R.id.getlist);
        contactsAdapter = new ContactsAdapter(this,R.layout.row_layout);
        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void,Void,String>
    {
        String json_url;
        @Override
        protected void onPreExecute() {
            json_url ="https://prashant8267851475.000webhostapp.com/json_get.php";
        }

        @Override
        protected String doInBackground( Void... params) {
            try {

                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder= new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return  stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }
        String URL[]=new String[10];
        String NAME[]=new String[10];
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                listView.setAdapter(null);
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("server_response");
                int count =0;
                String url,name;
                while(count<jsonArray.length()){

                    JSONObject JO = jsonArray.getJSONObject(count);
                    url= JO.getString("url");
                    name=JO.getString("name");
                    URL[count]=url;
                    NAME[count]=name;
                    //Contacts contacts = new Contacts(url);
                    //contactsAdapter.add(contacts);
                    count++;
                }

                Custom custom = new Custom(Main2Activity.this,URL,NAME);
                listView.setAdapter(custom);
               // listView.setAdapter(contactsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(Main2Activity.this,SphericalPlayerActivity.class);
                        //String data=(String)adapterView.getItemAtPosition(i);
                        intent.putExtra("url",URL[i]);
                        startActivity(intent);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
