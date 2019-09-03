package com.example.news.ui.main.Mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;

import static com.example.news.support.ServerInteraction.getInstance;

public class LoginActivity extends AppCompatActivity {
    static final String LOG_TAG = LoginActivity.class.getSimpleName();
    static final int RESULT_LOGIN = 2546;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("无网络").show();
                    return;
                }
                final String name = ((EditText)findViewById(R.id.editName)).getText().toString();
                final String passwd = ((EditText)findViewById(R.id.editPasswd)).getText().toString();
                ServerInteraction.ResultCode result = ServerInteraction.getInstance().login(name, passwd);
                switch (result) {
                    case success:
                        UserConfig.getInstance().setUserName(name);
                        setResult(RESULT_LOGIN, new Intent());
                        new AlertDialog.Builder(LoginActivity.this).setMessage("登录成功").
                                setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        LoginActivity.this.finish();
                                    }
                                }).show();
                        break;
                    case wrongUserNameorPassWord:
                        new AlertDialog.Builder(LoginActivity.this).setMessage("用户名或密码错误").show();
                        break;
                    case unknownError:
                        new AlertDialog.Builder(LoginActivity.this).setMessage("网络错误，请稍后重试").show();
                        break;
                }
                Log.d(LOG_TAG, "Login:" + result.toString());
            }
        });

        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("无网络").show();
                    return;
                }
                final String name = ((EditText)findViewById(R.id.editName)).getText().toString();
                final String passwd = ((EditText)findViewById(R.id.editPasswd)).getText().toString();
                if (!name.matches("[0-9a-zA-Z]{2,10}")) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("用户名应为长度2-10的字母或数字").show();
                    return;
                }
                if (!passwd.matches("[0-9]{2,10}")) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("密码应为长度2-10的数字").show();
                    return;
                }
                ServerInteraction.ResultCode result = getInstance().register(name, passwd);
                switch (result) {
                    case success:
                        new AlertDialog.Builder(LoginActivity.this).setMessage("注册成功").show();
                        break;
                    case nameUsed:
                        new AlertDialog.Builder(LoginActivity.this).setMessage("用户名已被占用").show();
                        break;
                    case unknownError:
                        new AlertDialog.Builder(LoginActivity.this).setMessage("网络错误，请稍后重试").show();
                        break;
                }
                Log.d(LOG_TAG, "Register:" + result.toString());
            }
        });
    }
}
