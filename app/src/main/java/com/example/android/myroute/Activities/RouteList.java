package com.example.android.myroute.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myroute.DbHelper.DbHelper;
import com.example.android.myroute.R;
import com.example.android.myroute.model.Ampersand;
import com.example.android.myroute.model.Route;
import com.example.android.myroute.model.jsonExport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RouteList extends AppCompatActivity {

    DbHelper myDb;
    ListView listview;
    TextView description;
    TextView date, time, idValue, singleChar;
    EditText searchtextbox;
    public LayoutInflater inflater;
    View v;
    LinearLayout emptyroute;

    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    FrameLayout frameLayout;
    double latit;
    double longit;
    Dialog dialog;
    TextView databaseid;
    FancyAdapter aa = null;
    EditText editDesc;
    Button editbtn;
    ArrayList<com.example.android.myroute.model.Route> arrayOfRoute = new ArrayList<com.example.android.myroute.model.Route>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        mToolbar = (Toolbar) findViewById(R.id.menutoobar);
        setSupportActionBar(mToolbar);
        //setupNavigationDrawerMenu();

        inflater = (LayoutInflater) getApplicationContext().getSystemService
                (LAYOUT_INFLATER_SERVICE);

      // v = inflater.inflate(R.layout.route_list, null);
        v = View.inflate(this,R.layout.list_design_spec, null);

        //Database helper initialization file for search fragment
        myDb = new DbHelper(this);

        ArrayList<String> a = new ArrayList<String>();

        listview = (ListView) findViewById(R.id.ListViewonPage);
        description = (TextView) v.findViewById(R.id.descriptonlabel);
        date = (TextView) v.findViewById(R.id.datelabel);
        time = (TextView) v.findViewById(R.id.timelabel);
        singleChar = (TextView) v.findViewById(R.id.bigchar);
        idValue = (TextView) v.findViewById(R.id.id);
        databaseid = (TextView) v.findViewById(R.id.idvalue);
        searchtextbox = (EditText) findViewById(R.id.searchtextbox);
        emptyroute = (LinearLayout) findViewById(R.id.emptyroute);
      //  navigationView = (NavigationView) findViewById(R.id.navigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_content);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        editDesc = (EditText)findViewById(R.id.description_edit);
        editbtn = (Button)v.findViewById(R.id.editbtn);

//        navigationView.setNavigationItemSelectedListener(this);
        //   cordList = getIntent().getParcelableArrayListExtra("LAT_LNG_ARRAY");
        //ListView onclick listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //connecting references to object pointers
                //String item = (String)listview.getItemAtPosition(position);
                idValue = (TextView) view.findViewById(R.id.id);
                //  Log.d("listItem", item );
                Intent intent = new Intent(RouteList.this, map_history.class);
                intent.putExtra("idValue", idValue.getText().toString());
                startActivity(intent);
            }
        });
            //Populate data on listview
            populateData();

        ((EditText) findViewById(R.id.searchtextbox)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //On textChange listener handler
                EditText searchtextbox = (EditText) findViewById(R.id.searchtextbox);

                date = (TextView) v.findViewById(R.id.datelabel);
                time = (TextView) v.findViewById(R.id.timelabel);
                singleChar = (TextView) v.findViewById(R.id.bigchar);
                idValue = (TextView) v.findViewById(R.id.id);
                // Cursor pointer to pull and link data from the database
            }
            //Aftertextchanged event handler
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (listview.getCount() == 0) {
            emptyroute.setVisibility(View.VISIBLE);
        } else {
            emptyroute.setVisibility(View.GONE);
        }


    }

    void populateData(){
        //Creating String to hold data items called from database
        final Cursor data = myDb.getAllData();
        while (data.moveToNext()) {

            int columnIndex;
            String id =    data.getString(0);
            String description = data.getString(1);
            String arrayvalue = data.getString(2);
            String date = data.getString(3);
            String time = data.getString(4);
            String bigchar = data.getString(5);

            com.example.android.myroute.model.Route route = new com.example.android.myroute.model.Route();
            route.setId(id);
            route.setDescription(description);
            route.setArrayvalue(arrayvalue);
            route.setDate(date);
            route.setTime(time);
            route.setBigchar(bigchar);

            arrayOfRoute.add(route);
            Log.d("Array Collection", String.valueOf(arrayOfRoute));

            aa = new FancyAdapter();
            listview.setAdapter(aa);
        }
    }

    class FancyAdapter extends ArrayAdapter<Route> {
        FancyAdapter() {
            super(RouteList.this, android.R.layout.simple_list_item_1, arrayOfRoute);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            try {

                ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(R.layout.list_design_spec, null);
                    //here is something new.  we are using a class called a view holder
                    holder = new ViewHolder(convertView);
                    //we are using that class to cache the result of the findViewById function
                    //which we then store in a tag on the view
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.populateFrom(arrayOfRoute.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return (convertView);
        }
    }

    class ViewHolder {
        public TextView id;
        public TextView description;
        public TextView bigchar;
        public TextView arrayvalue;
        public TextView date;
        public TextView time;
        public Button deletebtn;
        public Button editbtn;
        public Button  okbtn;
        public Button cancelbtn;
        public Button exportbtn;
        public TextView idValue;

        ViewHolder(View v) {
            id = (TextView) v.findViewById(R.id.idvalue);
            description = (TextView) v.findViewById(R.id.descriptonlabel);
            bigchar = (TextView) v.findViewById(R.id.bigchar);
            arrayvalue = (TextView) v.findViewById(R.id.id);
            date = (TextView) v.findViewById(R.id.datelabel);
            time = (TextView) v.findViewById(R.id.timelabel);
            deletebtn = (Button) v.findViewById(R.id.deletebtn);
            editbtn = (Button)v.findViewById(R.id.editbtn);
            exportbtn = (Button) v.findViewById(R.id.exportbtn);

        }

        void populateFrom(Route r) {
            id.setText(r.getId());

            description.setText(r.getDescription());
            bigchar.setText(r.getBigchar());
            arrayvalue.setText(r.getArrayvalue());
            String c = arrayvalue.getText().toString();
            date.setText(r.getDate());
            time.setText(r.getTime());

            deletebtn.setOnClickListener(v ->{
                try{
                    //Delete item from database
                    myDb.deleteItem(id.getText().toString());
                    //Save current position on the listview
                    int currentPosition = listview.getFirstVisiblePosition();
                    //clear the array holding the routeList
                    arrayOfRoute.clear();
                    populateData();
                    //Restore last saved listview position
                    listview.setSelection(currentPosition);
                    System.out.println("Deleted successfully " + id.getText().toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });

            editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(RouteList.this); // Context, this, etc.
                    dialog.setContentView(R.layout.edit_dialog_obtainer);
                    editDesc = (EditText)dialog.findViewById(R.id.description_edit);
                    okbtn = (Button) dialog.findViewById(R.id.okbtn);
                    cancelbtn = (Button) dialog.findViewById(R.id.cancelbtn);
                    String descIntentValue = description.getText().toString();

                    Log.d("desc", String.valueOf(descIntentValue));
                    editDesc.setText(descIntentValue);

                    okbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        myDb.updateData(editDesc.getText().toString(), id.getText().toString());
                        arrayOfRoute.clear();
                        populateData();
                        dialog.dismiss();
                        }
                    });
                    cancelbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });


            exportbtn.setOnClickListener(v ->{

                dialog = new Dialog(RouteList.this); // Context, this, etc.
                dialog.setTitle("Export to:");
                dialog.setContentView(R.layout.export_dialog);
                Button json = (Button) dialog.findViewById(R.id.Geo_json);
                Button kml = (Button) dialog.findViewById(R.id.kml);
                json.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("export","export button is triggered");
                        jsonExport export = new jsonExport();
                        export.setCoordinate("coordinate");
                        export.setCordValue(c);

                        Log.d("jsonValue", String.valueOf(export));
                        Gson gson = new Gson();
                        System.out.println(gson.toJson(export).toString());
                        writeJsonToFile(String.valueOf(export), RouteList.this);
                        dialog.dismiss();
                    }
                });


                dialog.show();
            });


        }
    }

    private void writeJsonToFile(String data, Context context) {

        String descriptonName = description.getText().toString();

        Log.d("descriptionName", descriptonName);
        File theDir = new File(Environment.getExternalStorageDirectory() + File.separator + "/Routes");
        // if the directory does not exist, create it
        if (!theDir.exists()) {

            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
                System.out.println("creating directory: " + theDir.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (result) {
                System.out.println("Created " + theDir.getName());
            }
        }
        try {
            File mediaDir = new File("/sdcard/Routes");
            if (!mediaDir.exists()){
                mediaDir.mkdir();
            }
            String string = data;
            File resolveMeSDCard = new File("/sdcard/Routes/"+ descriptonName+".json");
            resolveMeSDCard.createNewFile();
            FileOutputStream fos = new FileOutputStream(resolveMeSDCard);
            fos.write(string.getBytes());
            fos.close();

            Toast.makeText(this, "Json file has been exported to "+ theDir.getName(),
                    Toast.LENGTH_SHORT).show();

            Log.d("jsonRoutes",data);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

/*    private void setupNavigationDrawerMenu() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.main_content);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        drawerToggle.getDrawerArrowDrawable().setColor(Color.parseColor("#ffffff"));
    }*/

  /*  @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_menu_item:
                Intent profilebutton = new Intent(this, about.class);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(profilebutton);
                break;
            case R.id.priv_users_menu_item:
                Intent priviledgedUserbtn = new Intent(this, map_history.class);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(priviledgedUserbtn);
                break;
        }
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.*/

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
         if(frameLayout.isShown()) {
             frameLayout.setVisibility(View.GONE);
         }
         else{
             frameLayout.setVisibility(View.VISIBLE);
         }
         }
        return super.onOptionsItemSelected(item);
    }

    public void saveNewRoute(View view) {
        Intent intent = new Intent(RouteList.this, MapsActivity.class);
        startActivity(intent);
    }

    public void callGoogleMap(View view) {


        idValue = (TextView) findViewById(R.id.id);

        if(idValue !=  null ) {

            String[] ary = idValue.getText().toString().split(",");


            Log.d("EntireArray", Arrays.toString(ary));
            Log.d("Entire Array First", ary[0]);
            Log.d("Entire Array last", ary[ary.length - 1]);

            String[] f = ary[0].split("&");
            String[] l = ary[ary.length - 1].split("&");
            Log.d("ff", Arrays.toString(f));
            Log.d("ffl", Arrays.toString(l));


            String ff = f[0];
            String fl = f[1];
            String lf = l[0];
            String ll = l[1];

            Log.d("first and last", ff);
            Log.d("first and last", fl);
            Log.d("first and last", lf);
            Log.d("first and last", ll);


            String latFirst = ff.replace("[", "");
            String longLast = ll.replace("]", "");
            Log.d("first and last", latFirst);
            Log.d("first and last", longLast);

            double firstLatitude = Double.parseDouble(latFirst);
            double fistLongitude = Double.parseDouble(fl);
            double lastLatitude = Double.parseDouble(lf);
            double lastLongitude = Double.parseDouble(longLast);


            for (int a = 0; a < ary.length; a++) {

                String[] finalsplit = Arrays.toString(ary).split(",");
                String[] anotherfinalsplit = Arrays.toString(new String[]{finalsplit[a]}).split("&");

                Log.d("Individual Items", Arrays.toString(new String[]{finalsplit[a]}));
                Log.d("Individual Items2", Arrays.toString(anotherfinalsplit));
                Log.d("Individual Split", anotherfinalsplit[0]);
                Log.d("Individual Split 2", anotherfinalsplit[1]);


                try {

                    String latString = anotherfinalsplit[0].replace("[", "");
                    String longString = anotherfinalsplit[1].replace("]", "");


                    latit = Double.parseDouble(latString);
                    longit = Double.parseDouble(longString);

                    Log.d("lat", String.valueOf(latit));
                    Log.d("laat", String.valueOf(longit));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + firstLatitude + "," + fistLongitude + "&daddr=" + lastLatitude + "," + lastLongitude));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
            //// listview = myDb.deleteData(listview.getPositionForView());
        }
    }


    public void addNewRoute(View view) {

        Intent i = new Intent(RouteList.this, MapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        }

    }

