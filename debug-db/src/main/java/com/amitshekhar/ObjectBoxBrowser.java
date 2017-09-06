package com.amitshekhar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amitshekhar.server.ClientServer;
import com.amitshekhar.utils.NetworkUtils;

import java.lang.reflect.Method;

import io.objectbox.BoxStore;

public class ObjectBoxBrowser {

    private static final String TAG = ObjectBoxBrowser.class.getSimpleName();
    private static final int DEFAULT_PORT = 8080;
    private static ClientServer clientServer;
    private static String addressLog = "not available";

    private ObjectBoxBrowser() {
        // This class in not publicly instantiable
    }

    static void initialize(Context context) {
        int portNumber;

        try {
            portNumber = Integer.valueOf(context.getString(R.string.PORT_NUMBER));
        } catch (NumberFormatException ex) {
            Log.e(TAG, "PORT_NUMBER should be integer", ex);
            portNumber = DEFAULT_PORT;
            Log.i(TAG, "Using Default port : " + DEFAULT_PORT);
        }

        clientServer = new ClientServer(context, portNumber);
        clientServer.start();
        addressLog = NetworkUtils.getAddressLog(context, portNumber);
        Log.d(TAG, addressLog);
    }

//    public static String getAddressLog() {
//        Log.d(TAG, addressLog);
//        return addressLog;
//    }
//
//    public static void shutDown() {
//        if (clientServer != null) {
//            clientServer.stop();
//            clientServer = null;
//        }
//    }
//
//    public static void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
//        if (clientServer != null) {
//            clientServer.setCustomDatabaseFiles(customDatabaseFiles);
//        }
//    }

    public static void setBoxStore(BoxStore boxStore) {
        System.out.println("set box store " + boxStore + " " + clientServer);
        if (clientServer != null) {
            clientServer.setBoxStore(boxStore);
        }
    }

    //
//    public static boolean isServerRunning() {
//        return clientServer != null && clientServer.isRunning();
//    }
//
    public static void showDebugDBAddressLogToast(Context context) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.ObjectBoxBrowser");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();
            } catch (Exception ignore) {
            }
        }
    }

}
