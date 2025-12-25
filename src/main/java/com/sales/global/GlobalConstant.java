package com.sales.global;

import com.sales.entities.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalConstant {
    public static String secretKey = "asdfghj123456789";
    public static int suId =0;
    public  static  int groupId = 0;
    public static String wholesalerImagePath = "/wholesale/auth/profile/";
    public static String PATH_SEPARATOR = "/";
    public static String staffImagePath = "/admin/auth/profile/";
    public static String CHAT_STATIC_PATH = "/chat/images/";

    // for normal images
    public static int minWidth = 500;
    public static int minHeight = 500;
    public static int maxWidth = 10024;
    public static int maxHeight = 10024;

    // for banner images
    public static int bannerMinWidth = 500;
    public static int bannerMinHeight = 500;
    public static int bannerMaxWidth = 10000;
    public static int bannerMaxHeight = 10000;

    public static double[] allowedAspectRatios = {1.0, 1.33, 1.78};
    public static String[] allowedFormats = {"jpg", "jpeg", "png", "gif",};
    public static String removeBgUrl = "http://localhost:5000/remove-background";

    public static final Map<String, User> onlineUsers = new ConcurrentHashMap<>();

    public static final String ITEMS_NOT_UPDATED_PATH_FOR_ADMIN = "/admin/item/notUpdated/";
    public static final String ITEMS_NOT_UPDATED_PATH_FOR_WHOLESALE = "/wholesale/item/notUpdated/";
    public static final String AUTH_TOKEN_PREFIX = GlobalConstant.AUTH_TOKEN_PREFIX;
    public static final String AUTHORIZATION = "Authorization";



    private GlobalConstant () {
    }

}
