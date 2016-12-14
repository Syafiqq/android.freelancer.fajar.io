package io.localhost.freelancer.statushukum.model.util;

import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import me.kaede.tagview.Tag;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.model.util> created by :
 * Name         : syafiq
 * Date / Time  : 14 December 2016, 8:09 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class ME_TagAdapter extends Tag
{
    public String description;

    public ME_TagAdapter(final ME_Tag tag, float textSize)
    {
        super(tag.getName());
        super.layoutColor = tag.getColor();
        super.tagTextColor = tag.getColorText();
        super.tagTextSize = textSize;
        this.description = tag.getDesc();
    }
}
