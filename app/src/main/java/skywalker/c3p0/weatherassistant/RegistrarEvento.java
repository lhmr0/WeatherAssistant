package skywalker.c3p0.weatherassistant;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import skywalker.c3p0.weatherassistant.clases.Datos;
import skywalker.c3p0.weatherassistant.clases.FilePath;
import skywalker.c3p0.weatherassistant.clases.InputFilterMinMax;

public class RegistrarEvento extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedFilePath;
    private String SERVER_URL = "http://lhmr0.000webhostapp.com/upload.php";
    private Datos a=new Datos(this,"BDEventos.db",null,1);
    ImageView ivAttachment;
    Button bUpload, bRegistro, bVer, bSinc;
    TextView tvFileName;
    EditText nombre, descrip, dia, mes, ano;
    ProgressDialog dialog;
    String urlfin;
    SharedPreferences sp,sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_evento);
        sp = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("ciudad", Context.MODE_PRIVATE);
        final String nom = sp.getString(LoginActivity.Name,null);
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
                            Intent myIntent = new Intent(RegistrarEvento.this, MainActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item3){
                            Intent myIntent = new Intent(RegistrarEvento.this, RegistrarEvento.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item4){
                            Intent myIntent = new Intent(RegistrarEvento.this, VerEventos.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item5){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(LoginActivity.Name);
                            editor.commit();
                            LoginManager.getInstance().logOut();
                            Intent myIntent = new Intent(RegistrarEvento.this, LoginActivity.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item6){
                            Intent myIntent = new Intent(RegistrarEvento.this, SetAlarm.class);
                            startActivity(myIntent);
                            return true;
                        }else if(drawerItem==item7){
                            Intent myIntent = new Intent(RegistrarEvento.this, RealizarPronostico.class);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    }


                })
                .build();
        if(nom==null){
            Intent myIntent = new Intent(RegistrarEvento.this, LoginActivity.class);
            startActivity(myIntent);
        }
        dia = (EditText) findViewById(R.id.txtFechaDia);
        mes = (EditText) findViewById(R.id.txtFechaMes);
        ano = (EditText) findViewById(R.id.txtFechaAno);
        bRegistro = (Button)findViewById(R.id.b_registro);
        bVer = (Button)findViewById(R.id.btnVerEvento);
        bSinc = (Button)findViewById(R.id.btnSincronizar);
        bVer.setVisibility(View.INVISIBLE);
        bSinc.setVisibility(View.INVISIBLE);
        dia.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
        mes.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
        ano.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        dia.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "31")});
        mes.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "12")});
        ano.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "2050")});
        nombre = (EditText)findViewById(R.id.txtNombreEvento);
        descrip = (EditText)findViewById(R.id.txtDescripcion) ;
        ivAttachment = (ImageView) findViewById(R.id.ivAttachment);
        bUpload = (Button) findViewById(R.id.b_upload);
        tvFileName = (TextView) findViewById(R.id.tv_file_name);
        ActivityCompat.requestPermissions(RegistrarEvento.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR},1);
        ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFilePath != null){
                    dialog = ProgressDialog.show(RegistrarEvento.this,"","Subiendo archivo...",true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            uploadFile(selectedFilePath);
                        }
                    }).start();
                }else{
                    Toast.makeText(RegistrarEvento.this,"Seleccione el archivo a subir",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String aa= a.Insertar("Luis Marin", nombre.getText().toString(),dia.getText().toString()+"/"+mes.getText().toString()+"/"+ano.getText().toString(), descrip.getText().toString(), urlfin);
                String aa= a.Insertar(nom, nombre.getText().toString(),dia.getText().toString()+"/"+mes.getText().toString()+"/"+ano.getText().toString(), descrip.getText().toString(), urlfin);
                // String b = a.Insertar("Luis Marin", "Siembra","11/11/1111", "csm","asd");

                nombre.setEnabled(false);
                dia.setEnabled(false);
                mes.setEnabled(false);
                ano.setEnabled(false);
          descrip.setEnabled(false);

                ivAttachment.setVisibility(View.INVISIBLE);
                bUpload.setVisibility(View.INVISIBLE);
                bRegistro.setVisibility(View.INVISIBLE);
                bVer.setVisibility(View.VISIBLE);
                bSinc.setVisibility(View.VISIBLE);
                   // inserDB("Luis Marin","Cosecha","17/07/2017","asdasd","asdasd");

            }
        });
        bVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegistrarEvento.this, VerEventos.class);
                startActivity(myIntent);
            }
        });
        bSinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(RegistrarEvento.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(RegistrarEvento.this, "No se han asignado los permisos", Toast.LENGTH_LONG).show();

                }else if(ActivityCompat.checkSelfPermission(RegistrarEvento.this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){


                    long calID = 3;
                    long startMillis = 0;
                    long endMillis = 0;
                    int  day = Integer.parseInt(dia.getText().toString());
                    int month = Integer.parseInt(mes.getText().toString());
                    int year = Integer.parseInt(ano.getText().toString());
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(year, month-1, day, 5, 00);
                    startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(year, month-1, day, 18, 00);
                    endMillis = endTime.getTimeInMillis();


                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.DTEND, endMillis);
                    values.put(CalendarContract.Events.TITLE, nombre.getText().toString());
                    values.put(CalendarContract.Events.DESCRIPTION, descrip.getText().toString() + " \n LINK: "+urlfin);
                    values.put(CalendarContract.Events.CALENDAR_ID, 4);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);



                  tvFileName.setText("Sincronizado con Calendar!");

                    Toast.makeText(RegistrarEvento.this, "Se ha sincronizado", Toast.LENGTH_LONG).show();


                }

            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    showFileChooser(); //a sample method called

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(RegistrarEvento.this, "Permisos denegados", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Escoja un archivo a subir"),PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    tvFileName.setText(selectedFilePath);
                }else{
                    Toast.makeText(this,"No se puede subir el archivo",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFileName.setText("El archivo de origen no esxistet: " + selectedFilePath);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFileName.setText("Archivo subido, puedes ver el archivo en:" + "http://lhmr0.000webhostapp.com/uploads/"+ fileName);
                    urlfin = "http://lhmr0.000webhostapp.com/uploads/"+ fileName;
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrarEvento.this,"Archivo no encontrado",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(RegistrarEvento.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(RegistrarEvento.this, "No se puede acceder al archivo!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }
}
