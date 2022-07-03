package com.example.y3033906.calendar_113033906.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.y3033906.calendar_113033906.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {
    private TextView tweetView,translateView;
    private Button button;
    private Handler mHandler;
    private Timer mTimer;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd　HH:mm:ss", Locale.US);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tweetView = root.findViewById(R.id.quotation);
        translateView = root.findViewById(R.id.translate);

        mHandler = new Handler(Looper.getMainLooper());
        mTimer = new Timer();

        //1秒毎に定期実行する。
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        String nowDate = dateFormat.format(calendar.getTime());
                        //時刻を表示するTextView
                        ((TextView) root.findViewById(R.id.textView_date)).setText(nowDate);
                    }
                });}
            },0,1000);

        button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //リクエストを送信
                    httpRequest("https://api.tronalddump.io/random/quote");
                } catch (Exception e) {
                    Log.e("Hoge", e.getMessage());
                }
            }
        });
        return root;
    }


    public void httpRequest(String url) throws IOException{
        //OkHttpClinet生成
        OkHttpClient client = new OkHttpClient();
        //request生成
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "*/*")
                .build();
        //非同期リクエスト
        client.newCall(request)
                .enqueue(new Callback() {
                    //エラーのとき
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    //正常のとき
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //response取り出し
                        final String jsonStr = response.body().string();
                        //JSON処理
                        try{
                            //jsonパース
                            JSONObject json = new JSONObject(jsonStr);
                            final String value = json.getString("value");

                            //valueを翻訳
                            translate(value);

                            //親スレッドUI更新
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tweetView.setText(value);
                                }
                            });
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    //変数valueを翻訳する
    private void translate(String value){
        //OkHttpClinet生成
        OkHttpClient client = new OkHttpClient();
        String url ="https://script.google.com/macros/s/AKfycbzuDmbOrcwJBrUtvz8bQ13rRR4BZgYdjPy3850B089425l5AZ3D-yV1LiVarQYrkmp5Zw/exec";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        //set Request parameters
        Map<String, String> params = new HashMap<>();
        params.put("text", value);
        params.put("source", "en");
        params.put("target", "ja");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        //request生成
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Accept","*/*")
                .build();

        //非同期リクエスト
        client.newCall(request)
                .enqueue(new Callback() {
                    //エラーのとき
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    //正常のとき
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //response取り出し
                        final String jsonStr = response.body().string();
                        //JSON処理
                        try{
                            //親スレッドUI更新
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    translateView.setText(jsonStr);
                                }
                            });
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }
}

