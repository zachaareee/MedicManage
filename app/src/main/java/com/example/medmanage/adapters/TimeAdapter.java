package com.example.medmanage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medmanage.R;
import java.util.ArrayList;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    private final List<String> timeList;
    private final LayoutInflater inflater;
    private int selectedPosition = -1;
    private final OnItemClickListener listener;
    private List<String> bookedTimes = new ArrayList<>();
    private boolean isEnabled = false;

    public interface OnItemClickListener {
        void onItemClick(String time);
    }

    public TimeAdapter(Context context, List<String> timeList, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.timeList = timeList;
        this.listener = listener;
    }

    public void setBookedTimes(List<String> bookedTimes) {
        this.bookedTimes = bookedTimes;
        notifyDataSetChanged();
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.appnt_time_slot_item, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        String time = timeList.get(position);
        holder.timeSlotTextView.setText(time);

        if (bookedTimes.contains(time)) {
            holder.itemView.setEnabled(false);
            holder.itemView.setSelected(false);
            holder.itemView.setAlpha(0.4f);

        } else if (isEnabled) {
            holder.itemView.setEnabled(true);
            holder.itemView.setSelected(selectedPosition == position);
            holder.itemView.setAlpha(1.0f);

        } else {
            holder.itemView.setEnabled(false);
            holder.itemView.setSelected(false);
            holder.itemView.setAlpha(0.4f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isEnabled && !bookedTimes.contains(time) && listener != null) {
                int previousPosition = selectedPosition;
                if (position == selectedPosition) {
                    selectedPosition = -1;
                    listener.onItemClick(null);
                } else {
                    selectedPosition = holder.getAdapterPosition();
                    listener.onItemClick(time);
                }
                if (previousPosition != -1) notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }


    @Override
    public int getItemCount() { return timeList.size(); }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeSlotTextView;
        TimeViewHolder(View itemView) {
            super(itemView);
            timeSlotTextView = itemView.findViewById(R.id.timeSlotTextView);
        }
    }
}