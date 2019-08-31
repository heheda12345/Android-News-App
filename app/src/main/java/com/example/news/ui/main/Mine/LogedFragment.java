package com.example.news.ui.main.Mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;

public class LogedFragment extends Fragment {
    private static String LOG_TAG = LogedFragment.class.getSimpleName();
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
        return view;
    }

}
