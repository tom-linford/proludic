package io.github.carlorodriguez.alarmon;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ContentViewHolder> {

    private ArrayList<AlarmInfo> alarmInfos;
    private AlarmClockServiceBinder service;
    private Context context;
    private ToggleButton[] toggles;

    AlarmAdapter(ArrayList<AlarmInfo> alarmInfos,
                 AlarmClockServiceBinder service, Context context) {
        this.alarmInfos = alarmInfos;
        this.service = service;
        this.context = context;
    }

    ArrayList<AlarmInfo> getAlarmInfos() {
        return alarmInfos;
    }

    void removeAt(int position) {
        alarmInfos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alarmInfos.size());
    }

    void removeAll() {
        int size = alarmInfos.size();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                alarmInfos.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder holder, final int position) {
        final AlarmInfo info = alarmInfos.get(position);

        AlarmTime time = null;
        // See if there is an instance of this alarm scheduled.
        if (service.clock() != null) {
            try {
                time = service.clock().pendingAlarm(info.getAlarmId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        // If we couldn't find a pending alarm, display the configured time.
        if (time == null) {
            time = info.getTime();
        }

        String timeStr = time.localizedString(context, true);
        String[] timeSpli = timeStr.split("\\s+");
        holder.timeView.setText(timeSpli[0]);
        if (timeSpli.length > 1) {
            holder.morningOrAfternoonTextView.setText(timeSpli[1]);
        }
        holder.nextView.setText(time.timeUntilString(context));
        holder.labelView.setText(info.getName());

        if (!info.getTime().getDaysOfWeek().equals(Week.NO_REPEATS)) {
            holder.toggleContainer.setVisibility(View.VISIBLE);
            Boolean[] days = info.getTime().getDaysOfWeek().toArray();
            for (int i = 0; i < days.length; i++) {
                    if (days[i]) {
                        toggles[i].setChecked(days[i]);
                    }
            }
        } else {
            holder.toggleContainer.setVisibility(View.GONE);
        }

        holder.enabledView.setChecked(info.enabled());

        holder.enabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final AlarmInfo info = alarmInfos.get(holder.getAdapterPosition());
                if (isChecked) {
                    info.setEnabled(true);
                    service.scheduleAlarm(info.getAlarmId());
                } else {
                    info.setEnabled(false);
                    service.unscheduleAlarm(info.getAlarmId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmInfos.size();
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);
        return new ContentViewHolder(itemView);
    }

    class ContentViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView timeView;
        TextView nextView;
        TextView labelView;
        TextView morningOrAfternoonTextView;
        SwitchCompat enabledView;
        LinearLayout toggleContainer;
        ToggleButton day0, day1, day2, day3, day4, day5, day6;

        ContentViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            timeView = (TextView) view.findViewById(R.id.alarm_time);
            morningOrAfternoonTextView = (TextView) view.findViewById(R.id.alarm_am_or_pm);
            nextView = (TextView) view.findViewById(R.id.next_alarm);
            labelView = (TextView) view.findViewById(R.id.alarm_label);
            enabledView = (SwitchCompat) view.findViewById(R.id.alarm_enabled);
            toggleContainer = (LinearLayout) view.findViewById(R.id.days);
            day0 = (ToggleButton) view.findViewById(R.id.day0);
            day1 = (ToggleButton) view.findViewById(R.id.day1);
            day2 = (ToggleButton) view.findViewById(R.id.day2);
            day3 = (ToggleButton) view.findViewById(R.id.day3);
            day4 = (ToggleButton) view.findViewById(R.id.day4);
            day5 = (ToggleButton) view.findViewById(R.id.day5);
            day6 = (ToggleButton) view.findViewById(R.id.day6);
            toggles = new ToggleButton[]{day0, day1, day2, day3, day4, day5, day6};
        }

        void openAlarmSettings(Context context) {
            final AlarmInfo info = alarmInfos.get(getAdapterPosition());

            final Intent i = new Intent(context, ActivityAlarmSettings.class);

            i.putExtra(ActivityAlarmSettings.EXTRAS_ALARM_ID,
                    info.getAlarmId());

            context.startActivity(i);
        }

        @Override
        public void onClick(View v) {
            openAlarmSettings(v.getContext());
        }
    }

}
