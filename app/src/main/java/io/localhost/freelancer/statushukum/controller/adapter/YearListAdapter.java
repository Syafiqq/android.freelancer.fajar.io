package io.localhost.freelancer.statushukum.controller.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.Iterator;
import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.Detail;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.ME_TagAdapter;
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
        final Iterator<ME_Tag>      tags        = tmpMetadata.getTags().iterator();
        holder.tag.removeAllTags();

        holder.no.setText(tmpMetadata.getNo());
        holder.task.setImageDrawable(new IconicsDrawable(this.context)
                .icon(MaterialDesignIconic.Icon.gmi_receipt)
                .color(ContextCompat.getColor(this.context, R.color.grey_700))
                .sizeDp(24));
        if(tmpMetadata.getTagSize() > 0)
        {
            holder.tag.setVisibility(View.VISIBLE);
            for(int i = -1, is = tmpMetadata.getTagSize() > 3 ? 3 : tmpMetadata.getTagSize(); ++i < is; )
            {
                holder.tag.addTag(new ME_TagAdapter(tags.next(), 10f));
            }
            if(tmpMetadata.getTagSize() > 3)
            {
                holder.tag.addTag(new ME_TagAdapter(new ME_Tag(-1, "...", "Dan ke-" + (tmpMetadata.getTagSize() - 3) + " lainnya.", ContextCompat.getColor(this.context, R.color.black), ContextCompat.getColor(this.context, R.color.white)), 10f));
            }

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
        }
        else
        {
            holder.tag.setVisibility(View.GONE);
        }

        holder.listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(CLASS_NAME, CLASS_PATH + ".onClick");

                final Context context = YearListAdapter.this.context;
                final int     id      = tmpMetadata.getId();
                final Intent  intent  = new Intent(context, Detail.class);
                intent.putExtra(Detail.EXTRA_ID, id);
                context.startActivity(intent);
            }
        };
    }

    @Override
    public int getItemCount()
    {
        return this.yearList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public static final String CLASS_NAME = "ViewHolder";
        public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.YearListAdapter.ViewHolder";

        public final TextView  no;
        final        ImageView task;
        final        TagView   tag;
        View.OnClickListener listener;

        ViewHolder(final View view)
        {
            super(view);

            this.no = (TextView) view.findViewById(R.id.content_year_recycler_view_item_no);
            this.task = (ImageView) view.findViewById(R.id.content_year_recycler_view_item_task);
            this.tag = (TagView) view.findViewById(R.id.content_year_recycler_view_item_tag);
        }

        @Override
        public void onClick(View view)
        {
            this.listener.onClick(view);
        }
    }
}
