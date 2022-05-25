package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Consts {
    public static final int RELATION_FOLLOW = 0;
    public static final int RELATION_BLOCK = 1;
    public static final int RELATION_FOLLOWED = 2;
    public static final int RELATION_SELF = 3;

    public static final int RELATION_NONE = -1;

    public static final int FLAG_SELF = 0;
    public static final int FLAG_OTHER = 1;

    public static final int MESSAGE_LIKE_OR_COMMENT = 0;
    public static final int MESSAGE_FOLLOW = 1;

    public static final SimpleDateFormat date_format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
}
