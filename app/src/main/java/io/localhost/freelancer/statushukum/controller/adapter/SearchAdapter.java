package io.localhost.freelancer.statushukum.controller.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.apache.commons.lang3.StringUtils;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Iterator;
import java.util.List;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.Detail;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;
import io.localhost.freelancer.statushukum.model.entity.ME_Tag;
import io.localhost.freelancer.statushukum.model.util.HtmlUtil;
import io.localhost.freelancer.statushukum.model.util.ME_TagAdapter;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.adapter> created by :
 * Name         : syafiq
 * Date / Time  : 31 December 2016, 1:04 PM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable
{
    public static final String CLASS_NAME = "SearchAdapter";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.SearchAdapter";

    private final List<MDM_Data.MetadataSearchable> yearList;
    private final Context context;
    private final int category;
    private Filter filter;


    public SearchAdapter(final List<MDM_Data.MetadataSearchable> yearList, final Context context, int dataCategory)
    {
        super();

        this.yearList = yearList;
        this.context = context;
        this.category = dataCategory;
    }

    public void update(final List<MDM_Data.MetadataSearchable> MetadataSearchable)
    {

        this.yearList.clear();
        this.yearList.addAll(MetadataSearchable);
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_search_recycle_view_item, parent, false);
        final SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position)
    {

        final MDM_Data.MetadataSearchable tmpMetadata = this.yearList.get(position);
        final Iterator<ME_Tag> tags = tmpMetadata.getTags().iterator();
        holder.tag.removeAllTags();

        holder.no.setText(tmpMetadata.getNo());
        holder.description.setHtml(HtmlUtil.sanitizeHtml(StringUtils.abbreviate(tmpMetadata.getDescription(), 100)));
        holder.task.setImageDrawable(new IconicsDrawable(this.context)
                .icon(MaterialDesignIconic.Icon.gmi_receipt)
                .color(ContextCompat.getColor(this.context, R.color.grey_700))
                .sizeDp(24));
        for(int i = -1, is = tmpMetadata.getTagSize() > 3 ? 3 : tmpMetadata.getTagSize(); ++i < is; )
        {
            holder.tag.addTag(new ME_TagAdapter(tags.next(), 10f));
        }
        if(tmpMetadata.getTagSize() > 3)
        {
            holder.tag.addTag(new ME_TagAdapter(new ME_Tag(-1, "...", "Dan ke-" + (tmpMetadata.getTagSize() - 3) + " lainnya.", ContextCompat.getColor(this.context, R.color.black), ContextCompat.getColor(this.context, R.color.white)), 10f));
        }

        holder.tag.setOnTagClickListener(null);
        holder.tag.setOnTagClickListener(new OnTagClickListener()
        {
            @Override
            public void onTagClick(int i, Tag tag)
            {
                if(tag instanceof ME_TagAdapter)
                {
                    Toast.makeText(SearchAdapter.this.context, ((ME_TagAdapter) tag).description, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                final Context context = SearchAdapter.this.context;
                final int id = tmpMetadata.getId();
                final Intent intent = new Intent(context, Detail.class);
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

    @Override
    public Filter getFilter()
    {
        return this.filter;
    }

    public void setFilter(Filter filter)
    {
        this.filter = filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public static final String CLASS_NAME = "ViewHolder";
        public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.SearchAdapter.ViewHolder";

        public final TextView no;
        final ImageView task;
        final TagView tag;
        final HtmlTextView description;
        View.OnClickListener listener;

        ViewHolder(final View view)
        {
            super(view);

            this.no = (TextView) view.findViewById(R.id.content_search_recycler_view_item_no);
            this.description = (HtmlTextView) view.findViewById(R.id.content_search_recycler_view_item_description);
            this.description.setEllipsize(TextUtils.TruncateAt.END);
            this.task = (ImageView) view.findViewById(R.id.content_search_recycler_view_item_task);
            this.tag = (TagView) view.findViewById(R.id.content_search_recycler_view_item_tag);
        }

        @Override
        public void onClick(View view)
        {
            this.listener.onClick(view);
        }
    }
}
