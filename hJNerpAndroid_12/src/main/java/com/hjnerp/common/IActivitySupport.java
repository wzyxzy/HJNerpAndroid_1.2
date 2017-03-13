package com.hjnerp.common;

import android.app.ProgressDialog;
import android.content.Context;

import com.hjnerp.model.LoginConfig;
import com.hjnerp.widget.WaitDialogRectangle;

/**
 * Activity帮助支持类接口.
 * 
 * @author 李庆义
 */
public interface IActivitySupport {
	
	
	/**
	 * 
	 * 获取EimApplication.
	 * 
	 * @author 李庆义
	 * @update 2012-7-6 上午9:05:51
	 */
    EapApplication getEapApplication();

	/**
	 * 
	 * 终止服务.
	 * 
	 * @author 李庆义
	 * @update 2012-7-6 上午9:05:51
	 */
    void stopService();

	/**
	 * 
	 * 开启服务.
	 * 
	 * @author 李庆义
	 * @update 2012-7-6 上午9:05:44
	 */
    void startService();

	/**
	 * 
	 * 校验网络-如果没有网络就弹出设置,并返回true.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:03:56
	 */
    boolean validateInternet();

	/**
	 * 
	 * 校验网络-如果没有网络就返回true.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:05:15
	 */
    boolean hasInternetConnected();

	/**
	 * 
	 * 退出应用.
	 * 
	 * @author 李庆义
	 * @update 2012-7-6 上午9:06:42
	 */
    void isExit();

	/**
	 * 
	 * 判断GPS是否已经开启.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:04:07
	 */
    boolean hasLocationGPS();

	/**
	 * 
	 * 判断基站是否已经开启.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:07:34
	 */
    boolean hasLocationNetWork();

	/**
	 * 
	 * 检查内存卡.
	 * 
	 * @author 李庆义
	 * @update 2012-7-6 上午9:07:51
	 */
    void checkMemoryCard();

	/**
	 * 
	 * 获取进度条.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:14:38
	 */
    ProgressDialog getProgressDialog();

	
	WaitDialogRectangle getWaitDialogRectangle();
	/**
	 * 
	 * 返回当前Activity上下文.
	 * 
	 * @return
	 * @author 李庆义
	 * @update 2012-7-6 上午9:19:54
	 */
    Context getContext();

	 

	/**
	 * 
	 * 保存用户配置.
	 * 
	 * @param loginConfig
	 * @author 李庆义
	 * @update 2012-7-6 上午9:58:31
	 */
    void saveLoginConfig(LoginConfig loginConfig);

	/**
	 * 
	 * 获取用户配置.
	 * 
	 * @param loginConfig
	 * @author 李庆义
	 * @update 2012-7-6 上午9:59:49
	 */
    LoginConfig getLoginConfig();

	 

	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内容
	 * @param activity
	 * @author 李庆义
	 * @update 2012-5-14 下午12:01:55
	 */
    void setNotiType(int iconId, String contentTitle,
                     String contentText, Class activity, String from);
	
	
	
}
