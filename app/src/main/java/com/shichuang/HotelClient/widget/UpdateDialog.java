package com.shichuang.HotelClient.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shichuang.HotelClient.R;

/**
 * Created by Administrator on 2018/1/25.
 */

public class UpdateDialog extends BaseDialog {
    private TextView tvMessage;
    private Button btnUpdate;

    public UpdateDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_update, null);
        tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
        btnUpdate = (Button) dialogView.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onUpdateClickListener != null){
                    dismiss();
                    onUpdateClickListener.onClick();
                }
            }
        });
        setContentView(dialogView);
    }

    public void setMessage(String message){
        tvMessage.setText(message);
    }

    private OnUpdateClickListener onUpdateClickListener;

    public interface OnUpdateClickListener{
        void onClick();
    }

    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener){
        this.onUpdateClickListener = onUpdateClickListener;
    }
}
