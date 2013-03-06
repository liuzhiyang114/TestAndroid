package com.wangjh.testandroid;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MapActivity {

	BMapManager mBMapMan = null;
	MKLocationManager mLocationManager=null;
	LocationListener locationListener=null;
	static View mPopView = null;	// 点击mark时弹出的气泡View
	static MapView mMapView = null;
	
	private double defaultLatitudeE6 = 39.91334 * 1E6;// 纬度
    private double defaultLongitudeE6 = 116.404359 * 1E6;// 经度
    double userLongitudeE6 = 0.0d;// 纬度
    double userLatitudeE6 = 0.0d;// 经度
    
//    GeoPoint mypoint;
    OverItemT overitem=null;
    MyLocationOverlay mylocTest=null;
    
//    boolean isLockMyLocation=true;
    
    Button btn1;
    Button btn2;
    Spinner spinner;
    MyHandle myhandle;
    ConnectThread connectThread;
	
    String cardNo="123456";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);           //设置标题栏样式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_main);
      //spinner选择
        ArrayAdapter aa=ArrayAdapter.createFromResource(this,R.array.planets,android.R.layout.simple_spinner_item);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner=(Spinner) findViewById(R.id.mySpinner);  
		spinner.setAdapter(aa);
        spinner.setPrompt("请选择搜索范围");
        
        btn1=(Button)findViewById(R.id.button1);
        btn2=(Button)findViewById(R.id.button2);
        checkGPS();
        checkNET();
        myhandle=new MyHandle();
        
        btn1.setText("定位");
        btn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(userLongitudeE6!=0.0d||userLatitudeE6!=0.0d){
					startLocalMyPoint();
					mMapView.getController().animateTo(new GeoPoint((int) (userLatitudeE6),
			                (int) (userLongitudeE6)));
					mMapView.invalidate();
				}else{
				}
			}	
        });
        btn2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectThread=new ConnectThread();
				connectThread.setCardNo(cardNo);
				connectThread.setMainActivity(MainActivity.this);
				connectThread.setActivityHandler(myhandle);
				connectThread.start();
			}	
        });
        
        
        ParkApp app = (ParkApp)this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			mBMapMan=app.mBMapMan;
			app.mBMapMan.init(app.mStrKey, new ParkApp.MyGeneralListener());
		}else{
			mBMapMan=app.mBMapMan;
		}
		mBMapMan.start();        
		super.initMapActivity(mBMapMan);
		
         
        mMapView = (MapView) findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
         
        final MapController mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) ( defaultLatitudeE6),
                (int) (defaultLongitudeE6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);  //设置地图中心点
        mMapController.setZoom(16);    //设置地图zoom级别
        
       
        //开始定位
        startLocalMyPoint();
       
		
		// 创建点击mark时的弹出泡泡
		mPopView=super.getLayoutInflater().inflate(R.layout.popview, null);
		mMapView.addView( mPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		null, MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
		TextView tx1=(TextView)findViewById(R.id.text1);
		tx1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//跳转到详细页面
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Detail.class);
				MainActivity.this.startActivity(intent);
			}
		});
     		
		Log.e("status", "MainActivity onCreate");
    }

    //开启自己定位
    public boolean startLocalMyPoint(){
    	if (mLocationManager != null) {
    	
    	}else{
    		// 初始化Location模块
            mLocationManager = mBMapMan.getLocationManager();
    	}
    		 
        // 通过enableProvider和disableProvider方法，选择定位的Provider
        mLocationManager.enableProvider(MKLocationManager.MK_GPS_PROVIDER);
        mLocationManager.enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
        if(locationListener==null){
        	locationListener=new LocationListener() {
    			@Override
    			public void onLocationChanged(Location location) {
    				// TODO Auto-generated method stub
    				if (location != null) {  
    					userLatitudeE6 = location.getLatitude() * 1E6;  
    					userLongitudeE6 = location.getLongitude() * 1E6;
    				}
    			}    	
            };
        }
        mLocationManager.requestLocationUpdates(locationListener);
        mLocationManager.setNotifyInternal(5, 2);
        
     // 添加定位图层
        if(mylocTest==null){
        	mylocTest = new MyLocationOverlay(this, mMapView);
            mylocTest.enableMyLocation(); // 启用定位
            mylocTest.enableCompass();    // 启用指南针
        }
        if(mMapView.getOverlays().contains(mylocTest)){
        	mMapView.getOverlays().remove(mylocTest);
        	mMapView.getOverlays().add(mylocTest);
        }else{
        	mMapView.getOverlays().add(mylocTest);
        }   
        mMapView.invalidate();
    	
    	return true;
    }
    
    //判断GPS
    private void checkGPS() {
		boolean result=true;
		LocationManager lmanager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(!lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
//			Toast.makeText(getApplicationContext(), "GPS未开启",
//				     Toast.LENGTH_SHORT).show();
			AlertDialog alertDialogGPS = new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
	    	.setTitle("GPS定位")
	    	.setMessage("您的GPS未开启，是否设置？")
	    	.setPositiveButton("设置", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which){
	    	Intent intent= new Intent(
	    			android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
	    			);
	    	startActivityForResult(intent, 0);
	    	}
	    	}).setNegativeButton("取消",
	    	new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which){
	    	return;
	    	}}).create(); //创建对话框
	    	alertDialogGPS.show(); // 显示对话框
			result=false;
		}
	}
    
    
    //判断网络状态
    private void checkNET() {
    	boolean flag = false;
        ConnectivityManager manager = (ConnectivityManager)getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(manager.getActiveNetworkInfo() != null)
        {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if(!flag)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("网络状态");
            builder.setMessage("当前网络不可用，是否设置网络？");
            builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int which){
    	    		Intent intent= new Intent(
    		    			android.provider.Settings.ACTION_SETTINGS
    		    			);
    		    	startActivityForResult(intent, 0);
    	    	}
    	    	});
    	    	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    	        builder.create();
                builder.show();
            }

    	}
            
            
             
       
    //退出
    private void showTips(){
    	AlertDialog alertDialog = new AlertDialog.Builder(this)
    	.setTitle("退出程序")
    	.setMessage("是否退出停车小助手？")
    	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which){
    	finish();
    	}
    	}).setNegativeButton("取消",
    	new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which){
    	return;
    	}}).create(); //创建对话框
    	alertDialog.show(); // 显示对话框
    	}
//退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			this.showTips();
			return false;
			}
			return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
		if(mLocationManager!=null){
			if(locationListener!=null){
				mLocationManager.removeUpdates(locationListener);
			}
		}
	    if (mBMapMan != null) {
	    	mBMapMan.stop();
	        mBMapMan.destroy();
//	        mBMapMan = null;
	    }
	    Log.e("status", "MainActivity onDestroy");
	    super.onDestroy();
	    this.getApplication().onTerminate();
	}
	@Override
	protected void onPause() {
	    if (mBMapMan != null) {
	    	if(mLocationManager!=null){
				if(locationListener!=null){
					mLocationManager.removeUpdates(locationListener);
				}
			}
	        mBMapMan.stop();
	    }
	    Log.e("status", "MainActivity onPause");
	    super.onPause();
	}
	@Override
	protected void onResume() {
	    if (mBMapMan != null) {
	        mBMapMan.start();
	    }else{
	    	ParkApp app = (ParkApp)this.getApplication();
			if (app.mBMapMan == null) {
				app.mBMapMan = new BMapManager(getApplication());
				mBMapMan=app.mBMapMan;
				app.mBMapMan.init(app.mStrKey, new ParkApp.MyGeneralListener());
			}else{
				mBMapMan=app.mBMapMan;
			}
			mBMapMan.start();        
	    }
	    startLocalMyPoint();
	    Log.e("status", "MainActivity onResume");
	    super.onResume();
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.e("status", "MainActivity onStart");
		super.onStart();
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e("status", "MainActivity onStop");
		super.onStop();
	}



	public class MyHandle extends Handler{
		public MyHandle() {
		}
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case 0:
					Toast.makeText(MainActivity.this, "没有获得服务器端数据", Toast.LENGTH_LONG).show();
					break;
				case 1:
					double mLat1 = 39.90923; // point1纬度
					double mLon1 = 116.357428; // point1经度
					 // 添加ItemizedOverlay
			 		Drawable marker = getResources().getDrawable(R.drawable.icon_ar_tag);  //得到需要标在地图上的资源
			 		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
			     				.getIntrinsicHeight());   //为maker定义位置和边界
			 		if(overitem!=null){
			 			if(mMapView.getOverlays().contains(overitem)){
				 			mMapView.getOverlays().remove(overitem);
				 		}
			 		}
			 		overitem = new OverItemT(marker, MainActivity.this, 1);
			 		overitem.setMainActivity(MainActivity.this);
			 		overitem.addGeoPoint(mLat1, mLon1,"Parkname","restamount，parkamount");
			 		mMapView.getOverlays().add(overitem); //添加ItemizedOverlay实例到mMapView
			 		//将目标地址移动到中心
			 		mMapView.getController().animateTo(new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6)));
					mMapView.invalidate();
					break;
				default:
					
			}
			super.handleMessage(msg);
		}	
	}
}

