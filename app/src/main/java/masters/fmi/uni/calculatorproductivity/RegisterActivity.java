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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText lastname;
    EditText email;
    EditText password;
    EditText repeatpassword;

    Button okButton;
    Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.usernameEditText);
        lastname=findViewById(R.id.lastnameEditText);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditText);
        repeatpassword=findViewById(R.id.secondPasswordEditText);
        okButton=findViewById(R.id.okButton);
        cancelButton=findViewById(R.id.cancelButton);

        okButton.setOnClickListener(onClick);
        cancelButton.setOnClickListener(onClick);

    }
    View.OnClickListener onClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.okButton:

                    if(username.getText().length() == 0 || lastname.getText().length()==0 ||
                            email.getText().length()==0 ||
                            password.getText().length() == 0 ||
                            !password.getText().toString().
                                    equals(repeatpassword.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Всичките полета са задължителни!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    User user = new User( username.getText().toString(),
                            lastname.getText().toString(),email.getText().toString(),password.getText().toString());
                    new RegisterAsyncTask(user).execute();

                case R.id.cancelButton:
                    goToLogin();
                    break;
            }

        }
    };

    private void goToLogin() {
        Intent intent = new Intent(
                RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private class RegisterAsyncTask extends AsyncTask<Void, Void, Void>
    {

        User user;
        ProgressDialog dialog;
        boolean success = false;

        RegisterAsyncTask(User user){
            this.user = user;
            dialog = new ProgressDialog(RegisterActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Регистриране...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.format(
                    "http://android-projects.000webhostapp.com/register_user.php/?name=%s&lastname=%s&password=%s&email=%s"
                    , user.getName(),user.getLastname(), user.getPassword(),user.getEmaill());

            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String line = reader.readLine();

                if(line != null && line.contains("true")){
                    success = true;
                }else{
                    success = false;
                }

            }catch(java.io.IOException e){
                Log.wtf("Error" , e.getMessage());
            }finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialog.hide();

            if(success){
                Toast.makeText(RegisterActivity.this,
                        "Регистрирането е успешно.", Toast.LENGTH_LONG).show();
                goToLogin();
            }else{
                Toast.makeText(RegisterActivity.this,
                        "Грешка в записването на базата данни", Toast.LENGTH_LONG).show();
            }
        }
    }
}
