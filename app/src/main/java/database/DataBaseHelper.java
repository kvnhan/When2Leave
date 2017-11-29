package database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.MeetingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DbSchema.MeetingTable.Cols.TITLE + ", " +
                DbSchema.MeetingTable.Cols.DATE + ", " +
                DbSchema.MeetingTable.Cols.DESTINATION + ", " +
                DbSchema.MeetingTable.Cols.LOCATION + ", " +
                DbSchema.MeetingTable.Cols.TIME + ", " +
                DbSchema.MeetingTable.Cols.DESCRIPTION +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}