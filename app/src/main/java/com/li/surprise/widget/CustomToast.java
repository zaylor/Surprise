package com.li.surprise.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.li.surprise.R;

@SuppressLint("ResourceAsColor")
public class CustomToast {

    private Context context;
    private Toast toast;

    private static CustomToast instance;
    public static CustomToast getInstance(Context context){
        if (null == instance) {
            instance = new CustomToast(context);
        }
        return instance;
    }
    public CustomToast(Context context){
        this.context = context;
    }

    /**
     * 显示Toast
     * @param context
     * @param tvString
     */

    public void ToastShow(Context context,String tvString){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        TextView text = (TextView) view.findViewById(R.id.message_textview);
        text.setText(tvString);
        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

}