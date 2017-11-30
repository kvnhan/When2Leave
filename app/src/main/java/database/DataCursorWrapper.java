package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;


import com.example.kiennhan.when2leave.model.Account;
import com.example.kiennhan.when2leave.model.Address;
import com.example.kiennhan.when2leave.model.Meetings;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;



public class DataCursorWrapper extends CursorWrapper {

    public DataCursorWrapper(Cursor cursor) {
        super(cursor);
    }


}
