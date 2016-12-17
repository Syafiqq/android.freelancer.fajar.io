package io.localhost.freelancer.statushukum.controller.filter;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import java.util.LinkedList;
import java.util.List;

import io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.filter> created by :
 * Name         : syafiq
 * Date / Time  : 17 December 2016, 3:14 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class CountPerYearFilter extends Filter
{
    final RecyclerView.Adapter<CountPerYearAdapter.ViewHolder> adapter;
    final List<MDM_Data.CountPerYear>                          list;

    public CountPerYearFilter(RecyclerView.Adapter<CountPerYearAdapter.ViewHolder> adapter, List<MDM_Data.CountPerYear> list)
    {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence query)
    {
        final List<MDM_Data.CountPerYear> filtered = new LinkedList<>();
        final FilterResults               results  = new FilterResults();
        if(query.length() == 0)
        {
            filtered.addAll(this.list);
        }
        else
        {
            final String filterPattern = query.toString().toLowerCase().trim();
            for(final MDM_Data.CountPerYear list : this.list)
            {
                if(String.valueOf(list.getYear()).startsWith(filterPattern))
                {
                    filtered.add(list);
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
        if(adapter instanceof CountPerYearAdapter)
        {
            ((CountPerYearAdapter) adapter).update((List<MDM_Data.CountPerYear>) filterResults.values);
            this.adapter.notifyDataSetChanged();
        }
    }
}