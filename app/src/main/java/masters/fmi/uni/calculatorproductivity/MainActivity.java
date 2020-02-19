package masters.fmi.uni.calculatorproductivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button login;
    Button register;
    EditText emailET;
    EditText passET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=findViewById(R.id.loginButton);
        register=findViewById(R.id.registerButton);

        emailET=findViewById(R.id.emailET);
        passET=findViewById(R.id.passwordET);

        login.setOnClickListener(onClick);
        register.setOnClickListener(onClick);

    }

    private View.OnClickListener onClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.registerButton:
                    intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.loginButton:
                    String email = emailET.getText().toString();
                    email=email.toLowerCase();
                    String password = passET.getText().toString();
                    if(emailET.getText().length()==0 || passET.getText().length()==0)
                    {
                        Toast.makeText(getApplicationContext(),"И двете полета са задължителни",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        new LoginAsycnTask(email,password).execute();

                    }


             //       Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();


                    break;
            }

        }
    };

    private class LoginAsycnTask extends AsyncTask<Void, Void, Void>
    {
        String email;
        String password;
        User user;
        ProgressDialog dialog;

        LoginAsycnTask(String email, String password){
            this.email = email;
            this.password = password;
            dialog = new ProgressDialog(MainActivity.this);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Вписване...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/login_user.php/?email=%s&password=%s",
                            email, password);
            HttpURLConnection urlConnection = null;

            try{
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream));

                String line = reader.readLine();

                if(line != null){

                    JSONObject object = new JSONObject(line);
                    user = new User(object.getString("name"),object.getString("last_name"),email,password);
                    user.setId(object.getInt("id_user"));
                    user.setRole_id(object.getInt("role_id"));
                    user.setSupervisor_id(object.getInt("supervisor_id"));
                    if(object.getInt("activated")==1)
                        user.setActivated(true);
                    else
                        user.setActivated(false);


                }else{
                    user = null;
                }


            }catch (IOException e){
                Log.wtf("Error", e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(user != null)
            {

               if(user.isActivated())
               {
                   Intent intent=null;
                   switch (user.getRole_id())
                   {
                       case 1:
                           intent = new Intent(MainActivity.this, WorkerActivity.class);
                           intent.putExtra("user",user);
                           startActivity(intent);
                           dialog.hide();
                           break;
                       case 2:
                           intent = new Intent(MainActivity.this, AdministratorActivity.class);
                           intent.putExtra("user",user);
                           startActivity(intent);
                           dialog.hide();
                           break;
                       case 3:
                           intent = new Intent(MainActivity.this, SupervisorActivity.class);
                           intent.putExtra("user",user);
                           startActivity(intent);
                           dialog.hide();
                           break;
                   }
               }
               else
               {
                   Toast.makeText(MainActivity.this, "Акаунта чака одобрение от администратор",
                           Toast.LENGTH_LONG).show();
                   dialog.hide();

               }

            }
            else
            {
                Toast.makeText(MainActivity.this, "Грешен имейл или парола",
                        Toast.LENGTH_LONG).show();
                dialog.hide();
            }
        }



    }
}
