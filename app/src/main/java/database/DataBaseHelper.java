package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Meetings;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "when2leave.db";
    private SQLiteDatabase mDatabase;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.MeetingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.MeetingTable.Cols.UID + ", " +
                DbSchema.MeetingTable.Cols.TITLE + ", " +
                DbSchema.MeetingTable.Cols.DATE + ", " +
                DbSchema.MeetingTable.Cols.DESTINATION + ", " +
                DbSchema.MeetingTable.Cols.LOCATION + ", " +
                DbSchema.MeetingTable.Cols.TIME + ", " +
                DbSchema.MeetingTable.Cols.DESCRIPTION +
                ")"
        );

        db.execSQL("create table " + DbSchema.AddressTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.AddressTable.Cols.STREET_NAME + ", " +
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

    public void addAccount(Context context, Account account, String hash) {
        mDatabase = new DataBaseHelper(context).getWritableDatabase();

        ContentValues values = getAccountValues(account, hash);
        long i = mDatabase.insert(DbSchema.AccountTable.NAME, null, values);
        System.out.println(i);
        mDatabase.close();

    }

    public void addAddress(Context context, Address address, Account account) {
        mDatabase = new DataBaseHelper(context).getWritableDatabase();
        ContentValues values = getAddressValues(address, account);
        long i = mDatabase.insert(DbSchema.AddressTable.NAME, null, values);
        mDatabase.close();
    }

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

        Address address = new Address(streetNumber, streetName, zipCode, state, city);

        cursor.close();
        mDatabase.close();

        return address;
    }

    public Account getAccount(Context context, String id) {
        Address address = getAddress(context,id);
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
        Account account = new Account(id,firstName,lastName,userName,email, "", address, new ArrayList<Meetings>());

        cursor.close();
        mDatabase.close();

        return account;
    }

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

    private static ContentValues getAddressValues(Address address, Account account) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.AddressTable.Cols.STREET_NAME, address.getStreetName());
        values.put(DbSchema.AddressTable.Cols.STREET_NUMBER, address.getStreetNumber());
        values.put(DbSchema.AddressTable.Cols.STATE, address.getState());
        values.put(DbSchema.AddressTable.Cols.CITY, address.getCity());
        values.put(DbSchema.AddressTable.Cols.ZIPCODE, address.getZipCode());
        values.put(DbSchema.AddressTable.Cols.UID, account.getUid());
        return values;
    }

    public boolean checkAccount(Context context, String username, String email){
        Boolean accountExist = false;
        Boolean usernameExist = checkUsername(context, username);
        Boolean emailExist = checkEmail(context, email);

        if(usernameExist || emailExist){
            accountExist = true;
        }

        return accountExist;
    }

    private boolean checkUsername(Context context, String username){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.USER_NAME + "=?",
                new String[]{username}
        );

        if(cursor.getCount() > 0){
            mDatabase.close();
            cursor.close();
            return true;
        }

        cursor.close();
        mDatabase.close();
        return false;
    }

    private boolean checkEmail(Context context, String email){
        mDatabase = new DataBaseHelper(context).getReadableDatabase();
        DataCursorWrapper cursor = queryDatabase(DbSchema.AccountTable.NAME,
                DbSchema.AccountTable.Cols.EMAIL + "=?",
                new String[]{email}
        );

        if(cursor.getCount() > 0){
            mDatabase.close();
            cursor.close();
            return true;
        }

        cursor.close();
        mDatabase.close();
        return false;
    }
}