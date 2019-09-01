package com.example.news.ui.main.Mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.support.ServerInteraction;
import com.example.news.support.ServerInteraction.ResultCode;
import com.soundcloud.android.crop.Crop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.net.URI;

import static android.app.Activity.RESULT_OK;

public class LogedFragment extends Fragment {
    private static String LOG_TAG = LogedFragment.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 977;
    View view;
    Context context;
    ImageView icon;
    Uri iconUri;

    public LogedFragment() {

    }

    public static LogedFragment newInstance() { return new LogedFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loged, container, false);
        context = view.getContext();
        icon = view.findViewById(R.id.iconImageView);
        initIconButton();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            String username = UserConfig.getInstance().getUserName();
            TextView usernameView = view.findViewById(R.id.userName);
            usernameView.setText(username);
            File f1 = ServerInteraction.getInstance().getIcon(username,
                    true, getContext());
            ImageView icon = view.findViewById(R.id.iconImageView);
            if (f1 != null)
                Glide.with(view.getContext()).load(Uri.fromFile(f1)).into(icon);
        }
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

                Matisse.from(LogedFragment.this)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, String.format("activity result %d %d", requestCode, resultCode));
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            Uri uri = Matisse.obtainResult(data).get(0);
            File f= new File(Matisse.obtainPathResult(data).get(0));
            iconUri = Uri.fromFile(new File(getActivity().getCacheDir(), f.getName()));
            Crop.of(uri, iconUri).asSquare().start(getActivity(), LogedFragment.this);
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
}
