package database;

public class DbSchema {
    public static final class MeetingTable {
        public static final String NAME = "meetings";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String TITLE = "title";
            public static final String DATE_ID = "date";
            public static final String DESTINATION_ID = "destination";
            public static final String LOCATION_ID = "location";
            public static final String TIME_ID = "time";
            public static final String DESCRIPTION = "description";
        }
    }

    public static final class AddressTable {
        public static final String NAME = "address";

        public static final class Cols {
            public static final String ID = "id";
            public static final String STREET_NAME = "streetname";
            public static final String STREET_NUMBER = "streetnumber";
            public static final String STATE = "state";
            public static final String CITY = "city";
            public static final String ZIPCODE = "zipcode";
            public static final String UID = "uid";


        }
    }

    public static final class AccountTable {
        public static final String NAME = "account";

        public static final class Cols {
            public static final String FIRST_NAME = "firstname";
            public static final String LAST_NAME = "lastname";
            public static final String USER_NAME = "username";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String UID = "uid";


        }
    }
}