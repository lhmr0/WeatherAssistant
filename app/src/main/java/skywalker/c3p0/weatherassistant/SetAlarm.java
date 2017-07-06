package skywalker.c3p0.weatherassistant;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.util.Calendar;
import java.util.Random;
import java.util.Set;

import skywalker.c3p0.weatherassistant.clases.AlarmReceiver;

public class SetAlarm extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView;

    private AlarmReceiver alarm;


    SetAlarm inst;
    Context context;
    SharedPreferences sp,sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
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
                            Intent myIntent = new Intent(SetAlarm.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(SetAlarm.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(SetAlarm.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(SetAlarm.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(SetAlarm.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(SetAlarm.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();
        if(nom==null){
            Intent myIntent = new Intent(SetAlarm.this, LoginActivity.class);
            startActivity(myIntent);
        }

        //alarm = new AlarmReceiver();
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        final Intent myIntent = new Intent(SetAlarm.this, AlarmReceiver.class);

        // Get the alarm manager service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // set the alarm to the time that you picked
        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);



        Button start_alarm= (Button) findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)

            @Override
            public void onClick(View v) {

                calendar.add(Calendar.SECOND, 3);
                //setAlarmText("You clicked a button");

                final int hour = alarmTimePicker.getCurrentHour();
                final int minute = alarmTimePicker.getCurrentMinute();;

                Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
               // setAlarmText("You clicked a " + hour + " and " + minute);


                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND,0);

                myIntent.putExtra("extra", "yes");
                pending_intent = PendingIntent.getBroadcast(SetAlarm.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);


                // now you should change the set Alarm text so it says something nice


                setAlarmText("Alarma establecida para las " + hour + ":" + minute);
                //Toast.makeText(getApplicationContext(), "You set the alarm", Toast.LENGTH_SHORT).show();
            }

        });

        Button stop_alarm= (Button) findViewById(R.id.stop_alarm);
        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int min = 1;
                int max = 9;

                Random r = new Random();
                int random_number = r.nextInt(max - min + 1) + min;
                Log.e("random number is ", String.valueOf(random_number));

                myIntent.putExtra("extra", "no");
                sendBroadcast(myIntent);

                alarmManager.cancel(pending_intent);
                setAlarmText("Alarma cancelada");
                //setAlarmText("You clicked a " + " canceled");
            }
        });

    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }



    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("MyActivity", "on Destroy");
    }


}
