package masters.fmi.uni.calculatorproductivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SupervisorActivity extends AppCompatActivity {

    ListView listUsers;
    User userFromActivity;

    EditText input;
    Adapter adapter;

    Spinner spinUserSuper;
    ArrayAdapter adapterUsers;

    //ArrayList<User> UsertoSend= new ArrayList<>();
    Map<Integer,String> hashUuserToSend = new HashMap<>();
    ArrayList<String> listSuperUser= new ArrayList<>();
    Map<String,Integer> hashUserSuper = new HashMap<>();

    ArrayList<CustomUserForSupervisor> userList= new ArrayList<>();

    private DatePickerDialog.OnDateSetListener mDateSetListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo;

    TextView dateFrom;
    TextView dateTo;
    Button checkProduc;
    int id_userToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        dateFrom=findViewById(R.id.dateFromSuper);
        dateTo=findViewById(R.id.dateToSuper);
        checkProduc=findViewById(R.id.CheckProdSuper);
        checkProduc.setEnabled(false);

        listUsers=findViewById(R.id.listUserSuper);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");


        new GetUser(userFromActivity.getId()).execute();
        adapter = new Adapter(this,userList);

        listUsers.setAdapter(adapter);

        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {

                final int id_operation=userList.get(j).getId_operation();
                final int list_id=j;


                AlertDialog.Builder builder= new AlertDialog.Builder(SupervisorActivity.this);
                builder.setMessage("Моля потвърдете заработените бройки").setCancelable(false);

                input=new EditText(SupervisorActivity.this);
                builder.setView(input);
                input.setText(userList.get(j).getQuantity()+"");
                        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(input.getText().length()==0)
                                {
                                    Toast.makeText(getApplicationContext(),"Моля въведете бройки",Toast.LENGTH_LONG).show();
                                    return;
                                }else
                                {
                                    boolean digitsOnly = TextUtils.isDigitsOnly(input.getText());
                                    if(digitsOnly)
                                    {
                                        int num = Integer.parseInt(input.getText().toString());
                                        //  Toast.makeText(getApplicationContext(),id_operation+"",Toast.LENGTH_LONG).show();

                                        new ConfirmBroiki(id_operation,num,list_id).execute();

                                    }
                                    else
                                        Toast.makeText(getApplicationContext(),"Трябва да съдържа само числа",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(getApplicationContext(),"Вие не потвърдихте бройките",Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog title =builder.create();
                title.setTitle("Потвърждаване на бройки");
                title.show();
            }
        });

        spinUserSuper=findViewById(R.id.spinnerUserSuper);

        listSuperUser.add("Изберете потребител");
        adapterUsers = new ArrayAdapter(SupervisorActivity.this, android.R.layout.simple_spinner_dropdown_item, listSuperUser);
        spinUserSuper.setAdapter(adapterUsers);
        new GetUserSupervisor(userFromActivity.getId()).execute();

        //DatePicker start ----------------------------------
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SupervisorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerFrom,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SupervisorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerTo,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListenerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "-" + month + "-" + year;
                dateFrom.setError(null);
                dateFrom.setText(date);
            }
        };
        mDateSetListenerTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "-" + month + "-" + year;
                dateTo.setText(date);
            }
        };

        ////end of datePicker------------------------------------

        spinUserSuper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                {
                    checkProduc.setEnabled(true);

                    id_userToSend=hashUserSuper.get(listSuperUser.get(i));
                }
                else
                {
                    checkProduc.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        checkProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dateFrom.getText().length()==0 || dateTo.getText().length()==0){
                    Toast.makeText(getApplicationContext(),"И двете полета са задължителни",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Date date1=null,date2 = null;
                    String sDateFrom=dateFrom.getText().toString();
                    String sDateTo=dateTo.getText().toString();
                    try {
                        date1=new SimpleDateFormat("dd-MM-yyyy").parse(sDateFrom);
                        date2=new SimpleDateFormat("dd-MM-yyyy").parse(sDateTo);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date1.getTime()> date2.getTime())
                    {
                        dateFrom.setError("Изберете начална дата");
                        Toast.makeText(SupervisorActivity.this, "Датата трябва да е по малка от "+date2, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new getProductivity(hashUuserToSend.get(id_userToSend),id_userToSend,dateFrom.getText().toString(),dateTo.getText().toString()).execute();
                    }


                }

            }
        });

    }
    //izbirane na produktivnost za dadeniqt potrebitel izbran ot supervisor account

    private class getProductivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SupervisorActivity.this);
        int id;
        String dateFrom;
        String dateTo;
        double productivity=1;
        int countday=0;
        String nameLastName;

        getProductivity(String nameLastName,int id, String dateFrom, String dateTo)
        {
            this.nameLastName=nameLastName;
            this.id=id;
            this.dateFrom=dateFrom;
            this.dateTo=dateTo;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/get_productivity.php/?id_user=%s&datefrom=%s&dateto=%s",
                            id,dateFrom,dateTo);

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream));

                String line = reader.readLine();

                if (line != null)
                {
                    JSONArray array = new JSONArray(line);
                    String datee=array.getJSONObject(0).getString("date");
                    countday++;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        productivity=productivity+(json.getDouble("price")*json.getInt("quantity"));

                        if(datee.equals(json.getString("date"))){

                        }
                        else
                            countday++;
                        datee=json.getString("date");
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
            if(productivity==1.00)
            {

                Toast.makeText(getApplicationContext(),"Нямате продуктивност за избрания период",Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent intent = new Intent(SupervisorActivity.this, ProductivityWorkerActivity.class);
                intent.putExtra("user",userFromActivity);
                intent.putExtra("productivity",productivity);
                intent.putExtra("countday",countday);
                intent.putExtra("datefrom",dateFrom);
                intent.putExtra("dateto",dateTo);
                intent.putExtra("role",3);
                intent.putExtra("nameLastName",nameLastName);
                startActivity(intent);
            }

        }
    }



    //izvlichane na potrebitelite koito sa v grupata na daden Supervisor
    private class GetUserSupervisor extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SupervisorActivity.this);
        int id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане...");
            dialog.show();
        }
        GetUserSupervisor(int id)
        {
            this.id=id;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/supervisor/get_user_supervisor.php/?userid=%s",
                            id);
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream));

                String line = reader.readLine();

                if (line != null)
                {
                    JSONArray array = new JSONArray(line);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        listSuperUser.add(json.getString("name"));
                        hashUserSuper.put(json.getString("name"),json.getInt("id_user"));
                        hashUuserToSend.put(json.getInt("id_user"),json.getString("name")+" "+json.getString("last_name"));
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
            adapterUsers.notifyDataSetChanged();
        }
    }

    //potvyrjdavane na broikite v bazata danni
    private class ConfirmBroiki extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SupervisorActivity.this);

        int id_operation;
        int broiki;
        int list_id;
        boolean success;

        ConfirmBroiki(int id_operation,int broiki,int list_id){
            this.id_operation=id_operation;
            this.broiki=broiki;
            this.list_id=list_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Потвърждаване на бройки");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/supervisor/confirm_broiki.php?quantity=%s&id=%s",
                            broiki,id_operation);
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
                Toast.makeText(SupervisorActivity.this,
                        "Потвърждаването на бройките е успешно.", Toast.LENGTH_LONG).show();
                //userList.clear();
                userList.remove(list_id);
                //new GetUser(userFromActivity.getId()).execute();
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(SupervisorActivity.this,
                        "Грешка в обновяването", Toast.LENGTH_LONG).show();
            }
        }
    }
    private class GetUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SupervisorActivity.this);

        int id;

        GetUser(int id){
            this.id=id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/supervisor/get_user.php/?userid=%s",
                            id);
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
                        CustomUserForSupervisor user = new CustomUserForSupervisor();
                        user.setId_operation(json.getInt("id"));
                        user.setId(json.getInt("id_user"));
                        user.setName(json.getString("name"));
                        user.setQuantity(json.getInt("quantity"));
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
}
