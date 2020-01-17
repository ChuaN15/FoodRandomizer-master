package com.example.user.foodrandomizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.foodrandomizer.MainActivity.folderOptionsData;
import static com.example.user.foodrandomizer.MainActivity.optionsAdapter;
import static com.example.user.foodrandomizer.MainActivity.recyclerView2;


public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{
    private FolderOptionsData[] listdata;
    SharedPreferences pref;
    Set<String> setList;
    Activity activity;

    public void itemClick(ImageView iv,String foldername)
    {
        if(iv.getVisibility() == View.VISIBLE)
        {
            iv.setVisibility(View.INVISIBLE);
            setList.remove(MainActivity.selectedData + "|" + foldername);
        }
        else
        {
            iv.setVisibility(View.VISIBLE);
            setList.add(MainActivity.selectedData + "|" + foldername);
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("group",setList).apply();

    }

    // RecyclerView recyclerView;
    public OptionsAdapter(FolderOptionsData[] listdata,Activity activity) {
        this.listdata = listdata;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.options_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FolderOptionsData myListData = listdata[position];
        holder.textView.setText(listdata[position].getDescription());

        if(listdata[position].getImgId() != 0)
        {
            holder.imageView.setImageResource(listdata[position].getImgId());

            holder.ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Collection Name:");

// Set up the input
                        final EditText input = new EditText(activity);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

// Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String enteredData = input.getText().toString();

                                pref = activity.getSharedPreferences("MyPref", 0); // 0 - for private mode


                                SharedPreferences.Editor editor = pref.edit();

                                setList = new HashSet<String>(pref.getStringSet("folder", new HashSet<String>()));
                                List<String> setList2 = new ArrayList<>(setList);

                                if(setList2.contains(enteredData))
                                {
                                    Toast.makeText(activity,"Collection with same name already existed, try with another name.",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    setList.add(enteredData);

                                    editor.putStringSet("folder",setList).apply();

                                    setList2 = new ArrayList<>(setList);

                                    folderOptionsData = new FolderOptionsData[setList2.size()+1];
                                    folderOptionsData[0] = new FolderOptionsData("New Collection", android.R.drawable.ic_input_add);

                                    for (int i = 0; i < setList2.size(); i++) {
                                        folderOptionsData[i+1] = new FolderOptionsData(setList2.get(i), 0);
                                    }

                                    optionsAdapter = new OptionsAdapter(folderOptionsData,activity);
                                    recyclerView2.setHasFixedSize(true);
                                    recyclerView2.setLayoutManager(new LinearLayoutManager(activity));
                                    recyclerView2.setAdapter(optionsAdapter);

                                    Toast.makeText(activity,"Collection has been added successfully.",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
            });
        }
        else
        {
            pref = activity.getSharedPreferences("MyPref", 0); // 0 - for private mode
            setList = new HashSet<String>(pref.getStringSet("group", new HashSet<String>()));
            List<String> setList2 = new ArrayList<>(setList);

            if(setList2.contains(MainActivity.selectedData + "|" + listdata[position].getDescription()))
            {
                holder.imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.imageView.setVisibility(View.INVISIBLE);
            }

            holder.ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        itemClick(holder.imageView,listdata[position].getDescription());

                }
            });
        }




    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout ll1;
        public LinearLayout ll2;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            ll1 = (LinearLayout)itemView.findViewById(R.id.ll1);
            ll2 = (LinearLayout)itemView.findViewById(R.id.ll2);
        }
    }
}