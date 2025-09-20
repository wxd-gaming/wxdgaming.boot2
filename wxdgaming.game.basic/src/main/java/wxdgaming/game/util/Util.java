package wxdgaming.game.util;

import wxdgaming.boot2.core.timer.MyClock;

public abstract class Util {

    public static String formatDate(long time) {
        if (time < System.currentTimeMillis()) {
            return "";
        }
        return MyClock.formatDate("yyyy-MM-dd HH:mm:ss", time);
    }

    /** yyyy-MM-dd'T'HH:mm */
    public static String formatWebDate(long time) {
        return MyClock.formatDate(MyClock.SDF_YYYYMMDDHHMM_10, time);
    }

    /** yyyy-MM-dd'T'HH:mm */
    public static long parseWebDate(String date) {
        return MyClock.parseDate(MyClock.SDF_YYYYMMDDHHMM_10, date).getTime();
    }

}
