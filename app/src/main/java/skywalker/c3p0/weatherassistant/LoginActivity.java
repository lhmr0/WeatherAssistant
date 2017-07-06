package skywalker.c3p0.weatherassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import skywalker.c3p0.weatherassistant.clases.SessionManagement;


public class LoginActivity extends AppCompatActivity {
    Bitmap mIcon;
      TextView stat;
    URL profile_pic;
    LoginButton but;
    CallbackManager cl;
    String user="hola";
    public static final String MyPrefs = "Weather";
    public static final String Name = "nameKey";
   // public static final String MyPrefs = "Weather";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
                setContentView(R.layout.activity_login);
        sp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        String nom = sp.getString(LoginActivity.Name,null);

        cl = CallbackManager.Factory.create();
        stat = (TextView)findViewById(R.id.txtEstado);
        but = (LoginButton)findViewById(R.id.login_button);

        if(nom==null){
            login();
        }
        else{
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
        }




    }

    public void onStop(Bundle savedInstanceState){

    }


    private String login(){
        final String[] logs = {""};
        LoginManager.getInstance().registerCallback(cl, new FacebookCallback<LoginResult>() {
            Bundle bFacebookData;
            @Override
            public void onSuccess(LoginResult loginResult) {
                //http://stackoverflow.com/questions/32196682/facebook-android-sdk-4-5-0-get-email-address
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);
                final String[] a = {""};
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("Mainctivity", response.toString());
                        // Get facebook data from login
                        bFacebookData = getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location,picture.type(large)"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
               Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
//               String nom = bFacebookData.getString("first_name")+" "+ bFacebookData.getString("last_name");
  //              myIntent.putExtra("user", nom);
                startActivity(myIntent);
                logs[0] = "OK";
            }

            @Override
            public void onCancel() {
                stat.setText("Cojudo, no canceles");
                logs[0] = "NO";

            }

            @Override
            public void onError(FacebookException error) {
                stat.setText("Ta cagado"+error.getMessage());
                logs[0] = "NO";
            }
        });
        return logs[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cl.onActivityResult(requestCode, resultCode, data);
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        String avatar="";
        try {

            String id = object.getString("id");



            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                avatar = profile_pic.toString();

                bundle.putString("profile_pic", profile_pic.toString());
                bundle.putString("first_name", object.getString("first_name"));
                bundle.putString("last_name", object.getString("last_name"));
                bundle.putString("idFacebook", id);

                if (object.has("first_name"))
                    bundle.putString("first_name", object.getString("first_name"));
                if (object.has("last_name"))
                    bundle.putString("last_name", object.getString("last_name"));
                if (object.has("email"))
                    bundle.putString("email", object.getString("email"));
                if (object.has("gender"))
                    bundle.putString("gender", object.getString("gender"));
                if (object.has("birthday"))
                    bundle.putString("birthday", object.getString("birthday"));
                if (object.has("location"))
                    bundle.putString("location", object.getJSONObject("location").getString("name"));
                stat.setText("Hola, "+object.getString("first_name")+" "+object.getString("last_name"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("nombre: ", object.getString("first_name")+" "+object.getString("last_name"));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Name, object.getString("first_name")+" "+object.getString("last_name"));
            //editor.putString("id", id);
            editor.commit();


            //user = object.getString("first_name").toString()+" "+object.getString("last_name").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }


}
