package com.hjnerp.activity.im;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.LOG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hjnerp.activity.im.adapter.LocationAdatper;
import com.hjnerp.common.ActivitySupport;
import com.hjnerp.common.Constant;
import com.hjnerp.model.LocationInfo;
import com.hjnerp.util.DateUtil;
import com.hjnerp.util.Log;
import com.hjnerp.util.imageCompressUtil;
import com.hjnerpandroid.R;

/**
 * 用于发送位置的界面
 * 
 * @ClassName: LocationActivity
 * @Description: TODO
 * @author liuhaijian
 * 
 */
public class LocationActivity extends ActivitySupport implements
		OnGetGeoCoderResultListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	private String path;

	private OverlayOptions ooA;
	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

	GeoCoder mSearch = null; // 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，因此需要采取反编码方式去搜索此经纬度代表的地址

	static BDLocation lastLocation = null;
	private LocationInfo currenctPostion;
	private ListView lv;//附近建筑物
	private LocationAdatper adapter;
	private List<LocationInfo> lists;
	

	BitmapDescriptor bdgeo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo); 
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if(lastLocation!=null){
					location = currenctPostion.getInfo();
					Intent intent = new Intent();
					intent.putExtra("y", location.location.latitude);// 经度
					intent.putExtra("x", location.location.longitude);// 维度
					intent.putExtra("address", location.address);
					intent.putExtra("path", path);
					setResult(RESULT_OK, intent);
					finish();
				}else{
//					ShowToast("获取地理位置信息失败!");
				}
				break;

			default:
				break;
			}
		}
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			getSupportActionBar().setTitle("位置");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			setContentView(R.layout.activity_location);
			initBaiduMap();
			setupListener();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.i("info", "获取位置异常："+e.toString());
		}
	}

	private void setupListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击某个item将名字  地址发送给聊天界面
				adapter.setSelected(position);
				currenctPostion = adapter.getItem(position);
				//维度在前，经度在后
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(currenctPostion.getInfo().location));
				//显示当前位置图标
				mBaiduMap.clear();
				ooA = new MarkerOptions().position(currenctPostion.getInfo().location).icon(bdgeo).zIndex(9);
				mBaiduMap.addOverlay(ooA);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat_send, menu);
		if("select".equals(getIntent().getStringExtra("type"))){
			
			return super.onCreateOptionsMenu(menu);
		}else{
			return false;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_send:
			//发送位置
			gotoChatPage();
			
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	private void initBaiduMap() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		lv = (ListView) findViewById(R.id.position_lv);
		adapter = new LocationAdatper(this, lists);
		lv.setAdapter(adapter);
		mBaiduMap = mMapView.getMap();
		//设置缩放级别
		mBaiduMap.setMaxAndMinZoomLevel(18, 13);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);

		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18f));
		if (type.equals("select")) {// 选择发送位置
//			initTopBarForBoth("位置", R.drawable.btn_login_selector, "发送",
//					new onRightImageButtonClickListener() {
//
//						@Override
//						public void onClick() {
//							// TODO Auto-generated method stub
//							gotoChatPage();
//						}
//					});
//			mHeaderLayout.getRightImageButton().setEnabled(false);
			initLocClient();
		} else {// 查看当前位置
//			initTopBarForLeft("位置");
			Bundle b = intent.getExtras();
			lv.setVisibility(View.GONE);
			LatLng latlng = new LatLng(b.getDouble("latitude"), b.getDouble("longtitude"));//维度在前，经度在后
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			//显示当前位置图标
			OverlayOptions ooA = new MarkerOptions().position(latlng).icon(bdgeo).zIndex(9);
			mBaiduMap.addOverlay(ooA);
		}

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

	}

	/**
	 * 回到聊天界面
	 * @Title: gotoChatPage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void gotoChatPage() {
		mMapView.getMap().snapshot(new SnapshotReadyCallback() {
			
			@Override
			public void onSnapshotReady(Bitmap bit) {
				String picName =  sputil.getMyId()
						+ DateUtil.getCurrentTime() + ".jpg";
				path = picName;
				File f = new File(Constant.CHAT_CACHE_DIR, picName);
				if (f.exists()) {
					f.delete();
				}
				try {
					FileOutputStream out = new FileOutputStream(f);
					bit.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.flush();
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("info", e.toString());
				}
				String filePath = Constant.CHAT_CACHE_DIR + picName;
				Bitmap bitmap = imageCompressUtil.getCompressImage(filePath);
				try {
					imageCompressUtil.saveImage(bitmap, filePath);
					System.gc();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		});
//		if(lastLocation!=null){
//			Intent intent = new Intent();
//			intent.putExtra("y", lastLocation.getLongitude());// 经度
//			intent.putExtra("x", lastLocation.getLatitude());// 维度
//			intent.putExtra("address", lastLocation.getAddrStr());
//			setResult(RESULT_OK, intent);
//			this.finish();
//		}else{
////			ShowToast("获取地理位置信息失败!");
//		}
	}

	private void initLocClient() {
//		 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
//		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
//				com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// 设置产品线
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
		    mLocClient.requestLocation();

		if (lastLocation != null) {
			// 显示在地图上
			LatLng ll = new LatLng(lastLocation.getLatitude(),
					lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
						.getLongitude()) {
//					BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocClient.stop();
					return;
				}
			}
			lastLocation = location;
			
//			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
//					+ location.getLatitude() + ",地址 = "
//					+ lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			} else {
				// 反Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// 显示在地图上
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			//设置按钮可点击
//			mHeaderLayout.getRightImageButton().setEnabled(true);
		}

	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//				ShowToast("网络出错");
			}
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			ShowToast("抱歉，未能找到结果");
			return;
		}
//		BmobLog.i("反编码得到的地址：" + result.getAddress());
		lastLocation.setAddrStr(result.getAddress());
		lists = new ArrayList<LocationInfo>();
		for (int i = 0; i < result.getPoiList().size(); i++) {
			if(i==0){
				lists.add(new LocationInfo(result.getPoiList().get(i),true));
			}else{
				lists.add(new LocationInfo(result.getPoiList().get(i),false));
			}
		}
		currenctPostion = lists.get(0);
		adapter.setData(lists);
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		location = null;
	}

	@Override
	public void onDestroy() {
		if(mLocClient!=null && mLocClient.isStarted()){
			// 退出时销毁定位
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// 回收 bitmap 资源
		bdgeo.recycle();
	}

}
