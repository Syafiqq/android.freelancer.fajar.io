package io.localhost.freelancer.statushukum.model.entity;


import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.entity> created by :
 * Name         : syafiq
 * Date / Time  : 12 December 2016, 9:00 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class ME_Data
{
    public static final String CLASS_NAME = "ME_Data";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.model.entity.ME_Data";

    private final int    id;
    private final int    year;
    private final String no;
    private final String description;
    private final String status;
    private final int category;
    private final String reference;

    public ME_Data(int id, int year, String no, String description, String status, int category, String reference)
    {


        this.id = id;
        this.year = year;
        this.no = no;
        this.description = description;
        this.status = status;
        this.category = category;
        this.reference = reference;
    }

    public int getId()
    {
        return this.id;
    }

    public int getYear()
    {
        return this.year;
    }

    public String getNo()
    {
        return this.no;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getStatus()
    {
        return this.status;
    }

    public int getCategory()
    {
        return this.category;
    }

    public String getReference()
    {
        return this.reference;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("year", year)
                .append("no", no)
                .append("description", description)
                .append("status", status)
                .append("category", category)
                .append("reference", reference)
                .toString();
    }
}
