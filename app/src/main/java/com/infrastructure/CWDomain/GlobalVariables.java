package com.infrastructure.CWDomain;

import android.app.Activity;

public class GlobalVariables {
	public  static final boolean ISLOCALDATA=false;

	public static final String ACTION_DATA_RELOAD_OK="com.chinawit.data_reload_ok";
	public static final String ACTION_DATA_RELOAD_ERROR="com.chinawit.data_reload_error";
	public static final String DATA_RESULT="com.chinawit.data_result";

	public static final String DATA_DECODE_RESULT="com.infrastructure.CWDataDecoder.DataResult";
	public static final String DATAEntity_PACKAGE="com.Entity";

	public static final String PRIMARY_KEY_NAME="AutoID";
	
    public final static int RESULT_OK = Activity.RESULT_OK;
    public final static int RESULT_ERROR = Activity.RESULT_FIRST_USER;
    public final static int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public final static int REQUEST_DEFAULT = 0;
    
}
