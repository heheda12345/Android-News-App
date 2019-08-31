package com.example.news.ui.main.Mine;

import android.app.AlertDialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;
import com.example.news.support.ServerInteraction.ResultCode;

import java.io.File;

import static com.example.news.support.ServerInteraction.getInstance;

public class NotLogFragment extends Fragment {
    private String LOG_TAG = NotLogFragment.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 977;

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
        initLoginButton();
        return view;
    }

    private void initLoginButton() {
        ImageButton loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                final String name = ((EditText)view.findViewById(R.id.userName)).getText().toString();
                final String passwd = ((EditText)view.findViewById(R.id.password)).getText().toString();
                ResultCode result = getInstance().login(name, passwd);
                switch (result) {
                    case success:
                        new AlertDialog.Builder(getActivity()).setMessage("登录成功").show();
                        UserConfig.getInstance().setUserName(name);
                        File f1 = ServerInteraction.getInstance().getIcon(UserConfig.getInstance().getUserName(),
                                true, getContext());
                        ImageView icon = view.findViewById(R.id.iconImageView);
                        if (f1 != null)
                            Glide.with(view.getContext()).load(Uri.fromFile(f1)).into(icon);
                        break;
                    case wrongUserNameorPassWord:
                        new AlertDialog.Builder(getActivity()).setMessage("用户名或密码错误").show();
                        break;
                    case unknownError:
                        new AlertDialog.Builder(getActivity()).setMessage("网络错误，请稍后重试").show();
                        break;
                }
                Log.d(LOG_TAG, "Login:" + result.toString());
            }
        });
    }

    private void initRegisterButton() {
        ImageButton registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                String name = ((EditText)view.findViewById(R.id.userName)).getText().toString().trim();
                String passwd = ((EditText)view.findViewById(R.id.password)).getText().toString().trim();
                if (!name.matches("[0-9a-zA-Z]{2,10}")) {
                    new AlertDialog.Builder(getActivity()).setMessage("用户名应为长度2-10的字母或数字").show();
                    return;
                }
                if (!passwd.matches("[0-9]{2,10}")) {
                    new AlertDialog.Builder(getActivity()).setMessage("密码应为长度2-10的数字").show();
                    return;
                }
                ResultCode result = getInstance().register(name, passwd);
                switch (result) {
                    case success:
                        new AlertDialog.Builder(getActivity()).setMessage("注册成功").show();
                        break;
                    case nameUsed:
                        new AlertDialog.Builder(getActivity()).setMessage("用户名已被占用").show();
                        break;
                    case unknownError:
                        new AlertDialog.Builder(getActivity()).setMessage("网络错误，请稍后重试").show();
                        break;
                }
                Log.d(LOG_TAG, "Register:" + result.toString());
            }
        });
    }

}
