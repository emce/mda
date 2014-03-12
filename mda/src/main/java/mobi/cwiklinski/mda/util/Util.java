package mobi.cwiklinski.mda.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Util {

    public static final DateFormat FULL_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
}
