package skywalker.c3p0.weatherassistant.clases;

/**
 * Created by C3P0 on 5/13/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Datos extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private ArrayList results=null;

    public Datos(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version){
        super(context,nombre,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("Create TABLE EVENTOS(USUARIO TEXT, NOMBRE TEXT, FECHA TEXT, DESCRIPCION TEXT, LINK TEXT);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnt, int versionNue){
        db.execSQL("DROP IF EXISTS EVENTOS;");
        db.execSQL("Create TABLE EVENTOS(USUARIO TEXT, NOMBRE TEXT, FECHA TEXT, DESCRIPCION TEXT, LINK TEXT);");
    }

    public ArrayList openAndQueryDatabase(String nom) {
       // String[] sSalida = new String[] { "","","","" };
        try {
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT NOMBRE, FECHA, DESCRIPCION, LINK FROM EVENTOS Where USUARIO='"+nom+"';", null);
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                      //  sSalida = new String[] { };
                        results.add(c.getString(0)+ " "+ c.getString(1)+ " "+  c.getString(2)+ " "+  c.getString(3));
                    }while (c.moveToNext());
                }
            }
        } catch (Exception ex) {
            ex.getClass().getSimpleName();
        }
        return results;
    }

    public String[] ConsultaTodos(String nom) {
        try {
            db = this.getReadableDatabase();
            Cursor filas = db.rawQuery(
                    "SELECT NOMBRE, FECHA, DESCRIPCION, LINK FROM EVENTOS Where USUARIO='"+nom+"';", null);
            int nRo = filas.getCount();
            int i = 0;
            String[] sSalida = new String[nRo];
            if (filas.moveToFirst())
                do {
                    sSalida[i] = "Título: "+filas.getString(0) + "\n Fecha: " + filas.getString(1)
                            + "\n Descripción: " + filas.getString(2) + "\n Link: "+ filas.getString(3);
                    i++;
                } while (filas.moveToNext());
            db.close();
            return sSalida;
        } catch (Exception ex) {
            String[] sSalida = new String[] { "ERROR", ex.getMessage() };
            return sSalida;
        }
    }

    public String[] Consulta(String nom) {
        String[] sSalida = new String[] { "","","","" };
        try {
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT NOMBRE, FECHA, DESCRIPCION, LINK FROM EVENTOS Where USUARIO='"+nom+"';", null);
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        sSalida = new String[] {c.getString(0), c.getString(1), c.getString(2),
                                c.getString(3) };
                    }while (c.moveToNext());
                }
            }
        } catch (Exception ex) {
            ex.getClass().getSimpleName();
        }
        return sSalida;
    }

    public String Insertar(String usuario, String titulo, String fecha, String desc, String link){
        String salida="";
        try{
            db= this.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("Usuario", usuario);
            registro.put("Nombre", titulo);
            registro.put("Fecha", fecha);
            registro.put("Descripcion", desc);
            registro.put("Link", link);
            long cont = db.insert("Eventos", null, registro);
            db.close();
            if (cont > -1)return "OK";
            else return "Codigo ya existe, no lo registro";

        }catch(Exception ex){
            salida = "Error:"+ex.getMessage();
            return salida;
        }
    }
}
