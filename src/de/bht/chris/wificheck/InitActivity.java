package de.bht.chris.wificheck;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InitActivity extends Activity {

	/**
	 * for more informations, visit:
	 * https://github.com/android/platform_packages_apps_settings
	 * /blob/master/src/com/android/settings/TetherSettings.java
	 * http://stackoverflow
	 * .com/questions/3531801/enable-disable-usb-or-wifi-tethering
	 * -programmatically-on-android
	 * http://stackoverflow.com/questions/7509924/detect
	 * -usb-tethering-on-android/7830747#7830747
	 * http://stackoverflow.com/questions/3023226/android-2-2-wifi-hotspot-api
	 */

	private WifiManager wifiManager;
	private WifiInfo connectionInfo;
	private Button wifiButton;
	private ConnectivityManager connectivityManager;
	private boolean dataState;
	private Button dataButton;
	private Button tetheringButton;
	private boolean tetheringEnabled;
	private Button facebookButton;
	private String facebookUrl = "https://www.facebook.com";
	private String deutscheBahnUrl = "http://www.bahn.de";
	private String googleUrl = "http://www.google.de";
	private Button dbButton;
	private Button googleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connectionInfo = wifiManager.getConnectionInfo();

		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Button refreshButton = (Button) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkWifi();
			}
		});

		Button notifyButton = (Button) findViewById(R.id.notifyButton);
		notifyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				notifyForFun();
			}
		});

		wifiButton = (Button) findViewById(R.id.wifiButton);
		wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (wifiManager.isWifiEnabled()) {
					wifiManager.setWifiEnabled(false);
					wifiButton.setText(R.string.disable_wifi);
				} else {
					wifiManager.setWifiEnabled(true);
					wifiButton.setText(R.string.enable_wifi);
				}
				checkWifi();
			}
		});

		dataButton = (Button) findViewById(R.id.dataButton);
		dataButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					ReflectedIConnectivityManager conManager = new ReflectedIConnectivityManager(connectivityManager);
//					conManager.setMobileDataEnabled(!dataState);
					boolean mobileDataEnabled = conManager.getMobileDataEnabled();
					Method[] declaredMethods = conManager.getDeclaredMethods();
					checkWifi();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		tetheringButton = (Button) findViewById(R.id.buttonTethering);
		tetheringButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTetheringState(!tetheringEnabled);
			}
		});

		facebookButton = (Button) findViewById(R.id.facebookButton);
		facebookButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dataState || wifiManager.isWifiEnabled()) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(facebookUrl));
					startActivity(intent);
				}
			}

		});

		dbButton = (Button) findViewById(R.id.dbButton);
		dbButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dataState || wifiManager.isWifiEnabled()) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(deutscheBahnUrl));
					startActivity(intent);
				}
			}
		});

		googleButton = (Button) findViewById(R.id.googleButton);
		googleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dataState || wifiManager.isWifiEnabled()) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(googleUrl));
					startActivity(intent);
				}
			}
		});
		checkWifi();

		initWifiTethering();
	}

	private void initWifiTethering() {

	}

	private void notifyForFun() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification note = new Notification();
		note.tickerText = "hello gay";
		note.when = 100;
		notificationManager.notify(343455433, note);
	}

	private void changeTetheringState(Boolean enabled) {
		Method[] declaredMethods = wifiManager.getClass().getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.getName().equals("isWifiApEnabled")) {
				try {
					// Object invoke = method.invoke(wifiManager, null);
					boolean isTetheringEnabled = (Boolean) method
							.invoke(wifiManager);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (method.getName().equals("setWifiEnabled")) {
				try {
					method.invoke(wifiManager, enabled);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Object getReflectedConnectivityManager()
			throws NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException {
		final Class connectivityManagerClass = Class
				.forName(connectivityManager.getClass().getName());
		final Field mServiceField = connectivityManagerClass
				.getDeclaredField("mService");

		mServiceField.setAccessible(true);
		final Object iConnectivityManager = mServiceField
				.get(this.connectivityManager);
		return iConnectivityManager;
	}

	private void changeDataConnection(Class iConnectivityManagerClass,
			Object manager, boolean enabled) throws Exception {
		final Method setMobileDataEnabledMethod = iConnectivityManagerClass
				.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		setMobileDataEnabledMethod.setAccessible(true);
		dataState = enabled;
		setMobileDataEnabledMethod.invoke(manager, enabled);
	}

	private void checkWifi() {
		try {
			((TextView) findViewById(R.id.ip_address)).setText(connectionInfo
					.getIpAddress());
		} catch (Exception e) {
			((TextView) findViewById(R.id.ip_address))
					.setText("No IP Infos yet");
		}

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		try {
			((TextView) findViewById(R.id.wifi_type)).setText(activeNetworkInfo
					.getTypeName());
			switch (activeNetworkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
				((TextView) findViewById(R.id.extraInfo))
						.setText(connectivityManager.getNetworkInfo(
								ConnectivityManager.TYPE_MOBILE).getExtraInfo());
				break;
			case ConnectivityManager.TYPE_WIFI:
				((TextView) findViewById(R.id.extraInfo))
						.setText(connectivityManager.getNetworkInfo(
								ConnectivityManager.TYPE_WIFI).getExtraInfo());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			((TextView) findViewById(R.id.wifi_type))
					.setText("NO WIFI ACTIVATED");
		}

		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI DISABLED");
			((TextView) findViewById(R.id.wifiButton))
					.setText(R.string.enable_wifi);
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI ENABLED");
			((TextView) findViewById(R.id.wifiButton))
					.setText(R.string.disable_wifi);
			break;
		default:
			break;
		}

		try {
			Object reflectedConnectivityManager = getReflectedConnectivityManager();
			dataState = (Boolean) Class
					.forName(reflectedConnectivityManager.getClass().getName())
					.getDeclaredMethod("getMobileDataEnabled")
					.invoke(reflectedConnectivityManager);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataState) {
			dataButton.setText(R.string.disable_data);
		} else {
			dataButton.setText(R.string.enable_data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.init, menu);
		return true;
	}

}
