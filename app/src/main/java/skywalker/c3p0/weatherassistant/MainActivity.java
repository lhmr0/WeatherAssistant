package skywalker.c3p0.weatherassistant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import skywalker.c3p0.weatherassistant.clases.GPSTracker;
import skywalker.c3p0.weatherassistant.clases.Loc;
import skywalker.c3p0.weatherassistant.clases.RingtonePlayingService;
import skywalker.c3p0.weatherassistant.clases.SessionManagement;

public class MainActivity extends AppCompatActivity {
    TextView tempMax, tempMin, txt;
    ProfilePictureView fot;
    EditText ciudad;
    JSONObject data = null;
    GPSTracker locateme;
    Button masinfo, btnCambiar;
    String user;
    String nomCiudad = "CSM";
    public String ciudadinp = "csm";
    HashMap<String, String> usu;
    SharedPreferences sp, sp2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap bitmapa = (Bitmap) getIntent().getParcelableExtra("Image");
        sp = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("ciudad", Context.MODE_PRIVATE);
        String ciudad1 = sp2.getString("ciudadKey",null);
        String nom = sp.getString(LoginActivity.Name,null);
        String id = sp.getString("id", null);
        tempMax = (TextView)findViewById(R.id.txtMax);
        tempMin = (TextView)findViewById(R.id.txtMin);
        txt = (TextView)findViewById(R.id.txtcity);
        //fot = (ProfilePictureView)findViewById(R.id.image);
        ciudad = (EditText) findViewById(R.id.txtCiudad);
        masinfo = (Button)findViewById(R.id.btnMasInfo);
        btnCambiar = (Button)findViewById(R.id.btnCambiar);




        //Bundle bundle = getIntent().getExtras();

        new DrawerBuilder().withActivity(this).build();
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.app_name);
        final SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.info).withBadge(ciudad1).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withIcon(FontAwesome.Icon.faw_cloud);
        final SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.crear).withIcon(FontAwesome.Icon.faw_calendar_plus_o);
        final SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.alarm).withIcon(FontAwesome.Icon.faw_clock_o);
        final SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.ver).withIcon(FontAwesome.Icon.faw_calendar_check_o);
        final SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.log).withIcon(FontAwesome.Icon.faw_user);
        final SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName(R.string.pro).withIcon(FontAwesome.Icon.faw_forward);
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        item1,
                        new ProfileDrawerItem().withName(nom).withIcon(FontAwesome.Icon.faw_github),
                        new DividerDrawerItem(),
                        item2,
                        item7,
                        item3,
                        item4,
                        item6,
                        item5,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Perú - 2017").withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem==item2){
                            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(MainActivity.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(MainActivity.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                           editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(MainActivity.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(MainActivity.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();


        masinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MasInfo.class);
                myIntent.putExtra("ciudad",ciudadinp);
                startActivity(myIntent);
            }
        });
        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJSON(ciudad.getText().toString());
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            //getJSON(ubi().getLat(),ubi().getLon());
            getJSON(ciudad1);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    12281);
            //  getJSON(ubi().getLat(),ubi().getLon());
            getJSON(ciudad1);
        }

        // fot.setProfileId(id);


    }


    public String onReceive(Context context, Intent intent) {
        String user = intent.getExtras().getString("user");
        return user;
    }

    public String hora(int milliseg){
        Date sun = new Date(milliseg*1000);
        int hor = sun.getHours()-3;
        int min = sun.getMinutes()-3;
        return "Hora: "+hor+":"+min+"";
    }

    public Loc ubi(){
        Loc lc = new Loc();
        double latitude=1;
        double longitude=1;

        locateme = new GPSTracker(MainActivity.this);
        if(locateme.canGetLocation()) {
            latitude = locateme.getLatitude();
            longitude = locateme.getLongitude();
            lc.setLat(latitude);
            lc.setLon(longitude);
        }
        return lc;
    }

    public void getJSON(String ciud) {
        String nuevaci="Trujillo";
        if(ciud==null){
            nomCiudad = "Trujillo";
            ciudad.setText(nomCiudad);
        }else{
            nuevaci=ciud;
            nomCiudad = ciud;
            ciudad.setText(nomCiudad);
        }
                            final String[] defini = new String[1];
            final String finalNuevaci = nuevaci;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String api = "2884e0e33c8700895eb03582b8641712";
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+ nomCiudad +",pe&APPID=ea574594b9d36ab688642d5fbeab847e&lang=es");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();
                    data = new JSONObject(json.toString());
                    if(data.getInt("cod") != 200) {
                        Toast.makeText(MainActivity.this, "No se encuentra la ciudad", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(myIntent);
                    }
                } catch (Exception e) {
                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){
                    Log.d("datos recibidos: ",data.toString());
                    String text = "Hello, world!";
                    try {
                        double max = (data.getJSONObject("main").getDouble("temp_max")-273.15)+1.2;
                        double min = (data.getJSONObject("main").getDouble("temp_min")-273.15)-1.5;

                        String maxi = ""+max;
                        String mini = ""+min;
                        String maxi4 = maxi.substring(0, 4);
                        String mini4 = mini.substring(0, 4);
                        ciudadinp = data.getString("name");
                        SharedPreferences.Editor editor2 = sp2.edit();
                        editor2.putString("ciudadKey", ciudadinp);
                        editor2.commit();
                        defini[0] = ciudadinp;
                        tempMax.setText("máx: "+maxi4);
                        //tempMin.setText("mín: "+mini4);
                        ciudad.setText(ciudadinp);

                        tempMin.setText(mini4);
                        tempMax.setText(maxi4);
                        Log.d("hola", max+" , "+min);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        }.execute();


    }


}
