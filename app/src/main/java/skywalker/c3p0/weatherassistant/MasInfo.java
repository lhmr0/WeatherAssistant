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
import android.widget.Button;
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
import java.util.Date;

public class MasInfo extends AppCompatActivity {
    JSONObject data = null;
    String ciudadinp;
    TextView ubicacion, descripcion, humedad,presion,velocidad, direccion, salidasol, puestasol;
    SharedPreferences sp,sp2;
    Button pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas_info);
        sp = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("ciudad", Context.MODE_PRIVATE);
        String nom = sp.getString(LoginActivity.Name,null);
        String ciudad = sp2.getString("ciudadKey",null);
        ubicacion =(TextView)findViewById(R.id.txtUbi);
        descripcion  =(TextView)findViewById(R.id.txtDesc);
        humedad  =(TextView)findViewById(R.id.txtpre);
        presion =(TextView)findViewById(R.id.txtPresion);
        velocidad  =(TextView)findViewById(R.id.txtVelocidad);
        direccion =(TextView)findViewById(R.id.txtDireccion);
        salidasol =(TextView)findViewById(R.id.txtSalida);
        puestasol =(TextView)findViewById(R.id.txtPuesta);
        pro = (Button)findViewById(R.id.button2);
        new DrawerBuilder().withActivity(this).build();
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.app_name);
        final SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.info).withBadge(ciudad).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withIcon(FontAwesome.Icon.faw_cloud);
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
                            Intent myIntent = new Intent(MasInfo.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(MasInfo.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(MasInfo.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(MasInfo.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(MasInfo.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(MasInfo.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();
        if(nom==null){
            Intent myIntent = new Intent(MasInfo.this, LoginActivity.class);
            startActivity(myIntent);
        }

        getJSON(ciudad);
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MasInfo.this, RealizarPronostico.class);
                startActivity(myIntent);
            }
        });
    }


    public String hora(int milliseg){
        Date sun = new Date(milliseg*1000);
        int hor = sun.getHours()-3;
        int min = sun.getMinutes()-3;
        return " "+hor+":"+min+"";
    }

    public String getJSON(final String ciud) {
        final String[] defini = new String[1];
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String api = "2884e0e33c8700895eb03582b8641712";
                    String ci = "3691175";
                    String lat="-8.116";
                    String lon="-79.03";
                    URL urlloc = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Trujillo,pel&units=metric&cnt=1&appid=2884e0e33c8700895eb03582b8641712");
                    URL url1 = new URL("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=2884e0e33c8700895eb03582b8641712");
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+ciud+",pe&APPID=ea574594b9d36ab688642d5fbeab847e&lang=es");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();
                    data = new JSONObject(json.toString());
                    if(data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
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
                        int humidity = data.getJSONObject("main").getInt("humidity");
                        int pressure = data.getJSONObject("main").getInt("pressure");
                        double speed = data.getJSONObject("wind").getDouble("speed");
                        int deg = data.getJSONObject("wind").getInt("deg");
                        JSONArray are = data.getJSONArray("weather");
                        are.getJSONObject(0).getString("description");


                        ciudadinp = data.getString("name");
                        ubicacion.setText(ciudadinp);
                        defini[0] = ciudadinp;
                        int sunrise = data.getJSONObject("sys").getInt("sunrise");
                        int sunset = data.getJSONObject("sys").getInt("sunset");
                        String amane = hora(sunrise);
                        String ocaso = hora(sunset);
                         descripcion.setText(are.getJSONObject(0).getString("description"));
                     humedad.setText(humidity+"%");
                        presion.setText(pressure+"");
                        velocidad.setText(speed+"m/s");
                        direccion.setText(deg+" °");

                        salidasol.setText(amane+" AM");
                        puestasol.setText(ocaso+" PM");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        }.execute();

        return defini[0];

    }
}
