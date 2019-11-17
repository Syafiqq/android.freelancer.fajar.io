package io.localhost.freelancer.statushukum.firebase.entity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class VersionEntity {
    public Long milis;
    public String timestamp;

    public VersionEntity() {
    }

    public VersionEntity(Long milis, String timestamp) {
        this.milis = milis;
        this.timestamp = timestamp;
    }
}
