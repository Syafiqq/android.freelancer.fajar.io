package io.localhost.freelancer.statushukum.controller.filter;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.filter> created by :
 * Name         : syafiq
 * Date / Time  : 17 December 2016, 4:34 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class YearListFilter extends Filter
{
    final RecyclerView.Adapter<YearListAdapter.ViewHolder> adapter;
    final List<MDM_Data.YearMetadata>                      list;

    public YearListFilter(RecyclerView.Adapter<YearListAdapter.ViewHolder> adapter, List<MDM_Data.YearMetadata> list)
    {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence query)
    {
        final List<MDM_Data.YearMetadata> filtered = new LinkedList<>();
        final FilterResults               results  = new FilterResults();
        if(query.length() == 0)
        {
            filtered.addAll(this.list);
        }
        else
        {
            final String filterPattern = query.toString().toLowerCase();
            for(final MDM_Data.YearMetadata list : this.list)
            {
                if(Pattern.matches(".*" + filterPattern + ".*", list.getNo().toLowerCase()))
                {
                    filtered.add(list);
                    continue;
                }
                for(ME_Tag tag : list.getTags())
                {
                    if(Pattern.matches(".*" + filterPattern + ".*", tag.getName().toLowerCase()))
                    {
                        filtered.add(list);
                        break;
                    }
                }
            }
        }
        System.out.println("Count Number " + filtered.size());
        results.values = filtered;
        results.count = filtered.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults)
    {
        if(adapter instanceof YearListAdapter)
        {
            ((YearListAdapter) adapter).update((List<MDM_Data.YearMetadata>) filterResults.values);
            this.adapter.notifyDataSetChanged();
        }
    }
}
