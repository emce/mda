package mobi.cwiklinski.mda.model;

import com.google.gson.annotations.SerializedName;

import mobi.cwiklinski.mda.util.Constant;

public class Locality extends Model {

    @SerializedName(Constant.LOCALITY_ID)
    public Integer id;
    @SerializedName(Constant.LOCALITY_NAME)
    public String name;
    @SerializedName(Constant.LOCALITY_PROVINCE)
    public String province;
    @SerializedName(Constant.LOCALITY_DISTRICT)
    public String district;
    @SerializedName(Constant.LOCALITY_COMMUNITY)
    public String community;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String toString() {
        return getName() +
            ", województwo: " + getProvince() +
            ", powiat: " + getDistrict() +
            ", gmina: " + getCommunity();
    }
}
