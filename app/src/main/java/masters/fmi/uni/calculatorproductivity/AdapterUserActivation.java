package masters.fmi.uni.calculatorproductivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterUserActivation   extends BaseAdapter {

    private Context context;
    private ArrayList<User> userList;

    public AdapterUserActivation(Context context, ArrayList<User> userList){
        this.context=context;
        this.userList=userList;

    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view==null)
        {
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.user_to_activation,null);
        }

        TextView user_id=view.findViewById(R.id.id_UserActivation);
        TextView user_name=view.findViewById(R.id.nameUserActivation);
        TextView user_lastname=view.findViewById(R.id.lastNameUserActivation);

        user_id.setText(userList.get(i).getId()+"");
        user_name.setText(userList.get(i).getName());
        user_lastname.setText(userList.get(i).getLastname());
        return view;
    }
}
