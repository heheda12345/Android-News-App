package com.example.news.ui.main.News;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.data.UserConfig;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class TtsButton extends AppCompatImageButton {
    private static String LOG_TAG = TtsButton.class.getSimpleName();
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private String texts = "";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    private Toast mToast;
    enum State {waiting, running, pause};
    State state = State.waiting;
    String text = "";

    public TtsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTts = SpeechSynthesizer.createSynthesizer(getContext(), mTtsInitListener);
        Log.d(LOG_TAG, "constructor!");
        setState(State.waiting);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( null == mTts ){
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    Log.w(LOG_TAG, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
                    return;
                }
                Log.w(LOG_TAG, "clicked! "+state.name());
                switch (state) {
                    case waiting:
                        try {
                            // 设置参数
                            setParam();
                            int code = mTts.startSpeaking(texts, mTtsListener);
                            if (code != ErrorCode.SUCCESS) {
                                Log.w(LOG_TAG, "语音合成失败,错误码: " + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        break;

                    case running:
                        mTts.pauseSpeaking();
                        break;

                    case pause:
                        mTts.resumeSpeaking();
                        break;

                }
            }
        });
        mToast = Toast.makeText(getContext(),"",Toast.LENGTH_SHORT);
    }

    void setState(State state) {
        Log.d(LOG_TAG, "state "+this.state.name() + "->" + state.name());
        this.state = state;
        switch (state) {
            case waiting:
                setImageResource(R.drawable.ic_listen);
                break;
            case running:
                setImageResource(R.drawable.ic_pause);
                break;
            case pause:
                setImageResource(R.drawable.ic_continue);
                break;
        }
    }

    public void setTexts(String texts) {
        this.texts = texts;
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(LOG_TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.w(LOG_TAG,"初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            setState(state.running);
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            setState(State.pause);
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            setState(State.running);
            showTip("继续播放");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            mPercentForPlaying = percent;
            showTip(String.format(getContext().getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            mPercentForBuffering = percent;
            showTip(String.format(getContext().getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                setState(State.waiting);
                showTip("播放结束");
            } else {
                Log.w(LOG_TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {}
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private void setParam(){
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, UserConfig.getInstance().getTTSVoicer());
    }

    protected void destroy() {
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }
}
