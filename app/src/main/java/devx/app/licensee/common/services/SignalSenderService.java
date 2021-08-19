package devx.app.licensee.common.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
/*
import com.trackschoolbus.driverconsole.R;
import com.trackschoolbus.driverconsole.constants.KeyConstants;
import com.trackschoolbus.driverconsole.model.SignalData;
import com.trackschoolbus.driverconsole.sharepreference.SessionConstants;
import com.trackschoolbus.driverconsole.sharepreference.TSBAppSession;
import com.trackschoolbus.driverconsole.utility.DriverConsoleAppUtils;*/

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;

import devx.app.licensee.common.ChangeRentalStatusRequest;
import devx.app.licensee.common.utils.ConstantApi;
import devx.app.licensee.webapi.Api;
import devx.app.licensee.webapi.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 14/10/19.
 * created by Kalindi
 */

public class SignalSenderService extends Service  {

	Context mContext;
	//Socket socket;
	NotificationManager notificationManager;
	public static int SIGNAL_ID = 100;

	private final LocationServiceBinder binder = new LocationServiceBinder();
	private static final String TAG = "LocationService";
	private LocationListener mLocationListener;
	private LocationManager mLocationManager;
	private final int LOCATION_INTERVAL = 500;
	private final int LOCATION_DISTANCE = 10;
	private Socket mSocket= null;
	public class LocationServiceBinder extends Binder {
		public SignalSenderService getService() {
			return SignalSenderService.this;
		}
	}
	public static String invoiceNumber ="";
	public static String rentalId = "";

	@SuppressLint("LongLogTag")
	@Override
	public void onCreate() {
		super.onCreate();
		this.mContext = getApplicationContext();
		Log.d("Here", "Service create");
		try {
			mSocket = IO.socket(ConstantApi.LIVE_BASE_URL);
			Log.d("Here", "Socket Connect");

		} catch (URISyntaxException e) {
			Log.i(TAG, "fail to connect socket", e);

		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("here","start service");
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("here", "bound");
		return binder;
	}


	public void closeSocketConnection(){
		if(mSocket.connected()) {
			mSocket.disconnect();
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationManager != null) {
			try {
				mLocationManager.removeUpdates(mLocationListener);
				closeSocketConnection();
			} catch (Exception ex) {
				Log.i("LocationService", "fail to remove location listeners, ignore", ex);
			}
		}
	}

	private class LocationListener implements android.location.LocationListener {
		private Location lastLocation = null;
		private final String TAG = "LocationListener";
		private Location mLastLocation;

		public LocationListener(String provider) {
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			mLastLocation = location;
			Log.i(TAG, "LocationChanged: " + location);
			JsonObject jsonObject1=new JsonObject();
			jsonObject1.addProperty("invoiceNumber", invoiceNumber);
			jsonObject1.addProperty("lat",location.getLatitude());
			jsonObject1.addProperty("long",location.getLongitude());
			mSocket.emit("licenseeLocation",jsonObject1);
			Toast.makeText(SignalSenderService.this, "LAT: " + location.getLatitude() + "\n LONG: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + status);
		}
	}
	private void initializeLocationManager() {
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}
	public void startTracking() {
		Log.d("Here", "Called");
		initializeLocationManager();
		mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

		try {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
			//join socket
			if(mSocket.connected()) {
				JsonObject jsonObject =new  JsonObject();
				jsonObject.addProperty("invoiceNumber", invoiceNumber);
				mSocket.emit("licenseeJoin", jsonObject);
			}
			else{
				mSocket.connect();
				JsonObject jsonObject =new JsonObject();
				jsonObject.addProperty("invoiceNumber", invoiceNumber);
				mSocket.emit("licenseeJoin", jsonObject);
			}
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}
	}

	public void stopTracking() {
		Log.d("here", "fn called");
		onDestroy();
//		Api api = ServiceGenerator.getApi();
//		Call<ResponseBody> call = api.setStatus(new ChangeRentalStatusRequest(rentalId, "delivered"));
//		call.enqueue(new Callback<ResponseBody>() {
//			@Override
//			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//				if (response.isSuccessful()){
//					Log.d("here", "response success");
//					onDestroy();
//				}
//			}
//
//			@Override
//			public void onFailure(Call<ResponseBody> call, Throwable t) {
//				Log.d("here", "response failed"+t.getMessage());
//			}
//		});

	}
}
