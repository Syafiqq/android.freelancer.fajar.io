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

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.model.database.model.MDM_Data;

/**
 * This <StatusHukum> project in package <io.localhost.freelancer.statushukum.controller.adapter> created by :
 * Name         : syafiq
 * Date / Time  : 13 December 2016, 10:27 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

public class CountPerYearAdapter extends RecyclerView.Adapter<CountPerYearAdapter.ViewHolder>
{
    public static final String CLASS_NAME = "CountPerYearAdapter";
    public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter";

    private static final int AVAILABLE_ENTRY_COLOR   = 192;
    private static final int UNAVAILABLE_ENTRY_COLOR = 56;
    private final List<MDM_Data.CountPerYear> countPerYear;
    private final Context                     context;

    public CountPerYearAdapter(final List<MDM_Data.CountPerYear> countPerYear, final Context context)
    {
        super();
        Log.i(CLASS_NAME, CLASS_PATH + ".Constructor");

        this.countPerYear = countPerYear;
        this.context = context;
    }

    public void update(final List<MDM_Data.CountPerYear> countPerYear)
    {
        Log.i(CLASS_NAME, CLASS_PATH + ".update");

        this.countPerYear.clear();
        this.countPerYear.addAll(countPerYear);
        super.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View             itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_dashboard_recycler_view_item, parent, false);
        final ViewHolder holder   = new ViewHolder(itemView);
        itemView.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final MDM_Data.CountPerYear tmpCPY = this.countPerYear.get(position);
        holder.year.setText(String.valueOf(tmpCPY.getYear()));
        holder.count.setText(String.format(context.getResources().getString(R.string.content_dashboard_recycler_view_item_count), tmpCPY.getCount()));
        if(tmpCPY.getCount() > 0)
        {
            holder.year.setTextColor(holder.year.getTextColors().withAlpha(AVAILABLE_ENTRY_COLOR));
            holder.count.setTextColor(holder.year.getTextColors().withAlpha(AVAILABLE_ENTRY_COLOR));
            holder.status.setImageDrawable(new IconicsDrawable(this.context)
                    .icon(MaterialDesignIconic.Icon.gmi_check_circle)
                    .color(ContextCompat.getColor(this.context, R.color.green_a700))
                    .sizeDp(24));
        }
        else
        {
            holder.year.setTextColor(holder.year.getTextColors().withAlpha(UNAVAILABLE_ENTRY_COLOR));
            holder.count.setTextColor(holder.year.getTextColors().withAlpha(UNAVAILABLE_ENTRY_COLOR));
            holder.status.setImageDrawable(new IconicsDrawable(this.context)
                    .icon(MaterialDesignIconic.Icon.gmi_minus_square)
                    .color(ContextCompat.getColor(this.context, R.color.red_a700))
                    .sizeDp(24));
        }
    }

    @Override
    public int getItemCount()
    {
        return this.countPerYear.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public static final String CLASS_NAME = "ViewHolder";
        public static final String CLASS_PATH = "io.localhost.freelancer.statushukum.controller.adapter.CountPerYearAdapter.ViewHolder";

        public final TextView year, count;
        public final ImageView status;

        public ViewHolder(final View view)
        {
            super(view);

            this.year = (TextView) view.findViewById(R.id.content_dashboard_recycler_view_item_year);
            this.count = (TextView) view.findViewById(R.id.content_dashboard_recycler_view_item_count);
            this.status = (ImageView) view.findViewById(R.id.content_dashboard_recycler_view_item_status);
        }

        @Override
        public void onClick(View view)
        {
            Log.i(CLASS_NAME, CLASS_PATH + ".onClick");

            try
            {
                final int      year       = NumberFormat.getInstance(Locale.getDefault()).parse(this.year.getText().toString()).intValue();
                final MDM_Data model_data = MDM_Data.getInstance(CountPerYearAdapter.this.context);
                final int      count      = model_data.getYearCount(year);
                if(count > 0)
                {
                    Log.i(CLASS_NAME, year + " Pass");
                }
                else
                {
                    Log.i(CLASS_NAME, year + " No Pass");
                }
            }
            catch(ParseException ignored)
            {
                Log.i(CLASS_NAME, ".ParseException");
            }
        }
    }
}
