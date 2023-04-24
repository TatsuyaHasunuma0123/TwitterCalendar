package com.example.y3033906.calendar_113033906.ui.notifications.calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.y3033906.calendar_113033906.R;
import com.example.y3033906.calendar_113033906.TweetModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class CalendarAdapter extends BaseAdapter {
    private List<Date> dateArray;
    private final Context mContext;
    private final DateManager mDateManager;
    private final LayoutInflater mLayoutInflater;

    //カスタムセルを拡張したらここでWigetを定義
    public static class ViewHolder {
        public TextView pressedLayerDateText;
        public TextView flatLayerDateText;
        public TextView normalLayerDateText;
        public View pressedNewMorphView;
        public View flatNewMorphView;
    }

    public CalendarAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
                holder = new ViewHolder();
                /*---------------------------3層構造になっている-----------------------------------*/
                //日付が押された時のTextViewのレイアウト
                holder.pressedLayerDateText = convertView.findViewById(R.id.pressedLayerDateText);
                holder.pressedNewMorphView  = convertView.findViewById(R.id.pressedNeumorphView);
                //一般のNeumorphismのTextViewのレイアウト
                holder.flatLayerDateText    = convertView.findViewById(R.id.flatLayerDateText);
                holder.flatNewMorphView     = convertView.findViewById(R.id.flatNeumorphView);
                //平坦なtextView
                holder.normalLayerDateText  = convertView.findViewById(R.id.normalLayerDateText);
                /*--------------------------------------------------------------------------------*/
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

        //nextmonthやprevmonthで再描画処理が行われた際に背景や透明度を基に戻すための記述
        holder.pressedNewMorphView.setBackgroundColor(Color.parseColor("#EDEDED"));
        holder.normalLayerDateText.setBackgroundColor(Color.parseColor("#EDEDED"));
        holder.flatNewMorphView.setBackgroundColor(Color.parseColor("#EDEDED"));
        holder.normalLayerDateText.setVisibility(VISIBLE);

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth() / 7 - (int) dp, parent.getWidth() / 7 - (int) dp - (parent.getWidth() / 7 - (int) dp) / 8);
        convertView.setLayoutParams(params);

        //その月の1日から末日までを表示するためのif
        if((DateManager.dayOfWeek <= position) && (position < DateManager.dayOfWeek + DateManager.dayOfMonth)) {
            //日付のみ表示させる
            SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
            SimpleDateFormat compareFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

            //すべての層のtextViewに日付を表示
            holder.pressedLayerDateText.setText(dateFormat.format(dateArray.get(position)));
            holder.flatLayerDateText.setText(dateFormat.format(dateArray.get(position)));
            holder.normalLayerDateText.setText(dateFormat.format(dateArray.get(position)));


            /*---------ツイートが格納されている日付のflatNewMorphViewを見えるようにする----------------*/
            //tweetsがない場合のエラー処理if
            if (TweetModel.tweets[0] != null) {
                //tweetsクラスの長さの文繰り返す
                for (int i = 0; i < TweetModel.tweets.length; i++) {
                    if(TweetModel.tweets[i] != null) {
                        if (compareFormat.format(TweetModel.tweets[i].date).compareTo(compareFormat.format(dateArray.get(position))) == 0) {
                            //一番上にあるnormalLayerDateTextを透過させ、flatNewMorphViewが見えるようにする。
                            holder.normalLayerDateText.setVisibility(View.GONE);
                            //背景を実際のツイッターの青色に設定
                            holder.flatNewMorphView.setBackgroundColor(Color.parseColor("#00ACEE"));
                            //日付が押された時に見える背景は上の青色よりも少し暗めに設定
                            holder.pressedNewMorphView.setBackgroundColor(Color.parseColor("#267CA7"));
                        }
                    }
                }
            }
            /*------------------------------------------------------------------------------------*/
        }

        //その月の1～末日以外の場合は空欄
        else {
            holder.pressedLayerDateText.setText("");
            holder.flatLayerDateText.setText("");
            holder.normalLayerDateText.setText("");
        }

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 7:
                colorId = Color.RED;
                break;
            case 6:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.normalLayerDateText.setTextColor(colorId);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    //表示月を取得
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MMMM", Locale.US);
        return format.format(DateManager.mCalendar.getTime());
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    public void callTweetModel(String screenName, FragmentActivity notificationsFragment, Integer searchMode, ProgressDialog progressDialog) {
        int SEARCH_BY_USER = 0;
        int SEARCH_BY_HASHTAG = 1;
        if(searchMode == SEARCH_BY_USER)
            TweetModel.getTweetByAttmark(screenName,this,notificationsFragment,progressDialog);
        else if(searchMode == SEARCH_BY_HASHTAG)
            TweetModel.getTweetByHashTag(screenName,this,notificationsFragment,progressDialog);
    }

    /*-----------------------ツイートモデルのメソッドを呼び出す--------------------------------------*/
    public static String callGetUserNameFromView(View view){
        String string_DateFormat = setSimpleDateFormat(view);
        return TweetModel.getuserNameByCalendarDate(string_DateFormat);
    }

    public static String callGetScreenNameFromView(View view){
        String string_DateFormat = setSimpleDateFormat(view);
        return TweetModel.getScreenNameByCalendarDate(string_DateFormat);
    }

    public static String callGetTweetFromView(View view) {
        String string_DateFormat = setSimpleDateFormat(view);
        return TweetModel.getTweetByCalendarDate(string_DateFormat);
    }

    public static String callGetMediaURLFromView(View view){
        String string_DateFormat = setSimpleDateFormat(view);
        return TweetModel.getmediaURLByCalendarDate(string_DateFormat);
    }

    /*-------------------------------------------------------------------------------------------*/

    public static String setSimpleDateFormat(View view){
        View settingView = view.findViewById(R.id.normalLayerDateText);
        TextView txtDate = settingView.findViewById(R.id.normalLayerDateText);
        String strDate = txtDate.getText().toString();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM");

        String string_DateFormat = String.valueOf(sdformat.format(DateManager.mCalendar.getTime()));
        string_DateFormat = string_DateFormat + "/" + strDate;
        return string_DateFormat;
    }
}
