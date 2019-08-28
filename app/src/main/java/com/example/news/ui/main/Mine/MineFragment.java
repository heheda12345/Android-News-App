package com.example.news.ui.main.Mine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

import static android.app.Activity.RESULT_OK;
import static com.example.news.support.ServerInteraction.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    private static String LOG_TAG = MineFragment.class.getSimpleName();
    View view;

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
        view = inflater.inflate(R.layout.fragment_mine_section, container, false);
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
                if (!UserConfig.isNetworkAvailable(getContext())) {
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

        view.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable(getContext())) {
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

        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable(getContext())) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                ResultCode result = getInstance().logout();
                if (result == ResultCode.success) {
                    new AlertDialog.Builder(getActivity()).setMessage("退出成功").show();
                    UserConfig.getInstance().setUserName("");
                }
                Log.d(LOG_TAG, "Logout:" + result.toString());
            }
        });

        view.findViewById(R.id.iconButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable(getContext())) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                if (UserConfig.getInstance().getUserName().length() == 0) {
                    new AlertDialog.Builder(getActivity()).setMessage("请先登录").show();
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(false)
                        .setCrop(true)
                        .setCropColors(R.color.colorPrimary, R.color.colorPrimaryDark)
                        .start(getActivity(), MineFragment.this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照功能或者裁剪功能返回
        Log.d(LOG_TAG, String.format("ar %d %d", requestCode, resultCode));
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            ImageView icon = view.findViewById(R.id.iconImageView);
            Log.d(LOG_TAG, data.getStringExtra(PhotoPicker.KEY_CAMEAR_PATH));
            File f = new File(data.getStringExtra(PhotoPicker.KEY_CAMEAR_PATH));
            ResultCode result = ServerInteraction.getInstance().uploadIcon(f, UserConfig.getInstance().getUserName());
            if (result == ResultCode.success) {
                new AlertDialog.Builder(getActivity()).setMessage("上传成功").show();
                File f1 = ServerInteraction.getInstance().getIcon(UserConfig.getInstance().getUserName(),
                            true, getContext());
                if (f1 != null)
                    Glide.with(view.getContext()).load(Uri.fromFile(f1)).into(icon);
            } else {
                new AlertDialog.Builder(getActivity()).setMessage("上传失败").show();
            }
        }
    }

    int mTTSPersonSelected = 0;
    Context mContext;
}
