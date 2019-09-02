package com.example.news.ui.main.Mine;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.data.UserConfig;

import java.io.Serializable;
import static com.example.news.support.ServerInteraction.ResultCode;
import static com.example.news.support.ServerInteraction.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    private static String LOG_TAG = MineFragment.class.getSimpleName();
    public static String LOGIN_LISTENER_ARG = "login";
    public static String REGISTER_LISTENER_ARG = "register";

    View view;
    FragmentManager fragmentManager;
    LogedFragment logedFragment;
    NotLogFragment notLogFragment;

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mine_section, container, false);
        mContext = view.getContext();

        /* login fragment 和not login fragment*/
        fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        /* Loged Fragment */
        logedFragment = LogedFragment.newInstance();

        /* Not log Fragment */
        notLogFragment = NotLogFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGIN_LISTENER_ARG, loginButtonListener);
        bundle.putSerializable(REGISTER_LISTENER_ARG, registerButtonLister);
        notLogFragment.setArguments(bundle);

        /* Put fragments in to manager*/
        ft.add(R.id.login_fragment_container, notLogFragment);
        ft.add(R.id.login_fragment_container, logedFragment);
        ft.hide(logedFragment);
        ft.commit();

        /* 选择语音播报员按钮 */
        view.findViewById(R.id.tts_btn_person_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPersonSelectDialog();
            }
        });

        /* 仅文字版按钮 */
        ((Switch)view.findViewById(R.id.switch_text_only)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserConfig.getInstance().setTextMode(isChecked);
            }
        });

        /* 夜间模式切换 */
        Switch nightModeSwitch = view.findViewById(R.id.night_mode);
        if (UserConfig.getInstance().getNightMode()) {
            nightModeSwitch.setChecked(true);
        }
        else {
            nightModeSwitch.setChecked(false);
        }
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(LOG_TAG, "change mode");
                    UserConfig.getInstance().setNightMode(true);
                }
                else {
                    UserConfig.getInstance().setNightMode(false);
                }
                Activity activity = (Activity)mContext;
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
                activity.finish();
            }
        });

        /* 退出登录按钮 */
        initLogoutButton();

        return view;
    }

    private void initLogoutButton() {
        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                ResultCode result = getInstance().logout();
                if (result == ResultCode.success) {
                    new AlertDialog.Builder(getActivity()).setMessage("退出成功").show();
                    UserConfig.getInstance().setUserName("");
                    changeFragment(logedFragment, notLogFragment);
                }
                Log.d(LOG_TAG, "Logout:" + result.toString());
            }
        });
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

    private void changeFragment(Fragment fragmentToHide, Fragment fragmentToShow) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(fragmentToHide).show(fragmentToShow).commit();
    }

    abstract class LoginListener implements View.OnClickListener, Serializable { };

    private LoginListener loginButtonListener = new LoginListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                final String name = notLogFragment.getUsername();
                final String passwd = notLogFragment.getPasswd();
                ResultCode result = getInstance().login(name, passwd);
                switch (result) {
                    case success:
                        new AlertDialog.Builder(getActivity()).setMessage("登录成功").show();
                        UserConfig.getInstance().setUserName(name);
                        changeFragment(notLogFragment, logedFragment);
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
        };

    private LoginListener registerButtonLister = new LoginListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                String name = notLogFragment.getUsername();
                String passwd = notLogFragment.getPasswd();
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
        };

    int mTTSPersonSelected = 0;
    Context mContext;
}
