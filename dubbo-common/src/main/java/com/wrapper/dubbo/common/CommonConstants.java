package com.wrapper.dubbo.common;

/**
 * @author codel
 * @since 2020-01-02
 */
public class CommonConstants {

    public static final String WD_BIZ_TIME = "wd_biz_time";
    public static final String CONSUMER_APP_NAME = "consumer_app_name";

    public static String TRACE_ID                            = "trace_id";

    public static String SPAN_ID                             = "span_id";

    /* the consumer ip of the rpc call */
    public static String FROM_IP                             = "from_ip";

    public static String URL_PATH                            = "url_path";

    public static String APP_NAME = System.getProperty("project.name", "");

    public static String APP_NAME_KEY = "appname";

    public static String REGION_NAME = System.getProperty("region.name", "");

    public static String REGION = "region";

    public static final String LOCAL = "LOCAL";

    public static final String REMOTE = "REMOTE";


}
