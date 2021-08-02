package com.example.meettingflat.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.meettingflat.R;


public class LockPasswordDialog extends Dialog {

    private TextView mTitleTv;
    private OnConfirmClickListener mClickListener;
    private boolean mSingleModel = true;
    private GridPasswordView mPasswordView;
    private TextView mConfirmTv;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;
    private TextView zero;
    private TextView clear;

    public LockPasswordDialog(@NonNull Context context) {
        super(context);
        initView(context);
        initListener();
    }


    private void initView(Context context) {
        View inflate = View.inflate(context, R.layout.device_popup_lock_password_input, null);
        setContentView(inflate);
        mTitleTv = inflate. findViewById(R.id.title_tv);
        mPasswordView = inflate. findViewById(R.id.password_view);
        mConfirmTv = inflate.findViewById(R.id.confirm_tv);
        one = inflate.findViewById(R.id.one);
        two = inflate.findViewById(R.id.two);
        three = inflate.findViewById(R.id.three);
        four = inflate.findViewById(R.id.four);
        five = inflate.findViewById(R.id.five);
        six = inflate.findViewById(R.id.six);
        seven = inflate.findViewById(R.id.seven);
        eight = inflate.findViewById(R.id.eight);
        nine = inflate.findViewById(R.id.nine);
        zero = inflate.findViewById(R.id.zero);
        clear = inflate.findViewById(R.id.clear);
    }

    public void initListener() {
        findViewById(R.id.back).setOnClickListener(v -> {
            dismiss();
        });
        mPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                mConfirmTv.setEnabled(false);
            }

            @Override
            public void onInputFinish(String psw) {
                mConfirmTv.setEnabled(true);
            }
        });

        mConfirmTv.setOnClickListener(v -> {
            dismiss();
            if (mClickListener != null)
                mClickListener.onClick(mPasswordView.getPassWord());
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"1");
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"2");
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"3");
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"4");
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"5");
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"6");
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"7");
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"8");
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"9");
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mPasswordView.getInput().getText().toString();
                mPasswordView.getInput().setText(s+"0");
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView.delete();
            }
        });

    }

    public interface OnConfirmClickListener {

        /**
         * 密码结果
         *
         * @param password
         */
        void onClick(String password);
    }

    public void setClickListener(OnConfirmClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void show() {
        super.show();
        mPasswordView.deleteAll();
    }
}
