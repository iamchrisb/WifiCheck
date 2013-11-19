package de.bht.chris.wificheck;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        ((TextView) findViewById(R.id.ip_address)).setText(connectionInfo.getIpAddress());
        
    	switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI DISABLED");
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			((TextView) findViewById(R.id.wifiState)).setText("WIFI ENABLED");
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
