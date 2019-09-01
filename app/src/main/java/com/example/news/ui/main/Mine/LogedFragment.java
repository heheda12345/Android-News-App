package com.example.news.ui.main.Mine;

import android.app.AlertDialog;
import android.content.Context;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;

public class LogedFragment extends Fragment {
    private static String LOG_TAG = LogedFragment.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 977;
    View view;
    Context context;

    public LogedFragment() {

    }

    public static LogedFragment newInstance() { return new LogedFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loged, container, false);
        context = view.getContext();
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

}
