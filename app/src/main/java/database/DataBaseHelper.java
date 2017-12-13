package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Meetings;
import com.example.kiennhan.when2leave.model.Password;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "when2leave.db";
    private SQLiteDatabase mDatabase;
    private boolean correctPw = false;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.MeetingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.MeetingTable.Cols.UID + ", " +
                DbSchema.MeetingTable.Cols.ID + ", " +
                DbSchema.MeetingTable.Cols.TITLE + ", " +
                DbSchema.MeetingTable.Cols.DATE_ID + ", " +
                DbSchema.MeetingTable.Cols.DESTINATION_ID + ", " +
                DbSchema.MeetingTable.Cols.LOCATION_ID + ", " +
                DbSchema.MeetingTable.Cols.ISCOMPLETE + ", " +
                DbSchema.MeetingTable.Cols.TIME_ID + ", " +
                DbSchema.MeetingTable.Cols.DESCRIPTION +
                ")"
        );

        db.execSQL("create table " + DbSchema.AddressTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.AddressTable.Cols.STREET_NAME + ", " +
                DbSchema.AddressTable.Cols.ID + ", " +
                DbSchema.AddressTable.Cols.STREET_NUMBER + ", " +
                DbSchema.AddressTable.Cols.STATE + ", " +
                DbSchema.AddressTable.Cols.CITY + ", " +
                DbSchema.AddressTable.Cols.UID + ", " +
                DbSchema.AddressTable.Cols.ZIPCODE +
                ")"
        );

        db.execSQL("create table " + DbSchema.AccountTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.AccountTable.Cols.FIRST_NAME + ", " +
                DbSchema.AccountTable.Cols.LAST_NAME + ", " +
                DbSchema.AccountTable.Cols.EMAIL + ", " +
                DbSchema.AccountTable.Cols.USER_NAME + ", " +
                DbSchema.AccountTable.Cols.PASSWORD + ", " +
                DbSchema.AccountTable.Cols.UID +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /*
     * add account to database
     */
    public void addAccount(Context context, Account account, String hash) {
        mDatabase = new DataBaseHelper(context).getWritableDatabase();

        ContentValues values = getAccountValues(account, hash);
        long i = mDatabase.insert(DbSchema.AccountTable.NAME, null, values);
        System.out.println(i);
        mDatabase.close();

    }

    /*
     * add address to database
     */
    public void addAddress(Context context, Address address, Account account, Boolean isSchedule, Meetings meeting) {
        mDatabase = new DataBaseHelper(context).getWritableDatabase();
        ContentValues values = getAddressValues(address, account, isSchedule, meeting);
        long i = mDatabase.insert(DbSchema.AddressTable.NAME, null, values);
        mDatabase.close();
    }

    /*
     * add meeting to database
     */
    public void addMeeting(Context context, Account account, Meetings meetings) {
        mDatabase = new DataBaseHelper(context).getWritableDatabase();
        ContentValues values = getMeetingValues(meetings, account);
        long i = mDatabase.insert(DbSchema.MeetingTable.NAME, null, values);
        mDatabase.close();
    }

    /*
     * get address
     * //TODO: Make sure to get any addresses
     */
    public Address getAddress(Context context, String id) {
        mDatabase = new DataBaseHelper(context).getReadableDatabase();

        DataCursorWrapper cursor = queryDatabase(DbSchema.AddressTable.NAME,
                DbSchema.AddressTable.Cols.UID + "=?",
                new String[]{id}
        );

        cursor.moveToNext();
        String streetName = cursor.getString(cursor.getColumnIndex(DbSchema.AddressTable.Cols.STREET_NAME));
        String streetNumber = cursor.getString(cursor.getColumnIndex(DbSchema.AddressTable.Cols.STREET_NUMBER));
        String state = cursor.getString(cursor.getColumnIndex(DbSchema.AddressTable.Cols.STATE));
        String city = cursor.getString(cursor.getColumnIndex(DbSchema.AddressTable.Cols.CITY));
        String zipCode = cursor.getString(cursor.getColumnIndex(DbSchema.AddressTable.Cols.ZIPCODE));

        Address address = new Address("", streetNumber, streetName, zipCode, state, city);

        cursor.close();
        mDatabase.close();

        return address;
    }

    /*
    * get account
    */
    public Account getAccount(Context context, String id) {
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.UID + "=?",
                new String[]{id}
        );


        cursor.moveToNext();
        String firstName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.LAST_NAME));
        String userName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.USER_NAME));
        String email = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.EMAIL));
        Account account = new Account(id,firstName,lastName,userName,email, "");

        cursor.close();
        mDatabase.close();

        return account;
    }

    public Account getAccountWithUserName(Context context, String username) {
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.USER_NAME + "=?",
                new String[]{username}
        );

        if(cursor.getCount() == 0){
            mDatabase.close();
            cursor.close();
            cursor = queryDatabase(DbSchema.AccountTable.NAME,
                    DbSchema.AccountTable.Cols.EMAIL + "=?",
                    new String[]{username}
            );
        }
        cursor.moveToNext();
        String firstName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.LAST_NAME));
        String userName = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.USER_NAME));
        String email = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.EMAIL));
        String uid = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.UID));
        Account account = new Account(uid,firstName,lastName,userName,email, "");

        cursor.close();
        mDatabase.close();

        return account;
    }

    /*
    * query database
    */
    private DataCursorWrapper queryDatabase(String tableName, String whereClause, String[] whereArgs) {

        Cursor cursor = mDatabase.query(
                tableName,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new DataCursorWrapper(cursor);
    }



    private static ContentValues getAccountValues(Account account, String hashPassword) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.AccountTable.Cols.FIRST_NAME, account.getFirstName());
        values.put(DbSchema.AccountTable.Cols.LAST_NAME, account.getLastName());
        values.put(DbSchema.AccountTable.Cols.USER_NAME, account.getUserName());
        values.put(DbSchema.AccountTable.Cols.EMAIL, account.getEmail());
        values.put(DbSchema.AccountTable.Cols.PASSWORD, hashPassword);
        values.put(DbSchema.AccountTable.Cols.UID, account.getUid());
        return values;
    }

    private static ContentValues getAddressValues(Address address, Account account, Boolean isSchedule, Meetings meetings) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.AddressTable.Cols.STREET_NAME, address.getStreetName());
        values.put(DbSchema.AddressTable.Cols.STREET_NUMBER, address.getStreetNumber());
        values.put(DbSchema.AddressTable.Cols.STATE, address.getState());
        values.put(DbSchema.AddressTable.Cols.CITY, address.getCity());
        values.put(DbSchema.AddressTable.Cols.ZIPCODE, address.getZipCode());
        values.put(DbSchema.AddressTable.Cols.UID, account.getUid());
        if(isSchedule){
            values.put(DbSchema.AddressTable.Cols.ID, meetings.getId());
        }
        return values;
    }

    private ContentValues getMeetingValues(Meetings meetings, Account account) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.MeetingTable.Cols.ID, meetings.getId());
        values.put(DbSchema.MeetingTable.Cols.DATE_ID, meetings.getDateOfMeeting());
        values.put(DbSchema.MeetingTable.Cols.DESCRIPTION, meetings.getDescription());
        values.put(DbSchema.MeetingTable.Cols.DESTINATION_ID, meetings.getDestination());
        values.put(DbSchema.MeetingTable.Cols.TITLE, meetings.getTitle());
        values.put(DbSchema.MeetingTable.Cols.LOCATION_ID, meetings.getId());
        values.put(DbSchema.MeetingTable.Cols.TIME_ID, meetings.getTimeOfMeeting());
        values.put(DbSchema.MeetingTable.Cols.UID, account.getUid());
        values.put(DbSchema.MeetingTable.Cols.ISCOMPLETE, "false");
        return values;
    }

    public boolean checkAccount(Context context, String username, String password){
        Boolean accountExist = false;
        Boolean usernameExist = checkUsername(context, username, password);
        Boolean emailExist = checkEmail(context, username, password);

        if(usernameExist || emailExist){
            if(correctPw) {
                accountExist = true;
            }
        }

        return accountExist;
    }

    private boolean checkUsername(Context context, String username, String password){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.USER_NAME + "=?",
                new String[]{username}
        );

        if(cursor.getCount() > 0){
            cursor.moveToNext();
            String hashedpw = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.PASSWORD));
            Password pw = new Password();
            correctPw = pw.checkPassword(password,hashedpw);
            mDatabase.close();
            cursor.close();
            return true;
        }

        cursor.close();
        mDatabase.close();
        return false;
    }

    private boolean checkEmail(Context context, String email, String password){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.EMAIL + "=?",
                new String[]{email}
        );

        if(cursor.getCount() > 0){
            cursor.moveToNext();
            String hashedpw = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.PASSWORD));
            Password pw = new Password();
            correctPw = pw.checkPassword(password,hashedpw);
            mDatabase.close();
            cursor.close();
            return true;
        }

        cursor.close();
        mDatabase.close();
        return false;
    }

    public String getUUID(String username, Context context){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor;
        if(username.contains("@")){
            cursor = queryDatabase(DbSchema.AccountTable.NAME,
                    DbSchema.AccountTable.Cols.EMAIL + "=?",
                    new String[]{username}
            );
        }else {
            cursor = queryDatabase(DbSchema.AccountTable.NAME,
                    DbSchema.AccountTable.Cols.USER_NAME + "=?",
                    new String[]{username}
            );
        }
        cursor.moveToNext();
        String uid = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.UID));
        mDatabase.close();
        cursor.close();

        return uid;
    }

    public String getUsername(String username, Context context){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor;
        if(username.contains("@")){
            cursor = queryDatabase(DbSchema.AccountTable.NAME,
                    DbSchema.AccountTable.Cols.EMAIL + "=?",
                    new String[]{username}
            );
        }else{
            return username;
        }
        cursor.moveToNext();
        String user = cursor.getString(cursor.getColumnIndex(DbSchema.AccountTable.Cols.USER_NAME));
        mDatabase.close();
        cursor.close();

        return user;
    }

    public String getMeetingsID(String uid, Context context){
        String eventID = "";
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.MeetingTable.NAME,
                DbSchema.MeetingTable.Cols.UID + "=?",
                new String[]{uid}
        );

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            eventID = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.ID));
        }


        return eventID;
    }

    public ArrayList<Meetings> getMeetings(String uid, Context context){
        ArrayList<Meetings> meetingsList = new ArrayList<Meetings>();
        ArrayList<String> DateAndTimeList = new ArrayList<String>();
        HashMap<String, String> dateAndtimeKey = new HashMap<String, String>();
        HashMap<String, Meetings> meetingKey = new HashMap<String, Meetings>();
        mDatabase = new DataBaseHelper(context).getReadableDatabase();

        DataCursorWrapper cursor = queryDatabase(DbSchema.MeetingTable.NAME,
                DbSchema.MeetingTable.Cols.UID + "=?",
                new String[]{uid}
        );

        System.out.println(cursor.getCount());
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String eventID = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.ID));
                String eventname = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.TITLE));
                String eventLocation = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DESTINATION_ID));
                String eventTime = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.TIME_ID));
                String eventDate = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DATE_ID));
                String description = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DESCRIPTION));
                String isComplete = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.ISCOMPLETE));
                Boolean isDone = false;
                if(isComplete.equals("true")){
                    isDone = true;
                }
                Meetings newMeeting = new Meetings(eventID, eventname, null, eventTime, eventDate, null, eventLocation, description, isDone);
                //meetingsList.add(newMeeting);
                DateAndTimeList.add(eventDate + " @" + eventTime);
                dateAndtimeKey.put(eventDate + " @" + eventTime, eventID);
                meetingKey.put(eventID, newMeeting);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        mDatabase.close();
        Collections.sort(DateAndTimeList, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("MM/dd/yyyy '@'hh:mm");
            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        for(String s: DateAndTimeList){
            Meetings m = meetingKey.get(dateAndtimeKey.get(s));
            meetingsList.add(m);
        }
        return meetingsList;
    }

    public ArrayList<Meetings> getWeeklyMeetings(String uid, Context context){
        ArrayList<Meetings> meetingsList = new ArrayList<Meetings>();
        ArrayList<String> DateAndTimeList = new ArrayList<String>();
        HashMap<String, String> dateAndtimeKey = new HashMap<String, String>();
        HashMap<String, Meetings> meetingKey = new HashMap<String, Meetings>();
        mDatabase = new DataBaseHelper(context).getReadableDatabase();

        DataCursorWrapper cursor = queryDatabase(DbSchema.MeetingTable.NAME,
                DbSchema.MeetingTable.Cols.UID + "=?",
                new String[]{uid}
        );

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        int firstday = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        int lastday = cal.get(Calendar.DAY_OF_MONTH);

        System.out.println(cursor.getCount());
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String eventID = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.ID));
                String eventname = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.TITLE));
                String eventLocation = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DESTINATION_ID));
                String eventTime = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.TIME_ID));
                String eventDate = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DATE_ID));
                String description = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.DESCRIPTION));
                String isComplete = cursor.getString(cursor.getColumnIndex(DbSchema.MeetingTable.Cols.ISCOMPLETE));
                Boolean isDone = false;
                if(isComplete.equals("true")){
                    isDone = true;
                }
                Meetings newMeeting = new Meetings(eventID, eventname, null, eventTime, eventDate, null, eventLocation, description, isDone);
                //meetingsList.add(newMeeting);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                Date startDate=null;
                startDate = df.parse(eventDate);
                Calendar todayDate = Calendar.getInstance();
                todayDate.setTime(startDate);
                if(todayDate.get(Calendar.DAY_OF_MONTH) >= firstday && todayDate.get(Calendar.DAY_OF_MONTH) <= lastday) {
                    DateAndTimeList.add(eventDate + " @" + eventTime);
                    dateAndtimeKey.put(eventDate + " @" + eventTime, eventID);
                    meetingKey.put(eventID, newMeeting);
                }
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        mDatabase.close();
        if(DateAndTimeList.size() > 0) {
            Collections.sort(DateAndTimeList, new Comparator<String>() {
                DateFormat f = new SimpleDateFormat("MM/dd/yyyy '@'hh:mm");

                @Override
                public int compare(String o1, String o2) {
                    try {
                        return f.parse(o1).compareTo(f.parse(o2));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        for(String s: DateAndTimeList){
            Meetings m = meetingKey.get(dateAndtimeKey.get(s));
            meetingsList.add(m);
        }
        return meetingsList;
    }

    public void updateEvent(Context context, Meetings updatedMeeting){
        mDatabase = new DataBaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbSchema.MeetingTable.Cols.TITLE, updatedMeeting.getTitle());
        values.put(DbSchema.MeetingTable.Cols.DATE_ID, updatedMeeting.getDateOfMeeting());
        values.put(DbSchema.MeetingTable.Cols.TIME_ID, updatedMeeting.getTimeOfM0eeting());
        values.put(DbSchema.MeetingTable.Cols.LOCATION_ID, updatedMeeting.getDestination());
        values.put(DbSchema.MeetingTable.Cols.DESCRIPTION, updatedMeeting.getDescription());
        values.put(DbSchema.MeetingTable.Cols.ISCOMPLETE, "false");
        int ret = mDatabase.update(DbSchema.MeetingTable.NAME, values,
                DbSchema.MeetingTable.Cols.ID + "=?",
                new String[]{updatedMeeting.getId()});

        mDatabase.close();
    }

    public void deleteEvent(Context context, Meetings deletedMeeting){
        mDatabase = new DataBaseHelper(context).getWritableDatabase();
        mDatabase.delete(DbSchema.MeetingTable.NAME, DbSchema.MeetingTable.Cols.ID + "=?",
                new String[]{deletedMeeting.getId()});
        mDatabase.close();
    }
}