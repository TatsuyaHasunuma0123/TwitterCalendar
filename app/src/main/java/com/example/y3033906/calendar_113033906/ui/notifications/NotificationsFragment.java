package com.example.y3033906.calendar_113033906.ui.notifications;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.y3033906.calendar_113033906.MainActivity;
import com.example.y3033906.calendar_113033906.MyApplication;
import com.example.y3033906.calendar_113033906.R;
import com.example.y3033906.calendar_113033906.ui.notifications.calendar.CalendarAdapter;
import com.squareup.picasso.Picasso;

import soup.neumorphism.NeumorphImageButton;

public class NotificationsFragment extends Fragment {

    private CalendarAdapter mCalendarAdapter;
    private RelativeLayout beforeLayout;
    private  View root,neumorphView;
    private NeumorphImageButton hashTagButton;
    private String beforeTweet;
    private final int FLAT = 0;
    private final int PRESSED = 1;
    private Integer searchMode;
    private final int SEARCH_BY_USER = 0;
    private final int SEARCH_BY_HASHTAG = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Fragmentを取得
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        neumorphView = root.findViewById(R.id.neumorphTweetCardView);
        neumorphView.setVisibility(View.INVISIBLE);

        //タイトルテキスト(ex.「2021.July」の表示部)を取得
        TextView titleText = root.findViewById(R.id.titleText);

        /*---------------------------------EditTextの処理設定--------------------------------------*/
        EditText editText = root.findViewById(R.id.editText);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                neumorphView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        //https://www.itcowork.co.jp/blog/?p=864
        //キーボードのEnterボタンでキーボードを画面から消す
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    MainActivity.inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                return false;
            }
        });
        /*----------------------------------------------------------------------------------------*/

        /*----------------------------------------ボタンの処理-------------------------------------*/
        //「検索ボタン」
        ImageButton searchButton = root.findViewById(R.id.searchButton);
        FragmentActivity self = getActivity();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //エラー処理
                if(editText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.context , "入力がありません", Toast.LENGTH_LONG).show();
                }

                else {
                    SpannableStringBuilder sb = (SpannableStringBuilder) editText.getText();
                    String str = sb.toString();

                    //「取得中...」のダイアログを表示
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("ツイート取得中...");
                    progressDialog.show();

                    mCalendarAdapter.callTweetModel(str, self, searchMode,progressDialog);

                }

                //キーボードを消す
                MainActivity.inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });

        //「＠」ボタン
        NeumorphImageButton atButton = root.findViewById(R.id.atMarkButton);
        atButton.setShapeType(PRESSED);
        searchMode = SEARCH_BY_USER;
        atButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atButton.setShapeType(PRESSED);
                hashTagButton.setShapeType(FLAT);
                searchMode = SEARCH_BY_USER;
                editText.setHint("search by User(@XXX)...");
            }
        });

        //「＃」ボタン
        hashTagButton = root.findViewById(R.id.hashTagButton);
        hashTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atButton.setShapeType(FLAT);
                hashTagButton.setShapeType(PRESSED);
                searchMode = SEARCH_BY_HASHTAG;
                editText.setHint("search by #...");
            }
        });

        //「＜」ボタン：カレンダーを前月へ
        ImageButton prevButton = root.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                titleText.setText(mCalendarAdapter.getTitle());
            }
        });

        //「＞」ボタン：カレンダーを次月へ
        ImageButton nextButton = root.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                titleText.setText(mCalendarAdapter.getTitle());
            }
        });

        //「×」ボタン
        NeumorphImageButton closeButton = root.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ツイートを表示するビューを消す
                neumorphView.setVisibility(View.INVISIBLE);
            }
        });
        /*----------------------------------------------------------------------------------------*/

        //カレンダーの表示
        GridView calendarGridView = root.findViewById(R.id.calendarGridView);
        mCalendarAdapter = new CalendarAdapter(MyApplication.getAppContext());
        titleText.setText(mCalendarAdapter.getTitle());
        calendarGridView.setAdapter(mCalendarAdapter);

        //ClickListenerをセット
        calendarGridView.setOnItemClickListener(this::onItemClick);
        return root;
    }

    //カレンダーの日付をタッチした時にレイアウトを変更する
    public void onItemClick(AdapterView<?>parent, View view, int position, long id) {
        //タッチしたViewに対してレイアウトを割り当てる
        RelativeLayout layout = (RelativeLayout) view;

        //タッチした日付に対応するツイート，ユーザー，スクリーンネーム，画像のURLを取得
        String tweet = CalendarAdapter.callGetTweetFromView(view);
        String user = CalendarAdapter.callGetUserNameFromView(view);
        String screenName = CalendarAdapter.callGetScreenNameFromView(view);
        String tweetMediaUrl = CalendarAdapter.callGetMediaURLFromView(view);

        //1つ目及び2つ目のレイアウトを透過し、ボタン押下時のレイアウトが見えるようにする
        layout.getChildAt(1).setVisibility(View.GONE);
        layout.getChildAt(2).setVisibility(View.GONE);

        //別のボタンが押下された時、1つ前のレイアウトを透明で無くし、押下時のレイアウトを隠す
        if ((beforeLayout != null) && (beforeLayout != layout)) {
            beforeLayout.getChildAt(1).setVisibility(View.VISIBLE);
            if(beforeTweet == null)
                beforeLayout.getChildAt(2).setVisibility(View.VISIBLE);
        }

        //エラー処理
        if(tweet == null) {
            Toast.makeText(MainActivity.context, "検索されていません", Toast.LENGTH_SHORT).show();
            neumorphView.setVisibility(View.INVISIBLE);
        }

        //それ以外であればツイートを表示
        else {
            neumorphView.setVisibility(View.VISIBLE);
            //透明なまま表示されるため、背景色をセット
            neumorphView.setBackgroundColor(Color.parseColor("#EDEDED"));
            TextView tweetText = root.findViewById(R.id.tweetTextView);
            TextView userText = root.findViewById(R.id.userView);
            ImageView imageView = root.findViewById(R.id.imageView);

            //urlから画像取得nullなら表示を消す
            if(tweetMediaUrl != null)
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(tweetMediaUrl).into(imageView);
            if(tweetMediaUrl == null)
                imageView.setVisibility(View.GONE);

            tweetText.setText(tweet);
            userText.setText(user +"　@" + screenName);
        }

        beforeLayout = (RelativeLayout) view;
        beforeTweet = tweet;
    }
}
