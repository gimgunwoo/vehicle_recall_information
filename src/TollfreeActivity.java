package com.example.brian.vehiclerecallinfo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TollfreeActivity extends ActionBarActivity {
    MyDBHelper myDBHelper = new MyDBHelper(TollfreeActivity.this);
    String tollfree = "", number_to_call = "", make = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tollfree);

        List<String> makeList = new ArrayList<String>();
        makeList = myDBHelper.findMakesFromTollfree();

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.custom_spinner, makeList);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        spinner = (Spinner) findViewById( R.id.make_spinner );
        spinner.setAdapter(spinnerArrayAdapter);

        AdapterView.OnItemSelectedListener onSpinner = new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                make = obj.toString();

                TextView tv = (TextView)findViewById(R.id.tollfree_number);
                tollfree = myDBHelper.findTollFree(make);
                tv.setText(tollfree);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        };

        spinner.setOnItemSelectedListener(onSpinner);

		//inspiration from https://www.youtube.com/watch?v=3PHDcQOGFtg
		//hit the button to make a phone call
        Button b = (Button)findViewById(R.id.make_phone_call);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_CALL);
				//http://developer.android.com/reference/java/util/regex/Pattern.html
				//if number_to_call has any white space and hyphen, trim them
                Log.d("tollfree", tollfree);
                if(!tollfree.isEmpty()) {
                    number_to_call = tollfree.replaceAll("\\s\\-", "");
                    Log.d("number_to_call", number_to_call);
                    intent.setData(Uri.parse("tel:" + number_to_call));
                    startActivity(intent);
                } else {
                    Toast.makeText(TollfreeActivity.this, "Please choose a brand", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tollfree, menu);
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
