package com.example.mainpage.API.Model;

public class AccessTime {
    int hour;
    int minute;
    int second;

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public AccessTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public class WeeklyAccessTime extends AccessTime {
        private int dayOfWeek;

        public WeeklyAccessTime(int hour, int minute, int second, int dayOfWeek) {
            super(hour, minute, second); // Call parent class constructor
            this.dayOfWeek = dayOfWeek;
        }

        public int getDayOfWeek() {
            return dayOfWeek;
        }
    }
}