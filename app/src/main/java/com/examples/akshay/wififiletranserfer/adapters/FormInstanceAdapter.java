package com.examples.akshay.wififiletranserfer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.examples.akshay.wififiletranserfer.R;
import com.examples.akshay.wififiletranserfer.dto.Instance;
import com.examples.akshay.wififiletranserfer.interfaces.InstanceSelectionUpdate;

import java.util.ArrayList;

/**
 * Created by ash on 19/2/18.
 */

public class FormInstanceAdapter extends RecyclerView.Adapter<FormInstanceAdapter.ViewHolder> {

    private static final String TAG = "===FormIA";
    ArrayList<Instance> arrayListFromInstance;
    private Context context;
    private InstanceSelectionUpdate instanceSelectionUpdate;

    public FormInstanceAdapter(ArrayList<Instance> arrayListFromInstance, Context context, InstanceSelectionUpdate instanceSelectionUpdate) {
        Log.d(FormInstanceAdapter.TAG,"FormInstanceAdapter()");
        this.instanceSelectionUpdate = instanceSelectionUpdate;
        this.context = context;
        this.arrayListFromInstance = arrayListFromInstance;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "===ViewHolder";
        public TextView name;
        public CheckBox checkBox;
        public int id;
        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(ViewHolder.TAG,"ViewHolder()");
            name = itemView.findViewById(R.id.card_instance_layout_name);
            checkBox = itemView.findViewById(R.id.card_instance_layout_check_box);
            checkBox.setClickable(false);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            Log.d(ViewHolder.TAG,"ViewHolder()...click" + getAdapterPosition());
            CheckBox checkBox =view.findViewById(R.id.card_instance_layout_check_box);
            if(checkBox.isChecked()) {
               checkBox.setChecked(false);
                instanceSelectionUpdate.unSelect(getAdapterPosition());
            } else {
                checkBox.setChecked(true);
                instanceSelectionUpdate.select(getAdapterPosition());

            }

        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(FormInstanceAdapter.TAG,"onCreateViewHolder()");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_instance_layout,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(FormInstanceAdapter.TAG,"onBindViewHolder()");
        Instance instance = arrayListFromInstance.get(position);
        holder.name.setText(instance.getDisplayName());

    }

    @Override
    public int getItemCount() {
        return arrayListFromInstance.size();
    }

    public ArrayList<Instance> getArrayListBluetoothDevice() {
        return arrayListFromInstance;
    }

    public void setArrayListInstanceAdapter(ArrayList<Instance> arrayListFromInstance) {
        this.arrayListFromInstance = arrayListFromInstance;
    }

}
