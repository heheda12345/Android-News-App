package com.example.news.ui.main.Mine;


import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;
import com.soundcloud.android.crop.Crop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.net.URI;


import static android.app.Activity.RESULT_OK;
import static com.example.news.support.ServerInteraction.ResultCode;
import static com.example.news.support.ServerInteraction.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    private static final int REQUEST_CODE_CHOOSE = 977;
    private static String LOG_TAG = MineFragment.class.getSimpleName();
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
        logedFragment = LogedFragment.newInstance();
        notLogFragment = NotLogFragment.newInstance();
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


//        initLoginButton();
//        initRegisterButton();
        initLogoutButton();
        initIconButton();




        icon = view.findViewById(R.id.iconImageView);
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
                }
                Log.d(LOG_TAG, "Logout:" + result.toString());
            }
        });
    }

    private void initIconButton() {
        view.findViewById(R.id.iconButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserConfig.isNetworkAvailable()) {
                    new AlertDialog.Builder(getActivity()).setMessage("无网络").show();
                    return;
                }
                if (UserConfig.getInstance().getUserName().length() == 0) {
                    new AlertDialog.Builder(getActivity()).setMessage("请先登录").show();
                    return;
                }
                Log.d(LOG_TAG, "clicked!");

                Matisse.from(MineFragment.this)
                        .choose(MimeType.ofImage())
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .theme(R.style.Matisse_Mine)
                        .forResult(REQUEST_CODE_CHOOSE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, String.format("activity result %d %d", requestCode, resultCode));
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
                    Glide.with(view.getContext()).load(iconUri).into(icon);
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
    ImageView icon;
    Uri iconUri;
    Context mContext;
}
