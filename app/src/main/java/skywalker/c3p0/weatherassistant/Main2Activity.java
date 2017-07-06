package skywalker.c3p0.weatherassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import skywalker.c3p0.weatherassistant.clases.GPSTracker;
import skywalker.c3p0.weatherassistant.clases.Loc;

public class Main2Activity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    TextView tempMax, tempMin, ciudad;
    JSONObject data = null;
    GPSTracker locateme;
    Button masinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tempMax = (TextView)findViewById(R.id.txtMax);
        tempMin = (TextView)findViewById(R.id.txtMin);
        ciudad = (TextView)findViewById(R.id.txtcity);
        masinfo = (Button)findViewById(R.id.btnMasInfo);

        new DrawerBuilder().withActivity(this).build();
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.app_name);
        final SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.info).withBadge("Trujillo").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withIcon(FontAwesome.Icon.faw_cloud);
        final SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.crear).withIcon(FontAwesome.Icon.faw_calendar_plus_o);
        final SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.alarm).withIcon(FontAwesome.Icon.faw_clock_o);
        final SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.ver).withIcon(FontAwesome.Icon.faw_calendar_check_o);
        final SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.log).withIcon(FontAwesome.Icon.faw_user);
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(false)
               .addDrawerItems(
                       item1,
                       new ProfileDrawerItem().withName("Luis Marin").withIcon(FontAwesome.Icon.faw_user_circle),
                       new DividerDrawerItem(),
                       item2,
                       item6,
                       item3,
                       item4,
                       item5,
                       new DividerDrawerItem(),
                       new SecondaryDrawerItem().withName("Perú - 2017").withSelectable(false)
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem==item2){
                        Intent myIntent = new Intent(Main2Activity.this, LoginActivity.class);
                        startActivity(myIntent);
                        return true;
                        }else if(drawerItem==item3){
                                Intent myIntent = new Intent(Main2Activity.this, Upload.class);
                                startActivity(myIntent);
                                return true;
                            }else if(drawerItem==item4){
                            Intent myIntent = new Intent(Main2Activity.this, Upload.class);
                            startActivity(myIntent);
                            return true;
                        }
                    return true;
                    }


                })
                .build();




       // getJSON(lat, lon);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        //getJSON(ubi().getLat(),ubi().getLon());
            ciudad.setText(""+ubi().getLat()+ ubi().getLon());
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    12281);
          //  getJSON(ubi().getLat(),ubi().getLon());
            ciudad.setText(""+ubi().getLat()+ ubi().getLon());
        }

    }



    public String hora(){
        Date sunriseDate = new Date();
        Date sun = new Date(1498172880*1000);
        int hor = sun.getHours();
        int min = sun.getMinutes();
        System.out.println(hor+":"+min+" AM");
        return "Hora: "+hor+":"+min+"";
    }

    public Loc ubi(){
        Loc lc = new Loc();
        double latitude=1;
        double longitude=1;

        locateme = new GPSTracker(Main2Activity.this);
        if(locateme.canGetLocation()) {
            latitude = locateme.getLatitude();
            longitude = locateme.getLongitude();
            lc.setLat(latitude);
            lc.setLon(longitude);
        }
        return lc;
    }

    public void getJSON(final double lati, final double longi) {

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
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Trujillo,pe&APPID=ea574594b9d36ab688642d5fbeab847e");

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
                        double max = data.getJSONObject("main").getDouble("temp_max");
                        double min = data.getJSONObject("main").getDouble("temp_min");
                        String maxi = ""+max/16;
                        String mini = ""+min/16;
                        String ciudadinp = data.getString("name");
                        tempMax.setText("máx: "+maxi);
                        tempMin.setText("mín: "+mini);
                        ciudad.setText("Estás en : "+ciudadinp);
                        int sunrise = data.getJSONObject("sys").getInt("sunrise");
                        int sunset = data.getJSONObject("sys").getInt("sunset");
                        Log.d("hola", max+" , "+min+", "+sunrise+" ,"+sunset);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        }.execute();

    }
}
