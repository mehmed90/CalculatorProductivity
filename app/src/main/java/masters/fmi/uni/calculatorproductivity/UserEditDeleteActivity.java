package masters.fmi.uni.calculatorproductivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class UserEditDeleteActivity extends AppCompatActivity {

    EditText nameEdit;
    EditText lastNameEdit;
    EditText emailEdit;
    EditText roleEdit;
    EditText supervisorEdit;

    Button saveChange;
    Button resetPassowrd;
    Button backToLastActivity;
    Button deleteUser;

    User userToEdit;
    User userFromActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_delete);

        nameEdit=findViewById(R.id.editName);
        lastNameEdit=findViewById(R.id.editLastName);
        emailEdit=findViewById(R.id.editEmail);
        roleEdit=findViewById(R.id.editRole);
        supervisorEdit=findViewById(R.id.editSupervisor);

        saveChange=findViewById(R.id.saveChange);
        resetPassowrd=findViewById(R.id.resetPassword);
        backToLastActivity=findViewById(R.id.backBtToActivity);
        deleteUser=findViewById(R.id.deleteUser);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        userToEdit = (User) getIntent().getExtras().getSerializable("userToEdit");

        nameEdit.setText(userToEdit.getName());
        lastNameEdit.setText(userToEdit.getLastname());
        emailEdit.setText(userToEdit.getEmaill());
        roleEdit.setText(userToEdit.getRole_id()+"");
        supervisorEdit.setText(userToEdit.getSupervisor_id()+"");

        backToLastActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resetPassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new resetPassword(userToEdit.getId()).execute();

            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(UserEditDeleteActivity.this);
                builder.setMessage("Сигурни ли сте че искате да изтриете потребителя?").setCancelable(false);

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new deleteUser(userToEdit.getId()).execute();
                    }
                });
                builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog title =builder.create();
                title.setTitle("Изтриване на потребител.");
                title.show();

            }
        });

        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean digitsOnly1 = TextUtils.isDigitsOnly(roleEdit.getText());
                boolean digitsOnly2 = TextUtils.isDigitsOnly(supervisorEdit.getText());

                if(digitsOnly1 && digitsOnly2)
                {
                    new updateUser(userToEdit.getId(),
                            nameEdit.getText().toString(),
                            lastNameEdit.getText().toString(),
                            emailEdit.getText().toString(),
                            Integer.parseInt(roleEdit.getText().toString()),
                            Integer.parseInt(supervisorEdit.getText().toString())).execute();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Полетата Роля и Отговорник трябда да са числа!!!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private class updateUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(UserEditDeleteActivity.this);

        int idUser;
        boolean success;
        String name;
        String lastName;
        String email;
        int role;
        int supervisor;

        updateUser(int idUser, String name, String lastName, String email, int role, int supervisor){
            this.idUser=idUser;
            this.name=name;
            this.lastName=lastName;
            this.email=email;
            this.role=role;
            this.supervisor=supervisor;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Запазване на промените...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/update_user.php?id=%s&name=%s&lastname=%s&" +
                                    "email=%s&role=%s&supervisor=%s",
                            idUser,name,lastName,email,role,supervisor);
            HttpURLConnection urlConnection = null;
            try {
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

            } catch(java.io.IOException e){
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
                Intent intent = new Intent(UserEditDeleteActivity.this,ListUserAdministratorActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
                finish();
                Toast.makeText(UserEditDeleteActivity.this,
                        "Обновяването е успешно.", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(UserEditDeleteActivity.this,
                        "Грешка в обновяването", Toast.LENGTH_LONG).show();
            }
        }
    }

    //delete users from data base
    private class deleteUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(UserEditDeleteActivity.this);

        int idUser;
        boolean success;

        deleteUser(int idUser){
            this.idUser=idUser;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Изтриване на потребител...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/delete_user.php?id=%s",
                            idUser);
            HttpURLConnection urlConnection = null;
            try {
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

            } catch(java.io.IOException e){
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
                finish();
                Toast.makeText(UserEditDeleteActivity.this,
                        "Потребителят е изтрит успешно.", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(UserEditDeleteActivity.this,
                        "Грешка в изтриването на потребителя", Toast.LENGTH_LONG).show();
            }
        }
    }

    // reset password
    private class resetPassword extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(UserEditDeleteActivity.this);

        int idUser;
        boolean success;

        resetPassword(int idUser){
            this.idUser=idUser;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Нулиране на паролата");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/reset_password.php?id=%s",
                            idUser);
            HttpURLConnection urlConnection = null;
            try {
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

            } catch(java.io.IOException e){
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
                finish();
                Toast.makeText(UserEditDeleteActivity.this,
                        "Паролата е нулирана успешно.", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(UserEditDeleteActivity.this,
                        "Грешка в нулирането на парола", Toast.LENGTH_LONG).show();
            }
        }
    }
}
