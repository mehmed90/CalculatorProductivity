package masters.fmi.uni.calculatorproductivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class ProductivityAdminActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo;

    TextView dateFrom;
    TextView dateTo;
    Button checkProduc;
    Button backToPreviusActivity;

    User userFromActivity;

    Spinner userListSpiner;

    Map<String,Integer> hashUserSpiner = new HashMap<>();
    ArrayList<String> listAdminUserSpiner= new ArrayList<>();
    Map<Integer,String> hashUuserToSend = new HashMap<>();

    ArrayAdapter adapter;
    int id_userToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_admin);

        dateFrom=findViewById(R.id.dateFromAdmin);
        dateTo=findViewById(R.id.dateToAdmin);
        checkProduc=findViewById(R.id.CheckProdAdmin);
        backToPreviusActivity=findViewById(R.id.backBtToAdmin);

        userListSpiner=findViewById(R.id.spinnerUserAdmin);

        checkProduc.setEnabled(false);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        backToPreviusActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductivityAdminActivity.this,AdministratorActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
                finish();
            }
        });


        //DatePicker start ----------------------------------
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProductivityAdminActivity.this,
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
                        ProductivityAdminActivity.this,
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

        listAdminUserSpiner.add("Изберете потребител");
        adapter = new ArrayAdapter(ProductivityAdminActivity.this, android.R.layout.simple_spinner_dropdown_item, listAdminUserSpiner);
        userListSpiner.setAdapter(adapter);
        new GetUser().execute();


        // Get the ID of selected user----------------------------------------------
        userListSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                {
                    checkProduc.setEnabled(true);

                    id_userToSend=hashUserSpiner.get(listAdminUserSpiner.get(i));
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
        //------- end get ID of selected User--------------------------------

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
                        Toast.makeText(ProductivityAdminActivity.this, "Датата трябва да е по малка от "+date2, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        new getProductivity(hashUuserToSend.get(id_userToSend),id_userToSend,dateFrom.getText().toString(),dateTo.getText().toString()).execute();
                    }


                }

            }
        });
    }
// izvlichane na produktivnosta.
    private class getProductivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(ProductivityAdminActivity.this);
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
                Intent intent = new Intent(ProductivityAdminActivity.this, ProductivityWorkerActivity.class);
                intent.putExtra("user",userFromActivity);
                intent.putExtra("productivity",productivity);
                intent.putExtra("countday",countday);
                intent.putExtra("datefrom",dateFrom);
                intent.putExtra("dateto",dateTo);
                intent.putExtra("role",2);
                intent.putExtra("nameLastName",nameLastName);
                startActivity(intent);
            }

        }
    }

    private class GetUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(ProductivityAdminActivity.this);

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

                        listAdminUserSpiner.add("ID: "+json.getInt("id_user")+" "+json.getString("name"));
                        hashUuserToSend.put(json.getInt("id_user"),json.getString("name")+" "+json.getString("last_name"));
                        hashUserSpiner.put("ID: "+json.getInt("id_user")+" "+json.getString("name"),json.getInt("id_user"));
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
