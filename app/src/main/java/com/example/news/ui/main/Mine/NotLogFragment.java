package com.example.news.ui.main.Mine;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.news.R;

public class NotLogFragment extends Fragment {
    private String LOG_TAG = NotLogFragment.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 977;
    private View.OnClickListener loginClick;
    private View.OnClickListener registerClick;
    private LoginPopWindow popWindow;

    View view;
    Context context;

    public NotLogFragment() {

    }

    public static NotLogFragment newInstance() { return new NotLogFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_not_log, container, false);
        context = view.getContext();
        Bundle bundle = getArguments();
        loginClick = (View.OnClickListener) bundle.getSerializable(MineFragment.LOGIN_LISTENER_ARG);
        registerClick = (View.OnClickListener) bundle.getSerializable(MineFragment.REGISTER_LISTENER_ARG);

        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow = new LoginPopWindow(getActivity(), loginClick, registerClick);
                popWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popWindow.backGroundAlpha(getActivity(), 1f);
                    }
                });
            }
        });
        return view;
    }

    public String getUsername() {
        return popWindow.getUsername();
    }

    public String getPasswd() {
        return popWindow.getPasswd();
    }



}
