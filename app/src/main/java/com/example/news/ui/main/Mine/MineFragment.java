package com.example.news.ui.main.Mine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;

import static com.example.news.support.ServerInteraction.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    private static String LOG_TAG = MineFragment.class.getSimpleName();

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mine_section, container, false);
        mContext = view.getContext();
        view.findViewById(R.id.tts_btn_person_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPersonSelectDialog();
            }
        });

        ((Switch)view.findViewById(R.id.switch_text_only)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserConfig.getInstance().setTextMode(isChecked);
            }
        });

        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText)view.findViewById(R.id.userName)).getText().toString();
                final String passwd = ((EditText)view.findViewById(R.id.password)).getText().toString();
                LoginResult result = getInstance().login(name, passwd);
                switch (result) {
                    case success:
                         new AlertDialog.Builder(getActivity()).setMessage("登录成功").show();
                        return;
                    case wrong:
                        new AlertDialog.Builder(getActivity()).setMessage("用户名或密码错误").show();
                        return;
                    case error:
                        new AlertDialog.Builder(getActivity()).setMessage("网络错误，请稍后重试").show();
                        return;
                }
                Log.d(LOG_TAG, "Login:" + result.toString());
            }
        });

        view.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                RegisterResult result = getInstance().register(name, passwd);
                switch (result) {
                    case success:
                        new AlertDialog.Builder(getActivity()).setMessage("注册成功").show();
                        return;
                    case nameUsed:
                        new AlertDialog.Builder(getActivity()).setMessage("用户名已被占用").show();
                        return;
                    case error:
                        new AlertDialog.Builder(getActivity()).setMessage("网络错误，请稍后重试").show();
                        return;
                }
                Log.d(LOG_TAG, "Register:" + result.toString());
            }
        });

        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setMessage("退出成功").show();
                LogoutResult result = getInstance().logout();
                Log.d(LOG_TAG, "Logout:" + result.toString());
            }
        });

        return view;
    }

    private void showPersonSelectDialog() {
        final String[] cloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        final String[] cloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
        new AlertDialog.Builder(mContext).setTitle("在线合成发音人选项")
                .setSingleChoiceItems(cloudVoicersEntries, // 单选框有几项,各是什么名字
                        mTTSPersonSelected, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                Log.d(LOG_TAG, "Select " + which);
                                String voicer = cloudVoicersValue[which];
                                UserConfig.getInstance().setTTSVoicer(voicer);
                                mTTSPersonSelected = which;
                                dialog.dismiss();
                            }
                        }).show();
    }
    int mTTSPersonSelected = 0;
    Context mContext;
}
