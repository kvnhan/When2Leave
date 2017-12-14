package com.example.kiennhan.when2leave.model.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiennhan.when2leave.model.Meetings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import database.DataBaseHelper;

import static android.content.Context.MODE_PRIVATE;

public class EventListFragment extends Fragment {

    //Keys for Sharepreferences
    private static final String KEY = "isLogin";
    private static final String PREF = "MyPref";
    private static final String NAME = "username";
    private static final String EVENTNAME = "eventname";
    private static final String LOCATION = "location";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DESC = "description";
    private static final String MEETING_ID = "meetingid";

    //Recyclerview and adapter
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private DataBaseHelper mDB;
    ArrayList<Meetings> meetingsList = new ArrayList<Meetings>();

    //Firebase database reference
    private DatabaseReference myRef;
    private static final String ACCOUNT = "account";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_recycler_view, container, false);

        mDB = new DataBaseHelper(getContext());

        //Get User name of current login username
        SharedPreferences pref = getContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userName = pref.getString(KEY, null);
        String uid = "";
        if(!userName.equals("only_guest")){
            uid = mDB.getUUID(userName, getContext());
            myRef = FirebaseDatabase.getInstance().getReference(ACCOUNT + "/" + uid);

        }else{
            uid = "just_guest";
        }

        //Initialize the recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.e_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Allow swipe delete
        swipe();

        //Update UI when there are data changes
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


    /**
     * Update UI when meeting list changes
     */
    private void updateUI() {
        SharedPreferences pref = getContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userName = pref.getString(KEY, null);

        String uid;
        if(!userName.equals("only_guest")){
            uid = mDB.getUUID(userName, getContext());
        }else{
            uid = "just_guest";
        }
        meetingsList = mDB.getMeetings(uid, getContext());

        if (mAdapter == null) {
            mAdapter = new EventAdapter(meetingsList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setMeetings(meetingsList);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Swipe to delete an event from list
     */
    private void swipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are You Sure")
                            .setMessage("Do you really want to delete this event ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Meetings meeting = meetingsList.get(position);
                                    meeting.setComplete(true);
                                    SharedPreferences pref = getContext().getSharedPreferences(PREF, MODE_PRIVATE);
                                    String userName = pref.getString(KEY, null);
                                    if(!userName.equals("only_guest")){
                                        myRef.child(meeting.getId()).setValue(meeting);
                                    }
                                    mDB.deleteEvent(getContext(), meeting);
                                    meetingsList.remove(position);
                                    mRecyclerView.getAdapter().notifyItemRemoved(position);
                                    Toast.makeText(getActivity(), "Deleted " + meeting.getTitle(), Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }}).show();
                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Get adapter
     * @param lom
     * @return
     */
    public EventAdapter getmAdapter(ArrayList<Meetings> lom){
        return new EventAdapter(lom);
    }


    //Adpater Class
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
            private ImageView mViewImage;


            public EventHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_event, parent, false));
                itemView.setOnClickListener(this);

                mTitleTextView = (TextView) itemView.findViewById(R.id.fragment_event_name);
                mDateTextView = (TextView) itemView.findViewById(R.id.fragment_event_date);
                mTimeTextView = (TextView) itemView.findViewById(R.id.fragment_event_time);
                mViewImage = (ImageView) itemView.findViewById(R.id.completeCheck);

            }

            /**
             * Bind Meeting data to a view
             * @param meeting
             */
            public void bind(Meetings meeting) {
                mMeeting = meeting;
                if(mMeeting.getComplete()){
                    mViewImage.setVisibility(View.VISIBLE);
                }
                mTitleTextView.setText(mMeeting.getTitle());
                mDateTextView.setText(mMeeting.getDateOfMeeting());
                mTimeTextView.setText(mMeeting.getTimeOfMeeting());
            }



            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                int pos = getAdapterPosition();
                Meetings meeting = mMeetings.get(pos);
                String name = meeting.getTitle();
                String location = meeting.getDestination();
                String time = meeting.getTimeOfMeeting();
                String date = meeting.getDateOfMeeting();
                String desc = meeting.getDescription();

                //Store data to an intent
                intent.putExtra(EVENTNAME, name);
                intent.putExtra(LOCATION, location);
                intent.putExtra(TIME, time);
                intent.putExtra(DATE, date);
                intent.putExtra(DESC, desc);
                intent.putExtra(MEETING_ID, meeting.getId());

                //Start ViewEventActivity
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

        /**
         * Set mMeeting with a new list
         * @param m
         */
        public void setMeetings(List<Meetings> m) {
            mMeetings = m;
        }
    }

}
