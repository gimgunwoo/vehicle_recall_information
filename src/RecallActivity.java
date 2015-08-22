package com.example.brian.vehiclerecallinfo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class RecallActivity extends ActionBarActivity {
    MyDBHelper myDBHelper = new MyDBHelper(RecallActivity.this);
    String make = "", year = "", name = "", part = "";
    List<String> makeList = new ArrayList<String>();
    List<String> yearList = new ArrayList<String>();
    List<String> nameList = new ArrayList<String>();
    List<String> partList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recall);

        //get lists for spinners
        makeList = myDBHelper.findMakesFromRecallInfo();

        //spinner for make
        Spinner makeSpinner = (Spinner) findViewById( R.id.make_spinner );
        ArrayAdapter<String> makeSpinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, makeList);
        makeSpinner.setAdapter(makeSpinnerArrayAdapter);
        makeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //get a make from makeSpinner
        makeSpinner.setOnItemSelectedListener(new makeSpinnerListener());
    }

    class makeSpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Object obj = parent.getItemAtPosition(position);
            make = obj.toString();

            //populate yearList after a make is chosen
            yearList = myDBHelper.findYears(make);

            //spinner for year
            Spinner yearSpinner = (Spinner) findViewById( R.id.year_spinner );
            ArrayAdapter<String> yearSpinnerArrayAdapter = new ArrayAdapter<String>(
                    RecallActivity.this, android.R.layout.simple_spinner_item, yearList);
            yearSpinner.setAdapter(yearSpinnerArrayAdapter);
            yearSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //get a string from yearSpinner
            yearSpinner.setOnItemSelectedListener(new yearSpinnerListener());

            Log.d("make?", "'" + make + "'");
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }
    }

    class yearSpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Object obj = parent.getItemAtPosition(position);
            year = obj.toString();

            //populate nameList after a year is chosen
            nameList = myDBHelper.findNames(make, year);

            //spinner for name
            Spinner nameSpinner = (Spinner) findViewById( R.id.name_spinner );
            ArrayAdapter<String> nameSpinnerArrayAdapter = new ArrayAdapter<String>(
                    RecallActivity.this, android.R.layout.simple_spinner_item, nameList);
            nameSpinner.setAdapter(nameSpinnerArrayAdapter);
            nameSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //get a string from nameSpinner
            nameSpinner.setOnItemSelectedListener(new nameSpinnerListener());

            Log.d("year?", year);
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }
    }

    class nameSpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Object obj = parent.getItemAtPosition(position);
            name = obj.toString();

            //populate partList after a name is chosne
            partList = myDBHelper.findParts(make, year, name);

            //spinner for part
            Spinner partSpinner = (Spinner) findViewById( R.id.part_spinner );
            ArrayAdapter<String> partSpinnerArrayAdapter = new ArrayAdapter<String>(
                    RecallActivity.this, android.R.layout.simple_spinner_item, partList);
            partSpinner.setAdapter(partSpinnerArrayAdapter);
            partSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //get a string from partSpinner
            partSpinner.setOnItemSelectedListener(new partSpinnerListener());

        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }
    }

    class partSpinnerListener implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            // read the part that is chosen
            Object obj = parent.getItemAtPosition(position);
            part = obj.toString();
            Log.d("part?", part);
        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // TODO Auto-generated method stub
            // Do nothing.
        }
    }

    public void searchInfo(View view){
        if(make.isEmpty() || year.isEmpty() || name.isEmpty() || part.isEmpty()){
            Toast.makeText(RecallActivity.this, "Please provide with the correct information", Toast.LENGTH_SHORT).show();
        } else {
            //when all sections are selected correctly, get the description and set it in the textview
            TextView tv = (TextView) findViewById(R.id.desc_textview);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(myDBHelper.findInfo(make,year,name,part));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recall, menu);
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
}
