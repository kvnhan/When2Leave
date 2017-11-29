package database;

public class DbSchema {
    public static final class MeetingTable {
        public static final String NAME = "meetings";

        public static final class Cols {
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DESTINATION = "destination";
            public static final String LOCATION = "location";
            public static final String TIME = "time";
            public static final String DESCRIPTION = "description";
        }
    }
}