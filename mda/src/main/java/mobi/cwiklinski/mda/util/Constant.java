package mobi.cwiklinski.mda.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constant {
    String LOCALITY_ID = "id_miejscowosc";
    String LOCALITY_NAME = "miejscowosc";
    String LOCALITY_PROVINCE = "wojewodztwo";
    String LOCALITY_DISTRICT = "powiat";
    String LOCALITY_COMMUNITY = "gmina";

    String EXTRA_LOCALITY = "locality";
    String EXTRA_TIMETABLE_LIST = "timetable_list";
    String EXTRA_DETAIL = "detail";
    String EXTRA_STAGE_LIST = "stage_list";
    String EXTRA_CARRIER = "carrier";
    String EXTRA_LOADED = "loaded";

    String URL_MAIN = "http://rozklady.mda.malopolska.pl";
    DateFormat FULL_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    DateFormat TIMEDATE_FORMAT = new SimpleDateFormat("HH:mm, yyyy-MM-dd", Locale.getDefault());
    DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
    DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DateFormat FULL_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    enum Destination {
        FROM_CRACOW,
        TO_CRACOW,
        FROM_NOWY_SACZ,
        TO_NOWY_SACZ;

        public int getId() {
            return ordinal();
        }
    }
}
