/**
 * Copyright (c) 86cis.com 2015 All Rights Reserved.
 */
package com.itsinic.sso;


import com.itsinic.sso.model.Ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebConstants {

	/**
	 * 单点登录标记
	 */
	public static Map<String, Ticket> TICKETS = new ConcurrentHashMap<String, Ticket>();

	/**
	 * cookie名称
	 */
	public final static String COOKIENAME = "SSOID";

	/**
	 * 是否安全协议
	 */
	public final static boolean SECURE = false;

	/**
	 * 密钥
	 */
	public final static String SECRETKEY = "111111112222222233333333";

	/**
	 * ticket有效时间
	 */
	public final static int TICKETTIMEOUT = 10080;

}