package com.example.kiennhan.when2leave.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.OnItemClickListener;
import com.example.kiennhan.when2leave.model.activity.R;
import com.example.kiennhan.when2leave.model.activity.ViewEventActivity;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Kien Nhan on 12/8/2017.
 */

public class DailyEventAdapter  extends RecyclerView.Adapter<DailyEventAdapter.DailyEventHolder> {

    private List<Meetings> mMeetings;
    private Context context;
    OnItemClickListener onItemClickListener;
    private int pos;



        public DailyEventAdapter(Context context, List<Meetings> meetings) {
            this.context = context;
            mMeetings = meetings;
        }

        public class DailyEventHolder extends RecyclerView.ViewHolder {

            private Meetings mMeeting;

            private TextView mTitleTextView;
            private TextView mDateTextView;
            private TextView mTimeTextView;

            public LinearLayout eventlayout;

            public DailyEventHolder(View view) {
                super(view);
                eventlayout = (LinearLayout) view.findViewById(R.id.eventItem);
                eventlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(onItemClickListener != null) {
                            onItemClickListener.onItemClick(view, getAdapterPosition());
                        }
                        pos = getAdapterPosition();
                    }
                });

                mTitleTextView = (TextView) itemView.findViewById(R.id.fragment_event_name);
                mDateTextView = (TextView) itemView.findViewById(R.id.fragment_event_date);
                mTimeTextView = (TextView) itemView.findViewById(R.id.fragment_event_time);
            }

            public void bind(Meetings meeting) {
                mMeeting = meeting;
                mTitleTextView.setText(mMeeting.getTitle());
                mDateTextView.setText(mMeeting.getDateOfMeeting());
                mTimeTextView.setText(mMeeting.getTimeOfMeeting());
            }

        }

        @Override
        public DailyEventAdapter.DailyEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_event, parent, false);
            DailyEventHolder vh = new DailyEventHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(DailyEventAdapter.DailyEventHolder holder, int position) {
            Meetings m = mMeetings.get(position);
            holder.bind(m);
        }

        @Override
        public int getItemCount() {
            return mMeetings.size();
        }

        public void setMeetings(List<Meetings> m) {
            mMeetings = m;
        }

    public void setOnItemClickListener(final OnItemClickListener item){
        this.onItemClickListener = item;
    }
}
