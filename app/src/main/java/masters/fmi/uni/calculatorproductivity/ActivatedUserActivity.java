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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivatedUserActivity extends AppCompatActivity {

    ListView listUserToActivation;
    Button backToAdminActivity;

    User userFromActivity;

    ArrayList<User> userList= new ArrayList<>();

    Map<String,Integer> SupervisorNameHash = new HashMap<>();
    ArrayList<String> SupervisorName= new ArrayList<>();

    Spinner usersName;

    AdapterUserActivation adapter;

    ArrayAdapter adapterUser;

    int idSelectedUser;
    int idSelectedSupervisor;
    int selectedRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_user);

        listUserToActivation=findViewById(R.id.listUserToActivation);
        backToAdminActivity=findViewById(R.id.backBT);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        new getSupervisors().execute();
        new GetUser().execute();


        adapter = new AdapterUserActivation(this,userList);

        listUserToActivation.setAdapter(adapter);
        TextView textinfo= findViewById(R.id.textViewInfo);
       /* if(userList.isEmpty()){

            textinfo.setText("Няма потребители чакащи активация");
        }
        else
            textinfo.setText("Активиране на потребител");*/

        backToAdminActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AdministratorActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });
        listUserToActivation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {

                AlertDialog.Builder builder= new AlertDialog.Builder(ActivatedUserActivity.this);
                builder.setMessage("Моля изберете отговорник").setCancelable(false);

                usersName=new Spinner(ActivatedUserActivity.this);
                builder.setView(usersName);
                adapterUser = new ArrayAdapter(ActivatedUserActivity.this, android.R.layout.simple_spinner_dropdown_item, SupervisorName);
                usersName.setAdapter(adapterUser);

                idSelectedUser=userList.get(j).getId();
                selectedRow=j;

                // click spiner-----------------------------------------
                usersName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int m, long l) {

                       // Toast.makeText(getApplicationContext(),SupervisorNameHash.get(SupervisorName.get(m))+"",Toast.LENGTH_LONG).show();
                        idSelectedSupervisor=SupervisorNameHash.get(SupervisorName.get(m));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                // end clic spiner ----------------------------------


                builder.setPositiveButton("Активирай", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       /* Toast.makeText(getApplicationContext(),"Id selected user is "+idSelectedUser+
                                " Id selected Supervisor to assign is "+idSelectedSupervisor,Toast.LENGTH_LONG).show();
                        userList.remove(selectedRow);
                        adapter.notifyDataSetChanged();*/
                        new ActiveUser(idSelectedUser,idSelectedSupervisor,selectedRow).execute();

                    }
                });
              builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(getApplicationContext(),"Вие не Активирахте потребителя",Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog title =builder.create();
                title.setTitle("Активиране на потребител");
                title.show();

            }
        });
    }

// Активиране на потребител и свързване с даден отговорник на групата
    private class ActiveUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(ActivatedUserActivity.this);

        int idUser;
        int idSupervisor;
        int row;
        boolean success;

        ActiveUser(int idUser,int idSupervisor, int row)
        {
            this.idSupervisor=idSupervisor;
            this.idUser=idUser;
            this.row=row;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Активиране...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/activate_user.php?id_user=%s&id_supervisor=%s",
                    idUser,idSupervisor);
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
                Toast.makeText(ActivatedUserActivity.this,
                        "Успешно активирахте потребител.", Toast.LENGTH_LONG).show();
                userList.remove(row);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(ActivatedUserActivity.this,
                        "Грешка в активирането", Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

    // Извличане на потребителите които трябва да се активират.
    private class GetUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(ActivatedUserActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/get_user_to_activate.php"                            );
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();

                if (line != null) {
                    JSONArray array = new JSONArray(line);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        User user = new User(json.getInt("id_user"),json.getString("name"),
                                json.getString("last_name"));
                        userList.add(user);
                    }
                }
            } catch (java.io.IOException e) {
                Log.wtf("Error", e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            adapter.notifyDataSetChanged();
        }
    }
    // извличане на отговорните на групите за да може потребителя да бъде към избран отговорник
    private class getSupervisors extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/get_supervisors.php");
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();

                if (line != null) {
                    JSONArray array = new JSONArray(line);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        SupervisorNameHash.put(json.getString("name"),json.getInt("id_user"));
                        SupervisorName.add(json.getString("name"));
                    }
                }
            } catch (java.io.IOException e) {
                Log.wtf("Error", e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }
    }
}
