package mobi.cwiklinski.mda.util;

public interface Constant {
    String LOCALITY_ID = "id_miejscowosc";
    String LOCALITY_NAME = "miejscowosc";
    String LOCALITY_PROVINCE = "wojewodztwo";
    String LOCALITY_DISTRICT = "powiat";
    String LOCALITY_COMMUNITY = "gmina";

    String EXTRA_DESTINATION = "destination";
    String EXTRA_LOCALITY = "locality";
    String EXTRA_TIMETABLE_LIST = "timetable_list";

    String URL_MAIN = "http://rozklady.mda.malopolska.pl";

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
