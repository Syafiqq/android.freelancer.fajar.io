package io.localhost.freelancer.statushukum.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import io.localhost.freelancer.statushukum.R;
import io.localhost.freelancer.statushukum.controller.Year;
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

    private final List<MDM_Data.CountPerYear> countPerYear;
    private final Context context;
    private final int category;

    public CountPerYearAdapter(final List<MDM_Data.CountPerYear> countPerYear, final Context context, int dataCategory)
    {
        super();


        this.category = dataCategory;
        this.countPerYear = countPerYear;
        this.context = context;
    }

    public void update(final List<MDM_Data.CountPerYear> countPerYear)
    {


        this.countPerYear.clear();
        this.countPerYear.addAll(countPerYear);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_dashboard_recycler_view_item, parent, false);
        final ViewHolder holder = new ViewHolder(itemView);
        itemView.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final MDM_Data.CountPerYear tmpCPY = this.countPerYear.get(position);
        holder.year.setText(String.valueOf(tmpCPY.getYear()));
        holder.count = tmpCPY.getCount();
        holder.status.setImageDrawable(new IconicsDrawable(this.context)
                .icon(MaterialDesignIconic.Icon.gmi_calendar)
                .color(ContextCompat.getColor(this.context, R.color.grey_700))
                .sizeDp(24));
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

        public final TextView year;
        public final ImageView status;
        Integer count;

        public ViewHolder(final View view)
        {
            super(view);

            this.year = (TextView) view.findViewById(R.id.content_dashboard_recycler_view_item_year);
            this.count = 0;
            this.status = (ImageView) view.findViewById(R.id.content_dashboard_recycler_view_item_status);
        }

        @Override
        public void onClick(View view)
        {


            try
            {
                final Context context = CountPerYearAdapter.this.context;
                final int year = NumberFormat.getInstance(Locale.getDefault()).parse(this.year.getText().toString()).intValue();
                final int count = this.count;
                if(count > 0)
                {
                    final Intent intent = new Intent(context, Year.class);
                    intent.putExtra(Year.EXTRA_YEAR, year);
                    intent.putExtra(Year.EXTRA_CATEGORY, CountPerYearAdapter.this.category);
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, String.format(Locale.getDefault(), context.getResources().getString(R.string.content_dashboard_recycler_view_item_no_entry_message), year), Toast.LENGTH_SHORT).show();
                }
            }
            catch(ParseException ignored)
            {

            }
        }
    }
}
