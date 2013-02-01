package com.wangjh.testandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wangjh.testandroid.MainActivity.MyHandle;

public class ConnectThread extends Thread {

	MainActivity mMainActivity;
	MyHandle ActivityHandler;
	String cardNo;

	// OverItemT overitem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 1.从页面获得相应数据
		// 1.1获得半径数据
		int ra = mMainActivity.spinner.getSelectedItemPosition();
		System.out.println("线程得到半径数据：" + ra);
		String radius = "";
		switch (ra) {
		case 0:
			radius = "1";
			break;
		case 1:
			radius = "2";
			break;
		case 2:
			radius = "3";
			break;
		default:
			radius = "1";
			break;
		}
		// 1.2获得定位数据
		double userLongitude = 0;
		double userLatitude = 0;
		if (mMainActivity.userLongitudeE6 != 0.0d) {
			userLongitude = mMainActivity.userLongitudeE6/1E6;
			userLatitude = mMainActivity.userLatitudeE6/1E6;
		}
		// 1.3组装字符串
		String sendmsg = "type=phone&action=getGPS&params={\'action\':\'getGPS\',\'cardNO\':\'"
				+ cardNo
				+ "\',\'myGPSlat:\':\'"
				+ String.valueOf(userLatitude)
				+ "\',\'myGPSlon\':\'"
				+ String.valueOf(userLongitude)
				+ "\',\'rad\':\'" + radius + "\'}"+"</CFX>";
		// 2.启动socket连接，此IP地址需要更换成域名
		clientSocket cs = new clientSocket("183.93.75.33", 2010);

		// 发送请求数据
		if (cs.Send(sendmsg)) {
			// 开始读取返回数据
			String returnmsg = cs.ReadText();
			System.out.println("服务器返回数据：" + returnmsg);
			
			Message msg = new Message();
			msg.what = 1;
			Bundle data = new Bundle();

			ActivityHandler.sendMessage(msg);
		} else {
			System.out.println("没有发送成功数据！");
			
			Message msg = new Message();
			msg.what = 1;
			Bundle data = new Bundle();

			ActivityHandler.sendMessage(msg);
		}

		

		super.run();
	}

	public void setMainActivity(MainActivity mmMainActivity) {
		mMainActivity = mmMainActivity;
	}

	public void setActivityHandler(MyHandle myHandle) {
		ActivityHandler = myHandle;
	}

	public void setCardNo(String cardno) {
		cardNo = cardno;
	}

	class clientSocket {

		public String ip = null;// 连接服务器的IP
		public Integer port = null;// 连接服务器的端口
		Socket socket = null;// 套节字对象

		private Integer sotimeout = 2000;// 超时时间，以毫秒为单位

		public clientSocket(String iip, Integer pport) {
			ip = iip;
			port = pport;
			init();
		}

		public void init() {
			try {
				InetAddress address = InetAddress.getByName(ip);
				socket = new Socket(address, port);
				// socket.setSoTimeout(sotimeout);// 设置超时时间
//				socket.setKeepAlive(true);// 开启保持活动状态的套接字
				// close = !Send(socket, "2");// 发送初始数据，发送成功则表示已经连接上，发送失败表示已经断开
			} catch (UnknownHostException e) {
				socket = null;
				e.printStackTrace();
			} catch (IOException e) {
				socket = null;
				e.printStackTrace();
			}
		}

		public Boolean Send(String message) {
			boolean ok;
			try {
				OutputStream out = socket.getOutputStream();
				out.write(message.getBytes());
				out.flush();
				ok=true;
			} catch (Exception se) {
//				se.printStackTrace();
				ok=false;
			}
			Log.e("socket", "send result:"+ok);
			return ok;
		}


		public String ReadText() {
			String scc = "";
			try {
				socket.setSoTimeout(sotimeout);
				InputStream input = socket.getInputStream();
				byte[] sn = new byte[1024];
				int n = -1;

				while ((n = input.read(sn)) > 0) {
					String sc = new String(sn, 0, n);
					scc = scc + sc;
					if (scc.indexOf("</CFX>") != -1) {
                        break;
                    }
				}
				Log.e("socket", "receive result:"+scc);
			} catch (Exception se) {
				scc="";
				Log.e("socket", "receive Exception:"+se);
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return scc.split("</CFX>")[0];
			
		}
	}
}
