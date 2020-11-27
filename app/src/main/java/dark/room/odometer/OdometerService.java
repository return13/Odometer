package dark.room.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;



import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class OdometerService extends Service {
    private LocationManager locManager;
    public static final String PERMISSION_STRING= Manifest.permission.ACCESS_FINE_LOCATION;
    private static  Location lastLocation=null;
    private static  double distanceInMeters;
    private  final  IBinder binder=new OdometerBinder();
    private LocationListener listener;
    public void onCreate(){
        super.onCreate();
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (lastLocation==null){
                    lastLocation=location;
                }
                distanceInMeters+=location.distanceTo(lastLocation);
                lastLocation=location;

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) { }
            @Override
            public void onProviderDisabled(String arg0){}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
        };
        locManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED){
            String provider=locManager.getBestProvider(new Criteria(), true);
            if(provider!=null){
                locManager.requestLocationUpdates(provider, 1000, 1,listener);
            }
        }
    }

    public class OdometerBinder extends Binder{
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override

    public void onDestroy(){
        if(locManager!=null && listener!=null){
            if(ContextCompat.checkSelfPermission(this, PERMISSION_STRING)==PackageManager.PERMISSION_GRANTED){
                locManager.removeUpdates(listener);
            }
            locManager=null;
            listener=null;
        }
    }

    public double getDistance(){
        return this.distanceInMeters/1609.344;

    }

}
