package masters.fmi.uni.calculatorproductivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<CustomUserForSupervisor> userList;

    public Adapter(Context context, ArrayList<CustomUserForSupervisor> userList){
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
            view=layoutInflater.inflate(R.layout.user,null);
        }

        TextView user_id=view.findViewById(R.id.id_userTV);
        TextView user_name=view.findViewById(R.id.nameUserTV);
        TextView quantity=view.findViewById(R.id.quantityUserTV);

        user_id.setText(userList.get(i).getId()+"");
        user_name.setText(userList.get(i).getName());
        quantity.setText(userList.get(i).getQuantity()+"");
        return view;
    }
}
