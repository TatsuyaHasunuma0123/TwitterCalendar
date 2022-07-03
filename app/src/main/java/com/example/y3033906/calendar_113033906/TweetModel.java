package com.example.y3033906.calendar_113033906;

import android.app.ProgressDialog;

import androidx.fragment.app.FragmentActivity;

import com.example.y3033906.calendar_113033906.ui.notifications.calendar.CalendarAdapter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class TweetModel {
    private static User user;
    private static String URL;
    private static Integer mediaCount;
    private static boolean hasMedia;
    public static Tweet[] tweets = new Tweet[1500];


    public static class Tweet{
        //ツイートの内容
        public String tweet;
        //ツイートをした人
        public String user;
        //ツイートをした人のスクリーンネーム
        public String screenName;
        //ツイート画像のURL
        public String mediaURL;
        //ツイートの日時
        public Date date;

        public static Tweet[] tweets = new Tweet[1500];
        Tweet(String tweet, String user,String screenName,String mediaURL,Date date){
            this.tweet = tweet;
            this.user = user;
            this.screenName = screenName;
            this.mediaURL = mediaURL;
            this.date = date;
        }
    }

    public static void getTweetByAttmark(String screenName, CalendarAdapter calendarAdapter, FragmentActivity notificationsFragment, ProgressDialog progressDialog) {
        Arrays.fill(tweets,null);
        hasMedia = false;
        android.os.AsyncTask<Void, Void, String> task
                = new android.os.AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Twitter twitter = TwitterFactory.getSingleton();
                try {
                    //スクリーンネームからIdに変換
                    user = twitter.showUser(screenName);
                    user.getId();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                twitter = new TwitterFactory().getInstance();
                int i = 0;
                try {
                    for(int j = 0; j < 15; j++) {
                        ResponseList<twitter4j.Status> userStatus = twitter.getUserTimeline(user.getId(), new Paging(j+1, 100));
                        for (twitter4j.Status status : userStatus) {
                            for(MediaEntity mediaEntity : status.getMediaEntities()) {
                                URL = mediaEntity.getMediaURLHttps();
                                //画像があるツイート
                                hasMedia = true;
                            }

                            //hasMediaがfalseならURLをnullに更新
                            if(!hasMedia)
                                URL = null;
                            tweets[i] = new Tweet(status.getText(), status.getUser().getName(), status.getUser().getScreenName(), URL,status.getCreatedAt());

                            //hasMediaをfalseに更新
                            hasMedia = false;

                            i++;
                        }

                    }

                    //UI変更のため、メインスレッドでの動作を行う。
                    notificationsFragment.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //「取得中...」ダイアログを消す
                            progressDialog.dismiss();
                            //カレンダーを再描画
                            calendarAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    //ハッシュタグ検索
    public static void getTweetByHashTag (String hashTag, CalendarAdapter calendarAdapter, FragmentActivity notificationsFragment,ProgressDialog progressDialog){
        //tweetsを空にする
        Arrays.fill(tweets,null);
        hasMedia = false;
        //非同期処理
        android.os.AsyncTask<Void, Void, String> task
                = new android.os.AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Twitter twitter = TwitterFactory.getSingleton();

                //検索文字列を作る
                Query query = new Query("#" + hashTag);

                //最大100件取得できる
                query.count(100);
                query.resultType(Query.RECENT);

                int i=0;
                try {

                    //15回ループさせることで1500件取得できる
                    for(int j = 0; j < 15; j++) {
                        QueryResult result = twitter.search(query);
                        for (twitter4j.Status status : result.getTweets()) {
                            for(MediaEntity mediaEntity : status.getMediaEntities()) {
                                //ツイートの画像を取得
                                URL = mediaEntity.getMediaURL();
                                hasMedia = true;
                            }
                            if(!hasMedia)
                                URL = null;
                            tweets[i] = new Tweet(status.getText(), status.getUser().getName(), status.getUser().getScreenName(), URL,status.getCreatedAt());
                            hasMedia = false;
                            i++;
                        }

                        //最大クエリ数取得したら次のページへ
                        if(result.hasNext())
                            query = result.nextQuery();
                        else
                            break;
                    }

                    //UI変更のため、メインスレッドでの動作を行う。
                    notificationsFragment.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //「取得中...」のダイアログを消す
                            progressDialog.dismiss();
                            //カレンダーを再描画
                            calendarAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    //カレンダーの日付に対応したユーザネームを取得
    public static String getuserNameByCalendarDate(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/d", Locale.US);
        for(int i = 0; i < 1500; i++){
            if(tweets[i] == null)
                return null;
            //ツイートの日付と引数strDateが同じになるまで繰り返す
            else if(dateFormat.format(tweets[i].date).equals(strDate))
                return tweets[i].user;
        }
        return  null;
    }

    //カレンダーの日付に対応したスクリーンネームを取得
    public static String getScreenNameByCalendarDate(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/d", Locale.US);
        for(int i = 0; i < 1500; i++){
            if(tweets[i] == null)
                return null;
            //ツイートの日付と引数strDateが同じになるまで繰り返す
            else if(dateFormat.format(tweets[i].date).equals(strDate))
                return tweets[i].screenName;
        }
        return  null;
    }

    //カレンダーの日付に対応したツイートを取得
    public static String getTweetByCalendarDate(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/d", Locale.US);
        for(int i = 0; i < 1500; i++){
            if(tweets[i] == null)
                return null;
            //ツイートの日付と引数strDateが同じになるまで繰り返す
            else if(dateFormat.format(tweets[i].date).equals(strDate))
                return tweets[i].tweet;
        }
        return  null;
    }

    //カレンダーの日付に対応したツイートの画像URLを取得
    public static String getmediaURLByCalendarDate(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/d", Locale.US);
        for(int i = 0; i < 1500; i++){
            if(tweets[i] == null)
                return null;
            //ツイートの日付と引数strDateが同じになるまで繰り返す
            else if(dateFormat.format(tweets[i].date).equals(strDate))
                return tweets[i].mediaURL;
        }
        return  null;
    }
}
