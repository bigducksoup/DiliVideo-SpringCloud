package com.ducksoup.dilivideocontent.utils;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class TimeUtils {



    public static LocalDate getStartOfCurrentWeek(){
        return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
    }

    public static LocalDate getEndOfCurrentWeek(){
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    }

}
