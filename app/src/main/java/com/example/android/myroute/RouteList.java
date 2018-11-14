package com.example.android.myroute;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RouteList extends AppCompatActivity {


    DbHelper myDb;
    ListView listview;
    public SimpleCursorAdapter simpleCursorAdapter;
    TextView description;
    TextView date, time, idValue, singleChar;
    EditText searchtextbox;
    public String routes;
    ArrayList<LatLng> cordList;
    Button action;
    public LayoutInflater inflater;
    public View v;
    TextView arrayitem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);

        inflater = (LayoutInflater) getApplicationContext().getSystemService
                (this.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.list_design_spec, null);

        //Database helper initialization file for search fragment
        myDb = new DbHelper(this);
        listview = (ListView) findViewById(R.id.ListViewonPage);
        listview = (ListView) findViewById(R.id.ListViewonPage);
        description = (TextView) v.findViewById(R.id.descriptonlabel);
        date = (TextView) v.findViewById(R.id.datelabel);
        time = (TextView) v.findViewById(R.id.timelabel);
        singleChar = (TextView) v.findViewById(R.id.bigchar);
        idValue = (TextView) v.findViewById(R.id.id);
        searchtextbox = (EditText) findViewById(R.id.searchtextbox);
        arrayitem = (TextView) v.findViewById(R.id.id);

     //   cordList = getIntent().getParcelableArrayListExtra("LAT_LNG_ARRAY");

        //Listview onclick listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //connecting references to object pointers
                int itemposition = position;
                idValue = (TextView) view.findViewById(R.id.id);

                Log.d("Array Value", String.valueOf(idValue.getText()));
                Intent intent = new Intent(RouteList.this, map_history.class);
                intent.putExtra("idValue", idValue.getText().toString());
                startActivity(intent);
            }
        });



        //Creating String to hold data items called from database
        final Cursor data = myDb.getAllData();
        while (data.moveToNext()) {

            final String[] columns = new String[]{
                    DbHelper.col_1,
                    DbHelper.col_2,
                    DbHelper.col_3,
                    DbHelper.col_4,
                    DbHelper.col_5,
            };
            final int[] boundTo = new int[]{
                    R.id.descriptonlabel,
                    R.id.id,
                    R.id.datelabel,
                    R.id.timelabel,
                    R.id.bigchar,
            };

            //Initialized simplecursor adapter for inflating data onto the listview
            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_design_spec, data, columns, boundTo, 1);
            listview.setAdapter(simpleCursorAdapter);
        }


        ((EditText) findViewById(R.id.searchtextbox)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //On textchange listener handler
                EditText searchtextbox = (EditText) findViewById(R.id.searchtextbox);

                date = (TextView) v.findViewById(R.id.datelabel);
                time = (TextView) v.findViewById(R.id.timelabel);
                singleChar = (TextView) v.findViewById(R.id.bigchar);
                idValue = (TextView) v.findViewById(R.id.id);
                // Cursor pointer to pull and link data from the database
                Cursor data = myDb.getSomeData(searchtextbox.getText().toString());
                while (data.moveToNext()) {
                    final String[] columns = new String[]{
                            DbHelper.col_1,
                            DbHelper.col_2,
                            DbHelper.col_3,
                            DbHelper.col_4,
                            DbHelper.col_5,

                    };
                    final int[] boundTo = new int[]{
                            R.id.descriptonlabel,
                            R.id.id,
                            R.id.datelabel,
                            R.id.timelabel,
                            R.id.bigchar,
                    };

                    //Initialized simplecursor adapter for inflating data onto the listview
                    simpleCursorAdapter = new SimpleCursorAdapter(RouteList.this, R.layout.list_design_spec, data, columns, boundTo, 1);
                    listview.setAdapter(simpleCursorAdapter);
                }
            }

            //Aftertextchanged event handler
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

}
