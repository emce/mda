package mobi.cwiklinski.mda.model;

import android.text.TextUtils;

public class Detail extends Model {

    private String id;
    private String date;
    private String hash;

    public Detail() {}

    public Detail(String id, String date, String hash) {
        this.id = id;
        this.date = date;
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static Detail parseFromString(String text) {
        if (!TextUtils.isEmpty(text) && text.contains(",")) {
            String[] parts = text.split(",");
            if (parts.length == 3) {
                return new Detail(parts[0], parts[1], parts[2]);
            }
            return null;
        }
        return null;
    }

    public String getDetailUrl() {
        return "http://rozklady.mda.malopolska.pl/ws/getDriveDetail.php?" +
            "kurs_id=" + getId() +
            "&data=" + getDate() +
            "&p=" + getHash();
    }
}
