package io.localhost.freelancer.statushukum.firebase.entity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class VersionDetailLawOrderEntity {
    public String id;
    public String name;
    public int order;

    public VersionDetailLawOrderEntity() {
    }

    public VersionDetailLawOrderEntity(String id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }
}
