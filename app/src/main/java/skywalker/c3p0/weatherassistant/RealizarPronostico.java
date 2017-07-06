package skywalker.c3p0.weatherassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RealizarPronostico extends AppCompatActivity {
    SharedPreferences sp,sp2;
    Button cam, reg;
    TextView tv;
    EditText ciudad;
    ListView lstDatos;
    String nomCiudad = "CSM";
    NumberPicker np;
    String ciudad1;
    public String ciudadinp = "csm";
    JSONObject data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pronostico);
        sp = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("ciudad", Context.MODE_PRIVATE);
        String nom = sp.getString(LoginActivity.Name,null);
        ciudad1 = sp2.getString("ciudadKey",null);
        cam = (Button) findViewById(R.id.btnCambiar);
        tv = (TextView)findViewById(R.id.txtset);
        reg = (Button)findViewById(R.id.btnRegistrarE);
        lstDatos = (ListView)findViewById(R.id.lstDatos) ;
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
                            Intent myIntent = new Intent(RealizarPronostico.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(RealizarPronostico.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(RealizarPronostico.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(RealizarPronostico.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(RealizarPronostico.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(RealizarPronostico.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();
        if(nom==null){
            Intent myIntent = new Intent(RealizarPronostico.this, LoginActivity.class);
            startActivity(myIntent);
        }
        tv.setText("Estás en "+ciudad1);
        np = (NumberPicker) findViewById(R.id.np);
        np.setMinValue(1);
        np.setMaxValue(15);
        getJSON(ciudad1);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getJSON(ciudad1);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RealizarPronostico.this, RegistrarEvento.class);
                startActivity(myIntent);
            }
        });
    }

    public void getJSON(String ciud) {
        final String a = ""+np.getValue();
        String nuevaci="Trujillo";
        if(ciud==null){
            nomCiudad = "Trujillo";
            ciud = "Trujillo";
        }else{
            nuevaci=ciud;
            nomCiudad = ciud;
        }
        final String[] defini = new String[1];
        final String finalNuevaci = nuevaci;
        AsyncTask<Void, Void, Void> execute = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String api = "2884e0e33c8700895eb03582b8641712";
                    URL urlloc = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + nomCiudad + ",pel&units=metric&cnt=" + a + "&appid=2884e0e33c8700895eb03582b8641712&lang=es");
                    //URL url1 = new URL("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=ea574594b9d36ab688642d5fbeab847e&lang=es");
                    //URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+ nomCiudad +",pe&APPID=ea574594b9d36ab688642d5fbeab847e&lang=es");
                    HttpURLConnection connection = (HttpURLConnection) urlloc.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();
                    data = new JSONObject(json.toString());
                    if (data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("Exception " + e.getMessage());
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {
                    Log.d("datos recibidos: ", data.toString());
                    String text = "Hello, world!";

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();


                    try {
                        JSONArray are = data.getJSONArray("list");
                        String ciu = data.getJSONObject("city").getString("name");
                        int can = data.getInt("cnt");
                        String[] ass = new String[can];
                        int res=Calendar.DAY_OF_MONTH;
                        for(int i=0; i<can;i++){
                            ass[i] =  "Fecha: "+ (res)+"/0"+(Calendar.getInstance().MONTH+5) + "/2017\n" +
                                    "Descripción: "+are.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description")+ "\n" +
                                    "Temp. Max: "+are.getJSONObject(i).getJSONObject("temp").getDouble("max")+ "°"+
                                    "\nTemp. Min: "+ are.getJSONObject(i).getJSONObject("temp").getDouble("min")+ "°"+
                                    "\nPresión atmosferica: "+ are.getJSONObject(i).getString("pressure")+
                                    "\nVelocidad del viento: "+  are.getJSONObject(i).getDouble("speed")+ "m/s"+
                                    "\nAngulo del viento: "+are.getJSONObject(i).getInt("deg")+ "°"+
                                    "\nNubosidad: "+are.getJSONObject(i).getInt("clouds")+"%";



                            res++;

                        }
                        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(RealizarPronostico.this, android.R.layout.simple_list_item_1, ass);
                lstDatos.setAdapter(adaptador);
                        data.getInt("cod");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        }.execute();


    }
}
