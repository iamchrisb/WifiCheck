package de.bht.chris.wificheck;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.net.ConnectivityManager;

public class ReflectedIConnectivityManager {

	private Object iConnectivityManager;
	private ConnectivityManager manager;
	private Class<?> iConManClass;

	public ReflectedIConnectivityManager(ConnectivityManager connectivityManager) {
		manager = connectivityManager;
		try {
			final Class connectivityManagerClass = Class
					.forName(connectivityManager.getClass().getName());
			final Field mServiceField = connectivityManagerClass
					.getDeclaredField("mService");

			mServiceField.setAccessible(true);
			iConnectivityManager = mServiceField.get(connectivityManager);
			iConManClass = Class.forName(iConnectivityManager.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean getMobileDataEnabled() {
		try {
			Method method = iConManClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true);
			Boolean isEnabled = (Boolean) method.invoke(this.iConnectivityManager);
			return isEnabled;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean setMobileDataEnabled(boolean enabled) {
		try {
			Method setMobileDataEnabledMethod = iConManClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(this.iConnectivityManager, enabled);
			return enabled;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public Object getiConnectivityManager() {
		return iConnectivityManager;
	}

	public Method[] getDeclaredMethods() {
		return iConManClass.getDeclaredMethods();
	}

}
