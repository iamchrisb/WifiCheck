package de.bht.chris.wificheck;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InitActivity extends Activity {

    private WifiManager wifiManager;
	private WifiInfo connectionInfo;
	private Button wifiButton;
	private ConnectivityManager connectivityManager;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectionInfo = wifiManager.getConnectionInfo();
        
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        checkWifi();
        
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkWifi();
			}
		});
        
        wifiButton = (Button) findViewById(R.id.wifiButton);
        wifiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (wifiManager.isWifiEnabled()) {
					wifiManager.setWifiEnabled(false);
					wifiButton.setText(R.string.disable_wifi);
				}else {
					wifiManager.setWifiEnabled(true);
					wifiButton.setText(R.string.enable_wifi);
				}
				checkWifi();
			}
		});
    	
    }


    private void checkWifi() {
        try {
        	((TextView) findViewById(R.id.ip_address)).setText(connectionInfo.getIpAddress());
		} catch (Exception e) {
			((TextView) findViewById(R.id.ip_address)).setText("No IP Infos yet");
		}
        
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        try {
        	((TextView) findViewById(R.id.wifi_type)).setText(activeNetworkInfo.getTypeName());
        	switch (activeNetworkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
				((TextView) findViewById(R.id.extraInfo)).setText(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getExtraInfo());
				break;
			case ConnectivityManager.TYPE_WIFI: 
				((TextView) findViewById(R.id.extraInfo)).setText(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getExtraInfo());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			((TextView) findViewById(R.id.wifi_type)).setText("NO WIFI ACTIVATED");
		}
    	
        switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI DISABLED");
			((TextView) findViewById(R.id.wifiButton)).setText(R.string.enable_wifi);
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI ENABLED");
			((TextView) findViewById(R.id.wifiButton)).setText(R.string.disable_wifi);
			break;
		default:
			break;
		}
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.init, menu);
        return true;
    }
    
}
