package com.mlr.utils;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;

import com.mlr.mrecyclerview.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatUtils {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================

    // ==========================================================================
    // Constructors
    // ==========================================================================

    // ==========================================================================
    // Getters
    // ==========================================================================

    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    // 正则表达式
    private static final String cellPhoneRegex = "^1[3|4|5|8][0-9]\\d{8}$";

    public static String BLANK_STRING = "";
    public final static String DEFAULT_FILE_SUFFIX_DIVIDER = ".";
    public static String POPBANNER_STRING_DIVIDER = "___###";
    public static String BOTTOM_STRING_DIVIDER = " ";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");

    /**
     * transform millisecond to the special form for output
     *
     * @param millis
     * @return
     */
    public static String convertMillisToStr(long millis) {

        String sd = sdf.format(new Date(millis));
        return sd;
    }

    /**
     * 格式化文件大小
     *
     * @param len 文件长度，单位为字节
     * @return
     */
    public static String formatFileSize(long len) {
        // String size;
        //
        // if (len < 10 * 1024) {
        // // [0, 10KB)，保留两位小数
        // size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
        // } else if (len < 100 * 1024) {
        // // [10KB, 100KB)，保留一位小数
        // size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
        // } else if (len < 1024 * 1024) {
        // // [100KB, 1MB)，个位四舍五入
        // size = String.valueOf(len / 1024) + "KB";
        // } else if (len < 10 * 1024 * 1024) {
        // // [1MB, 10MB)，保留两位小数
        // size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100) + "MB";
        // } else if (len < 100 * 1024 * 1024) {
        // // [10MB, 100MB)，保留一位小数
        // size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10) + "MB";
        // } else if (len < 1024 * 1024 * 1024) {
        // // [100MB, 1GB)，个位四舍五入
        // size = String.valueOf(len / 1024 / 1024) + "MB";
        // } else {
        // // [1GB, ...)，保留两位小数
        // size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100) + "GB";
        // }
        // return size;
        return formatFileSize(len, false, true);
    }

    public static String formatFileSize(long len, boolean keepZero) {
        return formatFileSize(len, keepZero, true);
    }

    public static String formatFileSize(long len, boolean keepZero, boolean keepDecimal) {
        String size;

        DecimalFormat formatKeepTwoZero = new DecimalFormat(keepDecimal ? "#.00" : "#");
        DecimalFormat formatKeepOneZero = new DecimalFormat(keepDecimal ? "#.0" : "#");
        if (len == 0) {
            size = String.valueOf(len + "M");
        } else if (len < 1024) {
            size = String.valueOf(len + "B");
        } else if (len < 10 * 1024) {
            // [0, 10KB)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / (float) 100) + "K";
        } else if (len < 100 * 1024) {
            // [10KB, 100KB)，保留一位小数
            size = String.valueOf(len * 10 / 1024 / (float) 10) + "K";
        } else if (len < 1024 * 1024) {
            // [100KB, 1MB)，个位四舍五入
            size = String.valueOf(len / 1024) + "K";
        } else if (len < 10 * 1024 * 1024) {
            // [1MB, 10MB)，保留两位小数
            if (keepZero) {
                size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / (float) 100)) + "M";
            } else {
                size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100) + "M";
            }
        } else if (len < 100 * 1024 * 1024) {
            // [10MB, 100MB)，保留一位小数
            if (keepZero) {
                size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024 / 1024 / (float) 10)) + "M";
            } else {
                size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10) + "M";
            }
        } else if (len < 1024 * 1024 * 1024) {
            // [100MB, 1GB)，个位四舍五入
            size = String.valueOf(len / 1024 / 1024) + "M";
        } else {
            // [1GB, ...)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100) + "G";
        }
        return size;
    }

    public static String simpleFormatSize(long len, boolean keepDecimal) {
        String size;

        DecimalFormat formatKeepTwoZero = new DecimalFormat(keepDecimal ? "#.00" : "#");
        DecimalFormat formatKeepOneZero = new DecimalFormat(keepDecimal ? "#.0" : "#");
        if (len == 0) {
            size = String.valueOf(len + "M");
        } else if (len < 1024) {
            size = String.valueOf(len + "B");
        } else if (len < 10 * 1024) {
            // [0, 10KB)，保留两位小数
            size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / (float) 100)) + "K";
        } else if (len < 100 * 1024) {
            // [10KB, 100KB)，保留一位小数
            size = String.valueOf(formatKeepTwoZero.format(len * 10 / 1024 / (float) 10)) + "K";
        } else if (len < 1024 * 1024) {
            // [100KB, 1MB)，个位四舍五入
            size = String.valueOf(len / 1024) + "K";
        } else if (len < 10 * 1024 * 1024) {
            // [1MB, 10MB)，保留两位小数
            size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / (float) 100)) + "M";
        } else if (len < 100 * 1024 * 1024) {
            // [10MB, 100MB)，保留一位小数
            size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024 / 1024 / (float) 10)) + "M";
        } else if (len < 1024 * 1024 * 1024) {
            // [100MB, 1GB)，个位四舍五入
            size = String.valueOf(len / 1024 / 1024) + "M";
        } else {
            // [1GB, ...)，保留两位小数
            size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / 1024 / (float) 100)) + "G";
        }
        return size;
    }

    public static boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile("\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 把手机号码中间5位换成*
     */
    public static String encodePhoneNumber(String originalNumber) {
        if (originalNumber == null) {
            return originalNumber;
        }

        StringBuilder encodedNum = new StringBuilder(originalNumber);

        if (originalNumber.length() > 8) {
            for (int i = 3; i < 8; i++) {
                encodedNum.setCharAt(i, '*');
            }
        }

        return encodedNum.toString();
    }

    /**
     * 把邮箱@前面4个符号换成*
     */
    public static String encodeEmail(String originalEmail) {
        if (originalEmail == null) {
            return originalEmail;
        }
        StringBuilder encodedEmail = new StringBuilder(originalEmail);
        int index = originalEmail.indexOf("@") - 1;
        if (index >= 3) {
            for (int i = index; i > index - 4; i--) {
                encodedEmail.setCharAt(i, '*');
            }
        } else if (index >= 0) {
            for (int i = 0; i < index + 1; i++) {
                encodedEmail.setCharAt(i, '*');

            }
        }
        return encodedEmail.toString();
    }

    public static boolean isNull(String str) {
        return (str == null || BLANK_STRING.equals(str));
    }

    /**
     * 将string转换成非空字符串
     *
     * @param str
     * @return 如果str为空，则返回空串，否则直接返回原串
     */
    public static String getNoneNullString(String str) {
        return str == null ? BLANK_STRING : str;
    }

    /**
     * 将字符串分解成字符串数组，以splitter为分隔符
     *
     * @param str      输入字符串
     * @param splitter 分隔符
     * @return 字符串数组，如果输入为空，返回空
     */
    public static String[] StringToArray(String str, String splitter) {
        // 检查合法性
        if (isNull(str)) {
            // 原始字符串为空，直接返回null
            return null;
        } else if (isNull(splitter)) {
            // 分解字符串为空，返回原字符串的数组
            return new String[]{str};
        } else {
            // 返回分解结果
            return str.split(splitter);
        }
    }

    /**
     * 将字符串数组拼接成一个字符串
     *
     * @param strList  输入字符串数组
     * @param splitter 分隔符
     * @param start    截取开始位置
     * @param end      截取结束位置（不包括end）
     * @return 字符串，如果输入为空，返回空
     */

    public static String ArrayToString(String[] strList, String splitter, int start, int end) {
        // 检查数据合法性
        if (strList == null || strList.length == 0 || start < 0 || end > strList.length || start >= end) {
            return BLANK_STRING;
        }

        // 拼接字符串数组和分隔符
        StringBuilder sb = new StringBuilder();
        for (int index = start; index < end; index++) {
            sb.append(strList[index]);
            sb.append(splitter);
        }

        return sb.toString();
    }

    public static String getCellPhoneNum(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getLine1Number();
    }

    /**
     * <li>上次刷新时间距当前时间1分钟内，显示XX秒前</li> <li>上次刷新时间时间距当前时间1小时内显示XX分钟前</li> <li>上次刷新时间时间距当前时间1天内，显示XX小时前</li> <li>
     * 上次刷新时间时间距当前时间1周内，显示XX天前</li> <li>上次刷新时间时间距当前时间1月内，显示XX周前</li> <li>上次刷新时间时间距当前时间1年内，显示XX月前</li> <li>
     * 上次刷新时间时间距当前时间超过1年（含），显示XX年前</li>
     *
     * @param l 上次刷新的时间
     * @return
     */
    public static String formatPullToRefreshTime(Context context, long l) {

        Resources res = context.getResources();

        long nowTime = System.currentTimeMillis();
        long intervalTime = nowTime - l;
        intervalTime = Math.max(1000, intervalTime);// 处理用户设置时间后会出现负值的问题
        String updateText;
        if (intervalTime < 60 * 1000) {// 1分钟内
            updateText = res.getString(R.string.update_refresh_date_within_minute, intervalTime / 1000);
        } else if (intervalTime < 60 * 60 * 1000) {// 1小时内
            updateText = res.getString(R.string.update_refresh_date_within_hour, intervalTime / 1000 / 60);
        } else if (intervalTime < 24 * 60 * 60 * 1000) {// 1天内
            updateText = res.getString(R.string.update_refresh_date_within_day, intervalTime / 1000 / 60 / 60);
        } else if (intervalTime < 7 * 24 * 60 * 60 * 1000) {// 1周内
            updateText = res.getString(R.string.update_refresh_date_within_week, intervalTime / 1000 / 60 / 60 / 24);
        } else if (intervalTime < 30 * 24 * 60 * 60 * 1000) {// 1月内
            updateText = res.getString(R.string.update_refresh_date_within_month, intervalTime / 1000 / 60 / 60 / 24
                    / 7);
        } else if (intervalTime < 365 * 24 * 60 * 60 * 1000) {// 1年内
            updateText = res.getString(R.string.update_refresh_date_within_year, intervalTime / 1000 / 60 / 60 / 24
                    / 30);
        } else {
            updateText = res
                    .getString(R.string.update_refresh_date_more_year, intervalTime / 1000 / 60 / 60 / 24 / 365);
        }
        return updateText;

    }

    /*
     * 我的浏览，我的点赞，浏览时间通用处理
     */
    public static String formatMyBrowseTime(Context context, long l) {
        Resources res = context.getResources();

        long nowTime = System.currentTimeMillis();
        long intervalTime = nowTime - l;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String browseDate = dateFormat.format(new Date(l));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String browseTime = timeFormat.format(new Date(l));
        String updateText;
        if (intervalTime < 60 * 1000) {// 显示几秒前
            updateText = res.getString(R.string.update_refresh_date_within_minute, intervalTime / 1000);
        } else if (intervalTime < 30 * 60 * 1000) {// 显示半小时前
            updateText = res.getString(R.string.update_refresh_date_within_halfhour);
        } else if (intervalTime < 23 * 60 * 60 * 1000) {// 显示几小时前
            updateText = res.getString(R.string.update_refresh_date_within_day, (intervalTime / 1000 / 60 / 60) + 1);
        } else if (intervalTime < 24 * 60 * 60 * 1000) {// 显示1天前
            updateText = res.getString(R.string.update_refresh_date_day_more);
        } else if (intervalTime < 48 * 60 * 60 * 1000) {// 显示昨天几点几分
            updateText = res.getString(R.string.update_refresh_date_yesterday, browseTime);
        } else if (intervalTime < 72 * 60 * 60 * 1000) {// 显示前天几点几分
            updateText = res.getString(R.string.update_refresh_date_before_yesterday, browseTime);
        } else {
            updateText = browseDate;
        }
        return updateText;
    }
    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
