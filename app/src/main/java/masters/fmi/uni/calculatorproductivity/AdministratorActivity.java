package masters.fmi.uni.calculatorproductivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdministratorActivity extends AppCompatActivity {

    User userFromActivity;
    Button activeUser;
    Button addOperation;
    Button goToUserListBT;
    Button productivityAdminBT;
    Button exitAdminBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        userFromActivity = (User) getIntent().getExtras().getSerializable("user");

        activeUser=findViewById(R.id.activatedUserBT);
        addOperation=findViewById(R.id.addOperationAndCategory);
        goToUserListBT=findViewById(R.id.goToUserListBT);
        productivityAdminBT=findViewById(R.id.productivityAdminBT);
        exitAdminBT=findViewById(R.id.exitAdminBT);

        activeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministratorActivity.this,ActivatedUserActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });

        addOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministratorActivity.this,AddCategoryAndOperationActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });

        goToUserListBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministratorActivity.this,ListUserAdministratorActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });
        productivityAdminBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministratorActivity.this,ProductivityAdminActivity.class);
                intent.putExtra("user",userFromActivity);
                startActivity(intent);
            }
        });
        exitAdminBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministratorActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
