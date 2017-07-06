package skywalker.c3p0.weatherassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;

import skywalker.c3p0.weatherassistant.RegistrarEvento;
import skywalker.c3p0.weatherassistant.clases.Datos;

public class VerEventos extends AppCompatActivity {
    ListView lst;
    private Datos a= new Datos(VerEventos.this,"BDEventos.db",null,1);
    ArrayList ad = null;
    SharedPreferences sp,sp2;
    ArrayAdapter myCustomAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_eventos);
        sp = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("ciudad", Context.MODE_PRIVATE);
        String nom = sp.getString(LoginActivity.Name,null);
        String ciudad = sp2.getString("ciudadKey",null);
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
                            Intent myIntent = new Intent(VerEventos.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(VerEventos.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(VerEventos.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(VerEventos.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(VerEventos.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(VerEventos.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();
        if(nom==null){
            Intent myIntent = new Intent(VerEventos.this, LoginActivity.class);
            startActivity(myIntent);
        }
        lst = (ListView)findViewById(R.id.lstDatos);


       // ad = a.openAndQueryDatabase(nom);
        //myCustomAdapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,ad);
        //lst.setAdapter(myCustomAdapter);
        String[] datos = a.ConsultaTodos(nom);
        // 3. Definir un Adaptador para proporcionar los items para el control
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, datos);
        // 4. invocar al adaptador desde el control listview
        lst.setAdapter(adaptador);

    }

}
