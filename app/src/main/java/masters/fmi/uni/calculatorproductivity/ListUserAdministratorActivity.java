package masters.fmi.uni.calculatorproductivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListUserAdministratorActivity extends AppCompatActivity {

    User userFromActivity;
    ListView listUserToEdit;
    AdapterUserListAdministrator adapter;

    ArrayList<User> userListToEdit=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_administrator);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        listUserToEdit=findViewById(R.id.listUsertoEditET);

        new GetUser().execute();

        adapter = new AdapterUserListAdministrator(this,userListToEdit);
        listUserToEdit.setAdapter(adapter);

        listUserToEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(),userListToEdit.get(i).getId()+"",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ListUserAdministratorActivity.this,UserEditDeleteActivity.class);
                intent.putExtra("userToEdit",userListToEdit.get(i));
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });


    }

    //Get Users to list in ListView
    private class GetUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(ListUserAdministratorActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/get_user_to_edit.php"                            );
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
                        user.setEmaill(  json.getString("email"));
                        user.setSupervisor_id(json.getInt("supervisor_id"));
                        user.setRole_id(json.getInt("role_id"));
                        userListToEdit.add(user);
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
}
