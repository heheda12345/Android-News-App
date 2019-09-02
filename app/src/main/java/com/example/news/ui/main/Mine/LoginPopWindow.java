package com.example.news.ui.main.Mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.example.news.R;

public class LoginPopWindow extends PopupWindow {
    private static final String LOG_TAG = PopupWindow.class.getSimpleName();
    private View view;
    private Context context;

    public LoginPopWindow(Context context, View.OnClickListener loginClick, View.OnClickListener registerclick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.login_pop_window, null);
        this.context = context;

        view.findViewById(R.id.loginButton).setOnClickListener(loginClick);
        view.findViewById(R.id.registerButton).setOnClickListener(registerclick);

        initPopWindow();
    }

    private void initPopWindow() {
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        this.setBackgroundDrawable(dw);
        backGroundAlpha((Activity) context,0.5f);
    }

    void backGroundAlpha(Activity context, float alpha) {
        WindowManager.LayoutParams p = context.getWindow().getAttributes();
        p.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(p);
    }

    public String getUsername() {
        return ((EditText)view.findViewById(R.id.userName)).getText().toString();
    }

    public String getPasswd() {
        return ((EditText)view.findViewById(R.id.password)).getText().toString();
    }

}
