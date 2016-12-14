package io.localhost.freelancer.statushukum.controller.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.adapter> created by :
 * Name         : syafiq
 * Date / Time  : 13 December 2016, 9:58 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class YearListAdapter extends RecyclerView.Adapter<YearListAdapter.ViewHolder>
{
    public static final String CLASS_NAME = "YearListAdapter";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter";

    private static final int AVAILABLE_ENTRY_COLOR = 192;
    private final List<MDM_Data.YearMetadata> yearList;
    private final Context                     context;

    public YearListAdapter(final List<MDM_Data.YearMetadata> yearList, final Context context)
    {
        super();
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.yearList = yearList;
        this.context = context;
    }

    public void update(final List<MDM_Data.YearMetadata> YearMetadata)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".update");

        this.yearList.clear();
        this.yearList.addAll(YearMetadata);
        super.notifyDataSetChanged();
    }

    @Override
    public YearListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View                       itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_year_recycle_view_item, parent, false);
        final YearListAdapter.ViewHolder holder   = new YearListAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(YearListAdapter.ViewHolder holder, int position)
    {

        final MDM_Data.YearMetadata tmpMetadata = this.yearList.get(position);
        holder.tag.removeAllTags();

        holder.no.setText(tmpMetadata.getNo());
        holder.year.setText(String.valueOf(tmpMetadata.getYear()));
        holder.no.setTextColor(holder.year.getTextColors().withAlpha(AVAILABLE_ENTRY_COLOR));
        holder.year.setTextColor(holder.year.getTextColors().withAlpha(AVAILABLE_ENTRY_COLOR));
        holder.search.setImageDrawable(new IconicsDrawable(this.context)
                .icon(MaterialDesignIconic.Icon.gmi_search)
                .color(ContextCompat.getColor(this.context, R.color.green_a700))
                .sizeDp(24));
        for(final ME_Tag tag : tmpMetadata.getTags())
        {
            holder.tag.addTag(new ME_TagAdapter(tag));
        }
        holder.tag.setOnTagClickListener(null);
        holder.tag.setOnTagClickListener(new OnTagClickListener()
        {
            @Override
            public void onTagClick(int i, Tag tag)
            {
                if(tag instanceof ME_TagAdapter)
                {
                    Toast.makeText(YearListAdapter.this.context, ((ME_TagAdapter) tag).description, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(CLASS_NAME, CLASS_PATH + ".onClick");

                final Context context = YearListAdapter.this.context;
                final int     id      = tmpMetadata.getId();
            }
        };
    }

    @Override
    public int getItemCount()
    {
        return this.yearList.size();
    }

    public static class ME_TagAdapter extends Tag
    {
        public String description;

        public ME_TagAdapter(final ME_Tag tag)
        {
            super(tag.getName());
            super.layoutColor = tag.getColor();
            super.tagTextColor = tag.getColorText();
            super.tagTextSize = 10;
            this.description = tag.getDesc();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public static final String CLASS_NAME = "ViewHolder";
        public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter.ViewHolder";

        public final TextView no, year;
        public final ImageView            search;
        public final TagView              tag;
        public       View.OnClickListener listener;

        public ViewHolder(final View view)
        {
            super(view);

            this.no = (TextView) view.findViewById(R.id.content_year_recycler_view_item_no);
            this.year = (TextView) view.findViewById(R.id.content_year_recycler_view_item_year);
            this.search = (ImageView) view.findViewById(R.id.content_year_recycler_view_item_magnify);
            this.tag = (TagView) view.findViewById(R.id.content_year_recycler_view_item_tag);
        }

        @Override
        public void onClick(View view)
        {
            this.listener.onClick(view);
        }
    }
}
