package com.amitshekhar.server;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import io.objectbox.BoxStore;

public class ClientServer implements Runnable {

    private static final String TAG = "ClientServer";

    private final int mPort;

    private boolean mIsRunning;

    private ServerSocket mServerSocket;

    private final RequestHandler mRequestHandler;

    public ClientServer(Context context, int port) {
        mRequestHandler = new RequestHandler(context);
        mPort = port;
    }

    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                try {
                    mRequestHandler.handle(socket);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        } catch (Exception ignore) {

            ignore.printStackTrace();
        }
    }

    public void setBoxStore(BoxStore boxStore) {
        mRequestHandler.setBoxStore(boxStore);
    }
}
