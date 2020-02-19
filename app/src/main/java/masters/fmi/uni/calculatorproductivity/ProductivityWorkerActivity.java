package masters.fmi.uni.calculatorproductivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProductivityWorkerActivity extends AppCompatActivity {

    TextView usernameTV;
    TextView datefromTV;
    TextView datetoTV;
    TextView averageTV;
    TextView producTV;
    TextView countdays;

    User user;

    String nameLastName=null;

    Button backActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_worker);

        usernameTV=findViewById(R.id.userNameTV);
        datefromTV=findViewById(R.id.dateFromTV);
        datetoTV=findViewById(R.id.dateToTV);
        averageTV=findViewById(R.id.averageProdTV);
        producTV=findViewById(R.id.productTV);
        countdays=findViewById(R.id.countDaysTV);

        backActivity=findViewById(R.id.backBT);

        user = (User) getIntent().getExtras().getSerializable("user");

        double productivnost= getIntent().getDoubleExtra("productivity",0);
        int countday=getIntent().getIntExtra("countday",0);
        final int role=getIntent().getIntExtra("role",0);
        nameLastName=getIntent().getStringExtra("nameLastName");


        if(nameLastName==null)
        {
            usernameTV.setText("Потребител: "+user.getName()+" "+user.getLastname());
        }
        else
        {
            usernameTV.setText("Потребител: "+nameLastName);
        }

        datetoTV.setText("Дата до: "+getIntent().getStringExtra("dateto"));
        datefromTV.setText("Дата от: "+getIntent().getStringExtra("datefrom"));
        producTV.setText("Заработката за периода е "+String.format("%.2f",productivnost)+"лв");
        averageTV.setText("Средна заработка на ден е "+ String.format("%.2f",productivnost/countday)+"лв");
        countdays.setText("Брой изработени дни: "+countday);

        backActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =null;
                if(role==1)
                {
                    intent = new Intent(ProductivityWorkerActivity.this, WorkerActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }
                if(role==2)
                {
                    intent = new Intent(ProductivityWorkerActivity.this, ProductivityAdminActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }
                if(role==3)
                {
                    intent = new Intent(ProductivityWorkerActivity.this, SupervisorActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
