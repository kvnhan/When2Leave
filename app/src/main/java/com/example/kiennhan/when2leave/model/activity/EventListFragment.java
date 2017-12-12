package com.example.kiennhan.when2leave.model.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kiennhan.when2leave.model.Meetings;

import java.util.ArrayList;
import java.util.List;

import database.DataBaseHelper;

import static android.content.Context.MODE_PRIVATE;

public class EventListFragment extends Fragment {

    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String NAME = "username";

    private static final String EVENTNAME = "eventname";
    private static final String LOCATION = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private DataBaseHelper mDB;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_recycler_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.e_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }




    private void updateUI() {
        mDB = new DataBaseHelper(getContext());
        SharedPreferences pref = getContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userName = pref.getString(KEY, null);
        String uid = mDB.getUUID(userName, getContext());

        ArrayList<Meetings> meetingsList = mDB.getMeetings(uid, getContext());

        if (mAdapter == null) {
            mAdapter = new EventAdapter(meetingsList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setMeetings(meetingsList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public EventAdapter getmAdapter(ArrayList<Meetings> lom){
        return new EventAdapter(lom);
    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

        private List<Meetings> mMeetings;

        public EventAdapter(List<Meetings> meetings) {
            mMeetings = meetings;
        }

        public class EventHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            private Meetings mMeeting;

            private TextView mTitleTextView;
            private TextView mDateTextView;
            private TextView mTimeTextView;

            public EventHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_event, parent, false));
                itemView.setOnClickListener(this);

                mTitleTextView = (TextView) itemView.findViewById(R.id.fragment_event_name);
                mDateTextView = (TextView) itemView.findViewById(R.id.fragment_event_date);
                mTimeTextView = (TextView) itemView.findViewById(R.id.fragment_event_time);
            }

            public void bind(Meetings meeting) {
                mMeeting = meeting;
                mTitleTextView.setText(mMeeting.getTitle());
                mDateTextView.setText(mMeeting.getDateOfMeeting());
                mTimeTextView.setText(mMeeting.getTimeOfM0eeting());
            }



            @Override
            public void onClick(View view) {
                //TODO: Start intent
                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                int pos = getAdapterPosition();
                Meetings meeting = mMeetings.get(pos);
                String name = meeting.getTitle();
                String location = meeting.getDestination();
                String time = meeting.getTimeOfM0eeting();
                String date = meeting.getDateOfMeeting();
                String desc = meeting.getDescription();
                intent.putExtra(EVENTNAME, name);
                intent.putExtra(LOCATION, location);
                intent.putExtra(TIME, time);
                intent.putExtra(DATE, date);
                intent.putExtra(DESC, desc);
                startActivity(intent);
            }
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
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
    }
}
