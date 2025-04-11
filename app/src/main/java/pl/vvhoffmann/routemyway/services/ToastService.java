package pl.vvhoffmann.routemyway.services;

import android.content.Context;
import android.widget.Toast;

import pl.vvhoffmann.routemyway.RouteMyWayActivity;

public class ToastService {
    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
