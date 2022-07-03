package com.example.y3033906.calendar_113033906.ui.notifications.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class DateManager {
    static Calendar mCalendar;
    static Integer dayOfWeek,dayOfMonth;
    public static List<String> intDays;

    public DateManager(){
        mCalendar = Calendar.getInstance();
    }



    //当月の要素を取得
    public List<Date> getDays(){
        //現在の状態を保持
        Date startDate = mCalendar.getTime();

        //GridViewに表示するマスの合計を計算
        int count = getWeeks() * 7 ;
        dayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //当月のカレンダーに表示される前月分の日数を計算
        mCalendar.set(Calendar.DATE, 1);
        dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        mCalendar.add(Calendar.DATE, -dayOfWeek);

        List<Date> days = new ArrayList<>();
        intDays = new ArrayList<>();

        for (int i = 0; i < count; i ++){
            days.add(mCalendar.getTime());
            intDays.add(String.valueOf(mCalendar.get(Calendar.DATE)));
            mCalendar.add(Calendar.DATE, 1);
        }

        //状態を復元
        mCalendar.setTime(startDate);

        return days;
    }

    //当月かどうか確認
    public boolean isCurrentMonth(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        String currentMonth = format.format(mCalendar.getTime());
        if (currentMonth.equals(format.format(date))){
            return true;
        }else {
            return false;
        }
    }

    //週数を取得
    public int getWeeks(){
        return mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    //曜日を取得
    public int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //翌月へ
    public void nextMonth(){
        mCalendar.add(Calendar.MONTH, 1);
    }

    //前月へ
    public void prevMonth(){
        mCalendar.add(Calendar.MONTH, -1);
    }
}

