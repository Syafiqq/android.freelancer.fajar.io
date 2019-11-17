package io.localhost.freelancer.statushukum.firebase.entity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class VersionEntity {
    private Integer milis;
    private String dateTime;

    public VersionEntity() {
    }

    public VersionEntity(Integer milis, String dateTime) {
        this.milis = milis;
        this.dateTime = dateTime;
    }

    public Integer getMilis() {
        return milis;
    }

    public void setMilis(Integer milis) {
        this.milis = milis;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionEntity that = (VersionEntity) o;

        if (milis != null ? !milis.equals(that.milis) : that.milis != null) return false;
        return dateTime != null ? dateTime.equals(that.dateTime) : that.dateTime == null;
    }

    @Override
    public int hashCode() {
        int result = milis != null ? milis.hashCode() : 0;
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VersionEntity{" +
                "milis=" + milis +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
