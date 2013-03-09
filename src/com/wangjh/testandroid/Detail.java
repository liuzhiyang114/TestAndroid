package com.wangjh.testandroid;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.baidu.mapapi.MapActivity;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.os.Bundle;
import android.os.Message;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;;

public class Detail extends MapActivity implements OnClickListener{

	
	Button btnre;
	TextView residue;
	TextView name;
	TextView count;
	TextView ParkDetail;
	TextView booktext;
	Button   download;
	Button	 Book;
	ImageView	parkimage;
	
	String strname;
    String strcount;
    String strresidue;
    String strParkDetail;
    Boolean flagbook=true;
	String sendmsg ="{" +   
		    "   \"action\" : \"getGPS\"," +  
		    "   \"success\" : \"true\"," + 
		    "   \"cardNO\" : \"1234567\"," +  
		    "   \"lat\" : \"38.61748660991222\"," + 
		    "   \"lon\" : \"104.12037670612494\"," +  
		    "   \"parkName\" : \"北京市万达停车场\"," +  
		    "   \"parkamount\" : \"1000\"," +  
		    "   \"restamount\" : \"25\"," +  
		    "   \"parkinfo\" : \"每小时收费情况说明\"" +  
		"}";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		btnre = (Button)findViewById(R.id.btnre);
		btnre.setOnClickListener(this);
		residue =(TextView)findViewById(R.id.residue);//剩余停车场位
		name =(TextView)findViewById(R.id.name);//停车场名
		count =(TextView)findViewById(R.id.count);//总停车位
		ParkDetail = (TextView)findViewById(R.id.ParkDetail);//停车场详细信息
		booktext= (TextView)findViewById(R.id.booktext);//预定信息
		parkimage	=(ImageView)findViewById(R.id.imagepark);//park图片
		download=(Button)findViewById(R.id.parkimage);//加载图片
		download.setOnClickListener(this);
		Book=(Button)findViewById(R.id.btnbook);//预定
		Book.setOnClickListener(this);
		
		//bundlebook();

		//设置剩余车位数字粗体
//		residue = (EditText)findViewById(R.id.residue) ;
//		TextPaint paint = residue.getPaint();  
//		paint.setFakeBoldText(true); 
	
		jjson();	
		
	}
	private void  jjson(){
		try{  
		    JSONTokener jsonParser = new JSONTokener(sendmsg);  
		    // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。  
		    // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）  
		    JSONObject detail = (JSONObject) jsonParser.nextValue();  
		   
		    // 接下来的就是JSON对象的操作了  
		    detail.getString("action");
		    detail.getString("success");
		    detail.getString("cardNO");
		    detail.getString("lat");
		    detail.getString("lon");
		    strname=detail.getString("parkName");
		    strcount=detail.getString("parkamount");
		    strresidue=detail.getString("restamount");
		    strParkDetail=detail.getString("parkinfo");
		    //修改Activity
		    name.setText(strname);//停车场名
		    count.setText(strcount);//总停车位
		    residue.setText(strresidue);//剩余停车场位
		    ParkDetail.setText(strParkDetail);//停车场详细信息
		} catch (JSONException ex) {  
		    // 异常处理代码  
			Toast.makeText(getApplicationContext(), "获取数据失败！",Toast.LENGTH_SHORT).show();
			this.finish();
		}  
		
	
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		case R.id.parkimage://图片加载
			downimage();
			
			break;  
		case R.id.btnbook://车位预定
			if(flagbook==true){
				Intent intent=new Intent(Detail.this,BookPark.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_right_left,R.anim.out_left_right); 
				bundlebook();

				if(flagbook==false){
					booktext.setText("您已预定停车位，信息如下：\n"+strname+"	A区43号"+"\n如果想要取消预定请点击取消");
			   		Book.setText("取消");
				}
				
			}
			else{
				DeleteBook();
			}
			break;
		default:
			this.finish();
			overridePendingTransition(R.anim.in_right_left,R.anim.out_left_right); 
			break;
		}
	}
	
	
	
	private void DeleteBook(){
		
		
		AlertDialog alertDialog = new AlertDialog.Builder(this)
    	.setTitle("取消预定")
    	.setMessage("是否取消预定？")
    	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which){
    		Toast.makeText(getApplicationContext(), "您已取消预定!",
   			     Toast.LENGTH_SHORT).show();
   		booktext.setText("您还未预定，点击预定停车位");
   		Book.setText("预定");
   		flagbook=true;
    	}
    	}).setNegativeButton("取消",
    	new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which){
    	return;
    	}}).create(); //创建对话框
    	alertDialog.show(); // 显示对话框
	}
	
	
	
	//Bundle
	private void bundlebook(){
		Bundle bundle=new Bundle();
		bundle.clear();
		flagbook=bundle.getBoolean("flagbook");
		
	}
	
	//加载图片
	private void downimage(){
		
		parkimage.setImageDrawable(null);
	}
	
	
}
