package com.qxshikong.myvideoapp.util;

public class HttpUtil {
	private static final String ServerURL = "http://42.62.40.129:404";

	/**
	 * 分类内容列表
	 */
	public static String News_URL = ServerURL + "/app/video/new-video-list";
	/**
	 * 分类hot内容列表
	 */
	public static String Hot_URL = ServerURL + "/app/video/hot-video-list";
	/**
	 * 分类vip内容列表
	 */
	public static String VIP_URL = ServerURL + "/app/video/vip-video-list";
	/**
	 * 分类标签内容列表
	 */
	public static String Tags_URL = ServerURL + "/app/video/tags-list";

	/**
	 * 搜索列表
	 */
	public static String Search_URL = ServerURL + "/app/video/search-video-list";
	/**
	 * 搜索推荐记录列表
	 */
	public static String Search_LIST_URL = ServerURL + "/app/video/search-list";

	/**
	 * 详情接口app/video/show-video
	 */
	public static String Details_URL = ServerURL + "/app/video/show-video";
	/**
	 * 登陆接口app/video/show-video
	 */
	public static String Login_URL = ServerURL + "/app/video/user-reg";/**
	 * app列表接口/app/video/app-download-list
	 */
	public static String Download_URL = ServerURL + "/app/video/app-download-list";


	/**
	 * 历史收藏
	 */
	public static final String History_URL = ServerURL + "/app/video/look-video";
	/**
	 * 取消收藏接口
	 */
	public static final String DelHistory_URL = ServerURL + "/app/video/del-history";
	/**
	 * 收藏接口
	 */
	public static final String Collection_URL = ServerURL + "/app/video/add-collection";
	/**
	 * 取消收藏接口
	 */
	public static final String DelCollection_URL = ServerURL + "/app/video/del-collection";

	/**
	 * 根据标签ID查询列表列表
	 */
	public static String TID_URL = ServerURL + "/app/video/tag-video-list";
	/**
	 * vip订阅
	 */
	public static String VIP_Buy_URL = ServerURL + "/app/video/up-to-vip";
	public static final String Status = "status";
	public static final String Info = "info";
	public static final String Data = "data";
	public static final String Search = "search";
	public static final String Tid = "tid";
}