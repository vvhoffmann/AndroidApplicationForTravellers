package com.example.bachelorthesisapp;
import com.example.bachelorthesisapp.mapsActivity.DirectionsHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

public class PointUtils
{
    public static ArrayList<LatLng> createRandomPoints(int size) {
        ArrayList<LatLng> ArrayList = new ArrayList<LatLng>(size);
        Random random = new Random();
        for(int i=0;i<size;++i){
            LatLng point = new LatLng(random.nextInt(90),random.nextInt(90));
            if(ArrayList.contains(point)) --i;
            else ArrayList.add(point);
        }
        System.out.println(" ");
        return ArrayList;
    }

    public static double calculateShortestWalatToPoint(LatLng a, LatLng b, LatLng p)
    {
        return Math.min(Math.min(distance(a,p),distance(b,p)),distance(projection(a, b, p),p));
    }

    public static LatLng projection(LatLng a, LatLng b, LatLng c)
    {
        double BAy=b.latitude-a.latitude,BAlng=b.longitude-a.longitude,CAlat=c.latitude-a.latitude,CAlng=c.longitude-a.longitude;
        double BAy2=BAy*BAy,BAlng2=BAlng*BAlng,BA2=BAy2+BAlng2;
        return new LatLng
        (
                (a.longitude*BAy2+CAlat*BAlng*BAy+c.longitude*BAlng2)/BA2,
                (a.latitude*BAy2+CAlng*BAy*BAlng+c.latitude*BAy2)/BA2
        );
    }


    public static boolean less(LatLng a,LatLng b)
    {
        return ( (a.latitude<b.latitude) || ( (a.latitude==b.latitude) && (a.longitude<b.longitude) ) );
    }

    public static int min(LatLng a,LatLng b)
    {
        if(less(a,b)) return -1;
        if(less(b,a)) return +1;
        return 0;
    }

    public static double distance(LatLng a, LatLng b) { return (double) Math.hypot(a.longitude-b.longitude,a.latitude-b.latitude); }

    public static long doubleDiv(double a,double b) { return (long)(a/b); }

    public static double doubleMod(double a,double b) { return a-doubleDiv(a,b)*b; }

    public static double angle(LatLng a, LatLng b)
    {
        return doubleMod((double) (Math.atan2(b.latitude-a.latitude,b.longitude-a.longitude)+Math.PI), (double) Math.PI);
    }

}