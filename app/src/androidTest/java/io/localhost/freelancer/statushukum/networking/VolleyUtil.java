package io.localhost.freelancer.statushukum.networking;

import android.app.Application;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleyUtil {
    private static VolleyUtil instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleyUtil(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyUtil getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyUtil(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory(ctx.getApplicationContext())));
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

class ClientSSLSocketFactory extends SSLCertificateSocketFactory {
    private static SSLContext sslContext;

    /**
     * @param handshakeTimeoutMillis
     * @deprecated Use {@link #getDefault(int)} instead.
     */
    public ClientSSLSocketFactory(int handshakeTimeoutMillis) {
        super(handshakeTimeoutMillis);
    }

    public static SSLSocketFactory getSocketFactory(Context context){
        try
        {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { tm }, null);

            SSLSocketFactory ssf = ClientSSLSocketFactory.getDefault(10000, new SSLSessionCache(context));

            return ssf;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}