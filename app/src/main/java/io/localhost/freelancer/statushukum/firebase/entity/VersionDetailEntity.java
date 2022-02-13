package io.localhost.freelancer.statushukum.firebase.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;


@IgnoreExtraProperties
public class VersionDetailEntity {
    public List<String> law_filenames;
    public List<VersionDetailLawOrderEntity> orders;

    public VersionDetailEntity() {
    }

    public VersionDetailEntity(List<String> law_filenames, List<VersionDetailLawOrderEntity> orders) {
        this.law_filenames = law_filenames;
        this.orders = orders;
    }
}
