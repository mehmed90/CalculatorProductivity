package masters.fmi.uni.calculatorproductivity;

import android.app.Activity;
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
import android.widget.EditText;
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

public class WorkerActivity extends AppCompatActivity {

    Map<String,Integer> hashGroup = new HashMap<>();

    Map<String,Integer> hashOperation = new HashMap<>();

    ArrayList<String> listCategory = new ArrayList<>();

    ArrayList<String> listOperation = new ArrayList<>();

    Spinner spincategory;
    Spinner spinOperation;
    TextView textOperation;
    ArrayAdapter adapter;
    ArrayAdapter adapterOperation;
    EditText inputBroiki;
    Button inputBT;
    User user;
    int id_operation;


    private DatePickerDialog.OnDateSetListener mDateSetListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo;

    TextView dateFrom;
    TextView dateTo;
    Button checkProduc;
    Button BackBtToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        

        spincategory=findViewById(R.id.listCategorySpiner);

        spinOperation=findViewById(R.id.listOperationSpiner);

        inputBT=findViewById(R.id.inputBT);

        inputBroiki=findViewById(R.id.broikiET);

        inputBroiki.setVisibility(View.INVISIBLE);
        inputBT.setEnabled(false);

        // Get date from and to, to chek productivity

        dateFrom=findViewById(R.id.dateFromET);
        dateTo=findViewById(R.id.dateToET);
        checkProduc=findViewById(R.id.checkProdBT);
        BackBtToLogin=findViewById(R.id.BackBtToLogin);
        BackBtToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //datePicker------------------------------------------------------------------------------

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        WorkerActivity.this,
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
                        WorkerActivity.this,
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

        spinOperation.setVisibility(View.GONE);

        user = (User) getIntent().getExtras().getSerializable("user");

        listCategory.add("Изберете категория");
        listOperation.add("Изберете Операция");
        adapter = new ArrayAdapter(WorkerActivity.this, android.R.layout.simple_spinner_dropdown_item, listCategory);

        spincategory.setAdapter(adapter);

        new GetOperationGroup().execute();

        adapterOperation = new ArrayAdapter(WorkerActivity.this, android.R.layout.simple_spinner_dropdown_item, listOperation);
        spinOperation.setAdapter(adapterOperation);

        spincategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if(i>0)
               {
                   spinOperation.setVisibility(View.VISIBLE);

                   // izvlichane ot bazata danni operaciite za izbranata grupa
                   listOperation.removeAll(listOperation);
                   listOperation.add("Изберете Операция");
                   new GetOperation( hashGroup.get(listCategory.get(i))).execute();
               }
               else
               {
                   spinOperation.setVisibility(View.GONE);
                   inputBT.setEnabled(false);
                   inputBroiki.setVisibility(View.INVISIBLE);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      spinOperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              if(i>0)
              {
                  inputBroiki.setVisibility(View.VISIBLE);
                  inputBT.setEnabled(true);
                  inputBroiki.setText("");
                  id_operation=hashOperation.get(listOperation.get(i));
                 // Toast.makeText(getApplicationContext(),id_operation+"",Toast.LENGTH_LONG).show();
              }
              else
              {
                  inputBT.setEnabled(false);
                  inputBroiki.setVisibility(View.INVISIBLE);
              }

          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });

      inputBT.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
                if(inputBroiki.getText().length()==0)
                {
                    Toast.makeText(WorkerActivity.this,"Моля въведете бройки", Toast.LENGTH_LONG).show();
                    inputBroiki.setError("Моля въведете бройки");
                }
                else
                {
                    new InsertBroiki(user.getId(),id_operation,Integer.parseInt(inputBroiki.getText().toString())).execute();
                }
          }
      });
       final SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yyyy" );
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
                        Toast.makeText(WorkerActivity.this, "Датата трябва да е по малка от "+date2, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new getProductivity(user.getId(),dateFrom.getText().toString(),dateTo.getText().toString()).execute();
                    }


                }
            }
        });
    }

    // izvlichane na produktivnost

    private class getProductivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);
        int id;
        String dateFrom;
        String dateTo;
        double productivity=1;
        int countday=0;

        getProductivity(int id, String dateFrom, String dateTo)
        {
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
                Intent intent = new Intent(WorkerActivity.this, ProductivityWorkerActivity.class);
                intent.putExtra("user",user);
                intent.putExtra("productivity",productivity);
                intent.putExtra("countday",countday);
                intent.putExtra("datefrom",dateFrom);
                intent.putExtra("dateto",dateTo);
                intent.putExtra("role",1);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"Productivity is "+String.format("%.2f",productivity)+" leva",Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"count day is "+countday+" working days",Toast.LENGTH_LONG).show();
            }

        }
    }
    // vyvejdane na produktivnostta za daden potrebitel
    private class InsertBroiki extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);
        int id_user;
        int id_operation;
        int broiki;
        boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Въвеждане...");
            dialog.show();
        }

        InsertBroiki(int id_user, int id_operation,int broiki)
        {
            this.id_user=id_user;
            this.id_operation=id_operation;
            this.broiki=broiki;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/insert_broiki.php/?id_op=%s&id_user=%s&broiki=%s",
                            id_operation,id_user,broiki);

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream));

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
            dialog.hide();
            super.onPostExecute(aVoid);
            if(success){
                Toast.makeText(WorkerActivity.this,
                        "Успешно въведени данни.", Toast.LENGTH_LONG).show();
                inputBroiki.setText("");


            }else{
                Toast.makeText(WorkerActivity.this,
                        "Грешка в записването на базата данни", Toast.LENGTH_LONG).show();
            }
        }


    }

    // izvlichane na operaciite ot bazata danni
    private class GetOperation extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);
        int id;

        GetOperation(int id){
            this.id=id;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("https://android-projects.000webhostapp.com/get_operation.php?group_id=%s",
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
                        listOperation.add(json.getString("name"));
                        hashOperation.put(json.getString("name"),json.getInt("id"));
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
            adapterOperation.notifyDataSetChanged();
        }


    }

    private class GetOperationGroup extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = ("https://android-projects.000webhostapp.com/get_operation_group.php");
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
                        listCategory.add(json.getString("name"));
                        hashGroup.put(json.getString("name"),json.getInt("id"));
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
