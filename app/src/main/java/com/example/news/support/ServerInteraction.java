package com.example.news.support;

import android.util.Log;

import com.example.news.data.UserConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class PostThread extends Thread {
    private static final String LOG_TAG =
            PostThread.class.getSimpleName();
    private String result;


    abstract String postString();
    abstract String file();

    @Override
    public void run() {
        try {
            URL url = new URL("http", UserConfig.getHostName(), UserConfig.getHostPort(), file());
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setRequestMethod("POST");
//            setProperty(httpUrlConn);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(httpUrlConn.getOutputStream());
            wr.writeBytes(postString());
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConn.getInputStream(), "UTF-8"));
            StringBuilder st = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                st.append(line).append("\n");
            }
            result = st.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    String getResult() {
        return result;
    }
}

class LoginThread extends PostThread{
    private String name, passwd;
    LoginThread(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    @Override
    String postString() {
        return String.format("name=%s&passwd=%s", name, passwd);
    }

    @Override
    String file() {
        return "/login";
    }
}

class RegisterThread extends PostThread {
    private String name, passwd;
    RegisterThread(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    @Override
    String postString() {
        return String.format("name=%s&passwd=%s", name, passwd);
    }

    @Override
    String file() {
        return "/register";
    }
}

public class ServerInteraction {
    private static final String LOG_TAG =
            ServerInteraction.class.getSimpleName();
    private ServerInteraction() {}
    private static ServerInteraction instance = new ServerInteraction();
    public static ServerInteraction getInstance() {
        return instance;
    }

    private String name = "";
    public enum LoginResult {
        success, wrong, error
    }

    public LoginResult login(String name, String passwd) {
        try {
            LoginThread td = new LoginThread(name, passwd);
            td.start();
            td.join();
//            Log.d(LOG_TAG, String.format("Login %s %s: %s", name, passwd, td.getResult()));
            if (td.getResult().startsWith("Success")) {
                Log.d(LOG_TAG, "succ");
                this.name = name;
                return LoginResult.success;
            } else {
                Log.d(LOG_TAG, "wrong");
                return LoginResult.wrong;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return LoginResult.error;
        }
    }

    public enum RegisterResult {
        success, nameUsed, error
    }

    public RegisterResult register(String name, String passwd) {
        try {
            RegisterThread td = new RegisterThread(name, passwd);
            td.start();
            td.join();
            if (td.getResult().startsWith("Success")) {
                this.name = name;
                return RegisterResult.success;
            } else {
                return RegisterResult.nameUsed;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return RegisterResult.error;
        }
    }

    public enum LogoutResult { success};

    public LogoutResult logout() {
        name = "";
        return LogoutResult.success;
    }
}