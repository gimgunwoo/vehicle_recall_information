package com.example.brian.vehiclerecallinfo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String ns = null;
    private static final String TAG = "TAG";
    MyDBHelper myDBHelper;
    Context context;
    String DB_FILE_NAME = "recallDB.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inspiration from lab6
        context = getApplicationContext();

        try {
            String destPath = "/data/data/" + context.getPackageName() + "/databases/";

            File destPathFile =  new File(destPath);
            if (!destPathFile.exists())
                destPathFile.mkdirs();

            File destFile = new File(destPath + DB_FILE_NAME);
//            if (!destFile.exists())
//            {
                Log.d(TAG, "First run, copying default database");
                copyFile(context.getAssets().open(DB_FILE_NAME),new FileOutputStream(destPath + DB_FILE_NAME));
//            }
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        myDBHelper = new MyDBHelper(context);

        Toast.makeText(getApplicationContext(), "Checking new data. Please wait.", Toast.LENGTH_SHORT).show();
        new AccessWebServiceTask().execute();
        Toast.makeText(getApplicationContext(), "Data is up to date. Thank you for waiting.", Toast.LENGTH_SHORT).show();
    }

    public void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, length);
        inputStream.close();
        outputStream.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //inspiration from lab7
    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private List<RecallInfo> storeInfo() throws  XmlPullParserException, IOException {
        List<RecallInfo> infoArray = new ArrayList<RecallInfo>();

        InputStream in = OpenHttpConnection("http://data.tc.gc.ca/extracts/vrdb_60days_daily.xml");

        //inspiration from https://xjaphx.wordpress.com/2011/10/16/android-xml-adventure-parsing-xml-data-with-xmlpullparser/
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, null);
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    //when the tag is "Section", all information within this tag is for one product
                    if(parser.getName().equals("Section")) {
                        List<String> strings = new ArrayList<String>();
                        eventType = parser.nextTag();
                        if (parser.getName().equals("Field")) {
                            //can't get all attribute values in tag "Field"
                            //13 out of 14 attribute values are scanned as null
                            //so I decided to scan all the value and store only meaningful ones in the string arraylist
                            for(int i = 0; i < 14; i++) {
                                parser.getName();
                                parser.getAttributeValue(null, "Name");
                                // I had to use stringbuilder to append an empty string
                                // with parsed string and the value or else it will be null. This was strange.
                                StringBuilder strbldr = new StringBuilder("" + readText(parser));
                                if(i == 0 || i == 2 || i == 6 || i == 12 || i == 13) {
                                    //0:make, 2:part, 6:desc, 12:name, 13:year
                                    strings.add(strbldr.toString());
                                }
                            }
                            //the order in the RecallInfo constructor: make, year, name, part, desc
                            //index numbers in the string array?
                            //0: make, 4: year, 3: name, 1: part, 2: desc
                            RecallInfo ri = new RecallInfo(strings.get(0).toString(), strings.get(4).toString(),
                                    strings.get(3).toString(), strings.get(1).toString(), strings.get(2).toString());

                            infoArray.add(ri);
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {

        } catch (IOException e) {

        }


        return infoArray;
    }

    //read texts from the tag "FormattedValue", not "Value"
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("FormattedValue")) {
                //scan and store FormattedValue
                if (parser.next() == XmlPullParser.TEXT) {
                    result = parser.getText();
                    parser.nextTag();
                }
            } else if(parser.getName().equals("Value")){
                if (parser.next() == XmlPullParser.TEXT) {
                    //skip Value
                    parser.nextTag();
                }
            }
        }
        return result;
    }

    //inspiration from lab 7
    private class AccessWebServiceTask extends AsyncTask<String, Void, List<RecallInfo>> {
        protected List<RecallInfo> doInBackground(String... urls) {
            List<RecallInfo> tmp = new ArrayList<RecallInfo>();

            try {
                tmp = storeInfo();
                if (myDBHelper.countRows() == 0 || myDBHelper.countRows() == 1 ||
                        (myDBHelper.countRows() - 1) > tmp.size()) {
                    //when there is no row in the table, one row as an introduction,
                    // or data in the table has much information than the original xml file
                    myDBHelper.deleteAllRowsInRecallinfo();
                    Log.d("row test", "myDBHelper.countRows(): " + String.valueOf(myDBHelper.countRows()) + ", tmp.size(): " + String.valueOf(tmp.size()));
                    for(int i = 0 ; i < tmp.size(); i++){
//                        Log.d("listtest?", String.valueOf(tmp.size()) + " " + String.valueOf(i) + " " + tmp.get(i).toString());
                        myDBHelper.addInfo(tmp.get(i));
                    }
                } else if((myDBHelper.countRows() - 1) < tmp.size()){
                    //when there are more data from the web page, update them
                    Log.d("row test", "myDBHelper.countRows(): " + String.valueOf(myDBHelper.countRows()) + ", tmp.size(): " + String.valueOf(tmp.size()));
                    for(int i = myDBHelper.countRows() - 1; i < tmp.size(); i++)
                        myDBHelper.addInfo(tmp.get(i));
                } else {
                    //when numbers of rows in the table and the xml file are same, do nothing
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return tmp;
        }
    }

    //recall information button
    public void searchRecall(View view){
        Log.d("main!", "recall");
        Intent intent = new Intent(this, RecallActivity.class);
        startActivity(intent);
    }

    //toll free button
    public void searchPhone(View view){
        Log.d("main!", "phone");
        Intent intent = new Intent(this, TollfreeActivity.class);
        startActivity(intent);
    }

    //tutorial button
    public void openTutorial(View view){
        Log.d("main!", "tutorial");
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }
}