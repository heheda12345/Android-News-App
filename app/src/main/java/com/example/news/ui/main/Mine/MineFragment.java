package com.example.news.ui.main.Mine;


import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.collection.CollectionViewModel;
import com.example.news.data.UserConfig;
import com.example.news.support.NewsItem;
import com.example.news.support.ServerInteraction;
import com.soundcloud.android.crop.Crop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.URI;

import java.io.Serializable;

import static android.app.Activity.RESULT_OK;
import static com.example.news.support.ServerInteraction.ResultCode;
import static com.example.news.support.ServerInteraction.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment implements Serializable{
    private static final int REQUEST_CODE_CHOOSE = 977;
    private static final int LOGIN_ACTIVITY = 5616;
    private static String LOG_TAG = MineFragment.class.getSimpleName();
    public static String LOGIN_LISTENER_ARG = "login";
    public static String REGISTER_LISTENER_ARG = "register";

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
        final View view = inflater.inflate(R.layout.fragment_mine_section, container, false);

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
                Activity activity = (Activity)getView().getContext();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
                activity.finish();
            }
        });

        /* 退出登录按钮 */
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
                    updateLoginVisibility(view);
                }
                Log.d(LOG_TAG, "Logout:" + result.toString());
            }
        });
        updateLoginVisibility(view);

        /*头像&登录按钮*/
        ImageView loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserConfig.getInstance().isLogin()) {
                    Matisse.from(MineFragment.this)
                            .choose(MimeType.ofImage())
                            .maxSelectable(1)
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .theme(R.style.Matisse_Mine)
                            .forResult(REQUEST_CODE_CHOOSE);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MineFragment.this.getContext(), LoginActivity.class);
                    MineFragment.this.startActivityForResult(intent, LOGIN_ACTIVITY);
                }
            }
        });
        return view;
    }

    private void updateLoginVisibility(View view) {
        if (!UserConfig.getInstance().isLogin())
            ((ImageView)view.findViewById(R.id.loginButton)).setImageResource(R.mipmap.login_round);
        view.findViewById(R.id.userName).setVisibility(UserConfig.getInstance().isLogin() ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.logoutButton).setVisibility(UserConfig.getInstance().isLogin() ? View.VISIBLE : View.GONE);
    }

    private void showPersonSelectDialog() {
        final String[] cloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        final String[] cloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
        new AlertDialog.Builder(getView().getContext()).setTitle("选择朗读发音人")
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, String.format("activity result %d %d", requestCode, resultCode));
        ImageView icon = getView().findViewById(R.id.loginButton);
        if (resultCode == LoginActivity.RESULT_LOGIN && requestCode == LOGIN_ACTIVITY) {
            File f1 = ServerInteraction.getInstance().getIcon(UserConfig.getInstance().getUserName(),
                    true, getContext());
            icon.setImageResource(R.mipmap.ic_launcher);
            if (f1 != null)
                Glide.with(getContext()).load(Uri.fromFile(f1)).into(icon);
            TextView name = getView().findViewById(R.id.userName);
            name.setText(UserConfig.getInstance().getUserName());
            updateLoginVisibility(getView());
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            Uri uri = Matisse.obtainResult(data).get(0);
            File f= new File(Matisse.obtainPathResult(data).get(0));
            iconUri = Uri.fromFile(new File(getActivity().getCacheDir(), f.getName()));
            Crop.of(uri, iconUri).asSquare().start(getActivity(), MineFragment.this);
        }
        if (resultCode == RESULT_OK  && requestCode == Crop.REQUEST_CROP) {
            Log.d(LOG_TAG, iconUri.toString());
            try {
                File f = new File(new URI(iconUri.toString()));
                ResultCode result = ServerInteraction.getInstance().uploadIcon(f, UserConfig.getInstance().getUserName());
                if (result == ResultCode.success) {
                    new AlertDialog.Builder(getActivity()).setMessage("上传成功").show();
                    Glide.with(getView().getContext()).load(iconUri).into(icon);
                } else {
                    new AlertDialog.Builder(getActivity()).setMessage("上传失败").show();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "onActivityResult: ", e);
                new AlertDialog.Builder(getActivity()).setMessage("上传失败").show();
            }

        }
    }

    int mTTSPersonSelected = 0;
    Uri iconUri;
}
