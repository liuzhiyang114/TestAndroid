package com.wangjh.testandroid;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.baidu.mapapi.RouteOverlay;


public class OverItemT extends ItemizedOverlay<OverlayItem> {

	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Drawable marker;
	private Context mContext;
	RouteOverlay routeOverlay=null;
	
//	private double mLat1 = 39.90923; // point1纬度
//	private double mLon1 = 116.357428; // point1经度
	
	MainActivity mMainActivity;
	

	public OverItemT(Drawable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
//	btndetail.setOnClickListener(new OnClickListener(){
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			Intent intent=new Intent(MainActivity.this, .class);
//			startActivity(intent);
//		}	
//  });
  
	public OverItemT(Drawable marker, Context context, int count) {
		super(boundCenterBottom(marker));
		
		this.marker = marker;
		this.mContext = context;

		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
//		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
		
		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
//		mGeoList.add(new OverlayItem(p1, "P1", "point1"));
		
//		populate();  //createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}
	
	public void addGeoPoint(double mmLat1,double mmLon1,String name,String msg){
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (mmLat1 * 1E6), (int) (mmLon1 * 1E6));
		
		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
		mGeoList.add(new OverlayItem(p1, name, msg));
		
		populate();  //createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}

	public void updateOverlay()
	{
		populate();
	}
	

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mGeoList.size();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
		Projection projection = mapView.getProjection(); 
		for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
			OverlayItem overLayItem = getItem(index); // 得到给定索引的item

			String title = overLayItem.getTitle();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(overLayItem.getPoint(), null); 

			// 可在此处添加您的绘制代码
			Paint paintText = new Paint();
			paintText.setColor(Color.BLUE);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x+10, point.y, paintText); // 绘制文本
		}

		super.draw(canvas, mapView, shadow);
		//调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mGeoList.get(i);
	}

	@Override
	// 处理点击事件
	protected boolean onTap(int i) {
		setFocus(mGeoList.get(i));
		// 更新气泡位置,并使之显示
		final GeoPoint pt = mGeoList.get(i).getPoint();
		MainActivity.mMapView.updateViewLayout( MainActivity.mPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		pt, MapView.LayoutParams.BOTTOM_CENTER));
		MainActivity.mPopView.setVisibility(View.VISIBLE);
		Toast.makeText(this.mContext, mGeoList.get(i).getSnippet(),
				Toast.LENGTH_LONG).show();
		
//		//跳转至Detail
//				Intent intent = new Intent();
//				intent.setClass(mMainActivity, Detail.class);
//				mMainActivity.startActivity(intent);
				
		
		//询问是否开始导航
		new AlertDialog.Builder(mMainActivity)   
		.setTitle("导航")  
		.setMessage("是否开始路线导航？")  
		.setPositiveButton("是", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				MKSearch mSearch= new MKSearch();
				mSearch.init(mMainActivity.mBMapMan, new MKSearchListener(){

		            @Override
		            public void onGetPoiDetailSearchResult(int type, int error) {
		            }
		            
					public void onGetDrivingRouteResult(MKDrivingRouteResult res,
							int error) {
						// 错误号可参考MKEvent中的定义
							if (error != 0 || res == null) {
							Toast.makeText(mMainActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
							return;
						}
						routeOverlay = new RouteOverlay(mMainActivity, MainActivity.mMapView);
					    // 此处仅展示一个方案作为示例
					    routeOverlay.setData(res.getPlan(0).getRoute(0));
//					    MainActivity.mMapView.getOverlays().clear();
					    MainActivity.mMapView.getOverlays().add(routeOverlay);
					    MainActivity.mMapView.invalidate();
					    
					    MainActivity.mMapView.getController().animateTo(res.getStart().pt);
					}
					@Override
					public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetBusDetailResult(MKBusLineResult arg0,
							int arg1) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetPoiResult(MKPoiResult arg0, int arg1,
							int arg2) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetRGCShareUrlResult(String arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetSuggestionResult(MKSuggestionResult arg0,
							int arg1) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetTransitRouteResult(
							MKTransitRouteResult arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onGetWalkingRouteResult(
							MKWalkingRouteResult arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
		        });
				
				// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
				MKPlanNode stNode = new MKPlanNode();
				stNode.pt = new GeoPoint((int)mMainActivity.userLatitudeE6,(int)mMainActivity.userLongitudeE6);
				MKPlanNode enNode = new MKPlanNode();
				enNode.pt = pt;
				mSearch.drivingSearch(null, stNode, null, enNode);
			}	
		})  
		.setNegativeButton("否", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(MainActivity.mMapView.getOverlays().contains(routeOverlay)){
					MainActivity.mMapView.getOverlays().remove(routeOverlay);
//					routeOverlay=null;
				    MainActivity.mMapView.invalidate();
				}
			}
			
		})  
		.show();
		return true;
	}

	@Override
	public boolean  onTap(GeoPoint arg0, MapView arg1) {
		// TODO Auto-generated method stub
		// 消去弹出的气泡
		MainActivity.mPopView.setVisibility(View.GONE);
//		//跳转至Detail
//		Intent intent = new Intent();
//		intent.setClass(mMainActivity, Detail.class);
//		mMainActivity.startActivity(intent);
		
		return super.onTap(arg0, arg1);
	}
	
	 
	public void setMainActivity(MainActivity mmMainActivity){
		mMainActivity=mmMainActivity;
	}

}
