package io.localhost.freelancer.statushukum.firebase.entity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class VersionEntity {
    public Long milis;
    public String timestamp;
    public VersionDetailEntity detail;

    public VersionEntity() {
    }

    public VersionEntity(Long milis, String timestamp, VersionDetailEntity detail) {
        this.milis = milis;
        this.timestamp = timestamp;
        this.detail = detail;
    }
}
