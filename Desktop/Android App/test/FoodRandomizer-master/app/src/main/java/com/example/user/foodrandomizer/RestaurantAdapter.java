package com.example.user.foodrandomizer;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.user.foodrandomizer.MainActivity.fragmentt;
import static com.example.user.foodrandomizer.MainActivity.onConnected2;
import static com.example.user.foodrandomizer.RestaurantFragment.adapter;
import static com.example.user.foodrandomizer.RestaurantFragment.restaurantList;

/**
 * Created by RinonymousRi on 1/11/2018.
 */

public class RestaurantAdapter  extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{


    private Context mContext;
    SharedPreferences pref;
    Set<String> setList;
    private ArrayList<RestaurantModel> mList;
    public RestaurantAdapter(Context context, ArrayList<RestaurantModel> list){
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.restaurant_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RestaurantModel contact = mList.get(position);

        // Set item views based on your views and data model
        TextView item_name = holder.item_name;
        ImageView item_img = holder.item_image;
        LinearLayout ll1 = holder.ll1;

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.data = "Group";
                MainActivity.folderName = contact.getItem_name();
                onConnected2();

                fragmentt.getView().setVisibility(View.GONE);

            }
        });

        item_name.setText(contact.getItem_name());

        pref = mContext.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        setList = new HashSet<String>(pref.getStringSet("folder", new HashSet<String>()));

        item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                setList.remove(contact.getItem_name());
                                editor.putStringSet("folder",setList).apply();

                                List<String> setList2 = new ArrayList<>(setList);

                                Toast.makeText(mContext,"Collection has been removed successfully.",Toast.LENGTH_SHORT).show();

                                restaurantList.clear();

                                for (int i = 0; i < setList2.size(); i++) {
                                    restaurantList.add(new RestaurantModel(setList2.get(i),"", ""));
                                }

                                adapter.notifyDataSetChanged();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:


                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete the collection?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView item_image;
        public TextView item_name,item_place,item_price;
        public LinearLayout ll1;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
//
            item_image = itemView.findViewById(R.id.rv_item_img);
            item_name = itemView.findViewById(R.id.rv_item_name);
            ll1 = itemView.findViewById(R.id.ll1);

        }
    }
}
