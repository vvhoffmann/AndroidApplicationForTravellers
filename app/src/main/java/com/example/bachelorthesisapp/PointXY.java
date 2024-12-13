package com.example.bachelorthesisapp;

import android.graphics.PointF;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class PointXY extends PointF
{
    public PointXY(float x, float y) { super(x, y); }

    public static ArrayList<PointXY> createRandomPoints(int size) {
        ArrayList<PointXY> arrayList = new ArrayList<PointXY>(size);
        Random random = new Random();
        for(int i=0;i<size;++i){
            PointXY point = new PointXY(random.nextInt(100),random.nextInt(100));
            if(arrayList.contains(point)) --i;
            else arrayList.add(point);
        }
        /*arrayList.add(new Point(75,43));
        arrayList.add(new Point(94,72));
        arrayList.add(new Point(88,51));
        arrayList.add(new Point(59,20));
        arrayList.add(new Point(38,93));
        arrayList.add(new Point(66,19));
        arrayList.add(new Point(10,50));
        arrayList.add(new Point(74,8));
        arrayList.add(new Point(96,59));
        arrayList.add(new Point(63,60));
        arrayList.add(new Point(63,48));
        arrayList.add(new Point(51,49));
        arrayList.add(new Point(96,33));
        arrayList.add(new Point(82,46));
        arrayList.add(new Point(92,5));
        arrayList.add(new Point(63,20));
        arrayList.add(new Point(4,95));
        arrayList.add(new Point(95,67));
        arrayList.add(new Point(71,45));
        arrayList.add(new Point(58,51));
        arrayList.add(new Point(23,77));
        arrayList.add(new Point(96,7));
        arrayList.add(new Point(90,1));
        arrayList.add(new Point(76,11));
        arrayList.add(new Point(75,20));*/
        System.out.println(" ");
        return arrayList;
    }

    public static float calculateShortestWayToPoint(PointXY a, PointXY b, PointXY p)
    {
        return Math.min(Math.min(distance(a,p),distance(b,p)),distance(projection(a, b, p),p));
    }

    public static PointXY projection(PointXY a, PointXY b, PointXY c)
    {
        float BAy=b.y-a.y,BAx=b.x-a.x,CAy=c.y-a.y,CAx=c.x-a.x;
        float BAy2=BAy*BAy,BAx2=BAx*BAx,BA2=BAy2+BAx2;
        return new PointXY
        (
                (a.x*BAy2+CAy*BAx*BAy+c.x*BAx2)/BA2,
                (a.y*BAx2+CAx*BAy*BAx+c.y*BAy2)/BA2
        );
    }

    public static boolean less(PointXY a,PointXY b)
    {
        return ( (a.y<b.y) || ( (a.y==b.y) && (a.x<b.x) ) );
    }

    public static int min(PointXY a,PointXY b)
    {
        if(less(a,b)) return -1;
        if(less(b,a)) return +1;
        return 0;
    }

    public static float distance(PointXY a, PointXY b) { return (float) Math.hypot(a.x-b.x,a.y-b.y); }

    public static long floatDiv(float a,float b) { return (long)(a/b); }

    public static float floatMod(float a,float b) { return a-floatDiv(a,b)*b; }

    public static float angle(PointXY a, PointXY b)
    {
        return floatMod((float) (Math.atan2(b.y-a.y,b.x-a.x)+Math.PI), (float) Math.PI);
    }

    @NonNull
    @Override
    public String toString() {
        return " [" + x + ", " + y +']';
    }
}