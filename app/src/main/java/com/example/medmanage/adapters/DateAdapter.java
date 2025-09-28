package com.example.medmanage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medmanage.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private final List<Date> dateList;
    private final LayoutInflater inflater;
    private int selectedPosition = -1;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.US);

    public interface OnItemClickListener {
        void onItemClick(Date date);
    }

    public DateAdapter(Context context, List<Date> dateList, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Date date = dateList.get(position);
        holder.dayOfWeekTextView.setText(dayFormat.format(date).toUpperCase());
        holder.dayOfMonthTextView.setText(dateFormat.format(date));
        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            if (position == selectedPosition) {
                selectedPosition = -1; // Deselect if clicked again
                listener.onItemClick(null);
            } else {
                selectedPosition = holder.getAdapterPosition();
                listener.onItemClick(date);
            }
            if (previousPosition != -1) notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() { return dateList.size(); }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekTextView, dayOfMonthTextView;
        DateViewHolder(View itemView) {
            super(itemView);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeekTextView);
            dayOfMonthTextView = itemView.findViewById(R.id.dayOfMonthTextView);
        }
    }
}