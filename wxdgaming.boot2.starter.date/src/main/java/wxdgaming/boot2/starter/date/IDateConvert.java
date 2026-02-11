package wxdgaming.boot2.starter.date;

/**
 * 时间格式转换
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-11 19:13
 **/
public interface IDateConvert {

    String type();

    long convert(String date);

    long convertEndTime(long startTime, String date);

}
