package masters.fmi.uni.calculatorproductivity;

import android.app.ProgressDialog;
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
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCategoryAndOperationActivity extends AppCompatActivity {

    User userFromActivity;

    Button insertCaregoryBT;
    EditText insertNameCategoryET;

    EditText nameOperationET;
    EditText priceOperationET;
    Button insertOperationBT;
    Button backBT;

    Spinner CategoryListSpinner;
    ArrayAdapter adapter,adapterSpiner;

    Map<String,Integer> listCategoryHash= new HashMap<>();
    ArrayList<String> listCategory= new ArrayList<>();

    int idCategory;

    Map<String,Integer> listOperationUpdateHash= new HashMap<>();
    ArrayList<String> listOperationUpdate= new ArrayList<>();

    Spinner listOperationForUpdateSpinner;
    Button updatePriceOperationBT;
    int idOperationToUpdate=0;
    EditText insertPriceToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category_and_operation);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        insertCaregoryBT=findViewById(R.id.InsertCategoryBT);
        insertNameCategoryET=findViewById(R.id.InsertCategoryET);

        nameOperationET=findViewById(R.id.insertOperationNameET);
        priceOperationET=findViewById(R.id.insertOperationPrice);
        insertOperationBT=findViewById(R.id.InsertOperationBT);
        backBT=findViewById(R.id.backBTtoAdmin);
        updatePriceOperationBT=findViewById(R.id.updatePriceOperationBT);
        insertPriceToUpdate=findViewById(R.id.insertPriceToUpdate);

        CategoryListSpinner=findViewById(R.id.categoryListSpiner);

        listOperationForUpdateSpinner=findViewById(R.id.listOperationForUpdateSpinner);

        listOperationUpdate.add("Изберете Операция");

        new GetOperation().execute();
        adapterSpiner = new ArrayAdapter(AddCategoryAndOperationActivity.this, android.R.layout.simple_spinner_dropdown_item, listOperationUpdate);
        listOperationForUpdateSpinner.setAdapter(adapterSpiner);

        //-------------- Взимане на ИД-то на операцията, която ще се обнови цената.-----------------------
        listOperationForUpdateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                {
                    idOperationToUpdate=listOperationUpdateHash.get(listOperationUpdate.get(i));
                }
                else
                {
                    idOperationToUpdate=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //-------------------------------------------------------------------------------------------------

        // ----------- Обновяване на цената на операцията в базата данни --------------------------------

        updatePriceOperationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idOperationToUpdate>0 && insertPriceToUpdate.getText().length()>0 )
                {
                    new updatePriceOperation(idOperationToUpdate,Double.parseDouble(insertPriceToUpdate.getText().toString())).execute();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Моля изберете операция и въведете новата цена",Toast.LENGTH_LONG).show();
                }
            }
        });
        //-------------------------------------------------------------------------------------------------

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCategoryAndOperationActivity.this,AdministratorActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });

        insertCaregoryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(insertNameCategoryET.getText().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Моля въведете име на категория",Toast.LENGTH_LONG).show();
                    insertNameCategoryET.setError("Моля въведете име на категория");
                }
                else
                {
                    new insertCategory(insertNameCategoryET.getText().toString()).execute();
                }
            }
        });
        listCategory.add("Изберете категория");
        new getCategory().execute();
        adapter = new ArrayAdapter(AddCategoryAndOperationActivity.this, android.R.layout.simple_spinner_dropdown_item, listCategory);
        CategoryListSpinner.setAdapter(adapter);

        CategoryListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i>0)
                {
                    idCategory=listCategoryHash.get(listCategory.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        insertOperationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nameOperationET.getText().length()==0 || priceOperationET.getText().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"И двете полета са задължителни",Toast.LENGTH_LONG).show();
                }
                else
                {

                        new insertOperation(nameOperationET.getText().toString(),
                                Double.parseDouble(priceOperationET.getText().toString()),idCategory).execute();
                }
            }
        });
    }

    // Обновяване на цента на операцията
    private class updatePriceOperation extends AsyncTask<Void, Void, Void>
    {

        int id;
        Double price;

        ProgressDialog dialog;
        boolean success = false;

        updatePriceOperation(int id, double price){
            this.id = id;
            this.price=price;
            dialog = new ProgressDialog(AddCategoryAndOperationActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Обновяване на операцията...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.format(
                    "http://android-projects.000webhostapp.com/admin/update_operation_price.php?id=%s&price=%s",
                    id,price);

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
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Обновяването на цената е успешно.", Toast.LENGTH_LONG).show();
                CategoryListSpinner.setSelection(0);
                insertPriceToUpdate.setText("");


            }else{
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Грешка в обновяването на цената", Toast.LENGTH_LONG).show();
            }
        }
    }




    //Листване на всички операции за обновяване на цената.

    private class GetOperation extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(AddCategoryAndOperationActivity.this);

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("http://android-projects.000webhostapp.com/admin/get_operation_to_update_price.php");

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
                        listOperationUpdate.add("ID: "+json.getInt("id")+" "+json.getString("name"));
                        listOperationUpdateHash.put("ID: "+json.getInt("id")+" "+json.getString("name"),json.getInt("id"));
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
            adapterSpiner.notifyDataSetChanged();
        }
    }


    //Добавяне на операция в базата данни
    private class insertOperation extends AsyncTask<Void, Void, Void>
    {

        String name;
        Double price;
        int id_group;
        ProgressDialog dialog;
        boolean success = false;

        insertOperation(String name, double price, int id_group){
            this.name = name;
            this.price=price;
            this.id_group=id_group;
            dialog = new ProgressDialog(AddCategoryAndOperationActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Добавяне в базата...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.format(
                    "http://android-projects.000webhostapp.com/admin/insert_operation.php?name=%s&price=%s&id_group=%s"
                    , name,price,id_group);

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
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Добавянето е успешно.", Toast.LENGTH_LONG).show();
                nameOperationET.setText("");
                priceOperationET.setText("");

            }else{
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Грешка в записването на базата данни", Toast.LENGTH_LONG).show();
            }
        }
    }

    // извличане от базата данни наличните категории от операции
    private class getCategory extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog= new ProgressDialog(AddCategoryAndOperationActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Зареждане на информацията...");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.
                    format("https://android-projects.000webhostapp.com/get_operation_group.php");
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
                        listCategoryHash.put(json.getString("name"),json.getInt("id"));
                        listCategory.add(json.getString("name"));
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

        }

    }

    //Добавяне на базата данни категория от операция
    private class insertCategory extends AsyncTask<Void, Void, Void>
    {

        String name;
        ProgressDialog dialog;
        boolean success = false;

        insertCategory(String name){
            this.name = name;
            dialog = new ProgressDialog(AddCategoryAndOperationActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Добавяне в базата...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = String.format(
                    "http://android-projects.000webhostapp.com/admin/insert_group.php?name=%s"
                    , name);

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
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Добавянето е успешно.", Toast.LENGTH_LONG).show();
                insertNameCategoryET.setError(null);
                insertNameCategoryET.setText("");

            }else{
                Toast.makeText(AddCategoryAndOperationActivity.this,
                        "Грешка в записването на базата данни", Toast.LENGTH_LONG).show();
            }
        }
    }
}
