package io.localhost.freelancer.statushukum.controller.filter;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import io.localhost.freelancer.statushukum.controller.adapter.SearchAdapter;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.filter> created by :
 * Name         : syafiq
 * Date / Time  : 31 December 2016, 2:21 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class SearchFilter extends Filter
{
    final RecyclerView.Adapter<SearchAdapter.ViewHolder> adapter;
    final List<MDM_Data.MetadataSearchable>              list;

    public SearchFilter(RecyclerView.Adapter<SearchAdapter.ViewHolder> adapter, List<MDM_Data.MetadataSearchable> list)
    {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence query)
    {
        final List<MDM_Data.MetadataSearchable> filtered = new LinkedList<>();
        final FilterResults                     results  = new FilterResults();
        if(query.length() == 0)
        {
            filtered.addAll(this.list);
        }
        else
        {
            final String filterPattern = query.toString().toLowerCase();
            for(final MDM_Data.MetadataSearchable list : this.list)
            {
                if(Pattern.matches(".*" + filterPattern + ".*", list.getDescription().toLowerCase()))
                {
                    filtered.add(list);
                }
            }
        }
        results.values = filtered;
        results.count = filtered.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults)
    {
        if(adapter instanceof SearchAdapter)
        {
            ((SearchAdapter) adapter).update((List<MDM_Data.MetadataSearchable>) filterResults.values);
            this.adapter.notifyDataSetChanged();
        }
    }
}
