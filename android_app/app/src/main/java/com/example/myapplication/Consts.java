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

    public static final int MESSAGE_LIKE_OR_COMMENT = 1;
    public static final int MESSAGE_FOLLOW = 0;

    public static final SimpleDateFormat date_format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);

    public static final int COMMENT_FLOOR = 0; // 楼层的评论
    public static final int COMMENT_USER = 1; // 某个用户的评论

    public static final int NOTICE_CHECK_PERIOD = 10*60*1000; // 10min

    public static final String channelId = "FORUM";
    public static final String channelName = "FORUM";

    public static final int SORT_BY_TIME = 1;
    public static final int SORT_BY_WAVE = 3;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_AUDIO = 3;

    public static final int SORT_FOLLOW = 0;
    public static final int SORT_ALL = 1;
}
