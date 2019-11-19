package io.localhost.freelancer.statushukum.networking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
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

/**
 * This <TestOAuth_001> project in package <id.ac.ub.filkom.se.kcv.testoauth_001.model.networking> created by :
 * Name         : syafiq
 * Date / Time  : 11 November 2016, 5:03 AM.
 * Email        : syafiq.rezpector@gmail.com
 * Github       : syafiqq
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkRequestQueue
{
    @SuppressLint("StaticFieldLeak") private static NetworkRequestQueue instance;
    @SuppressLint("StaticFieldLeak") private static Context             context;

    private RequestQueue requestQueue;
    private ImageLoader  imageLoader;

    private NetworkRequestQueue(Context context)
    {
        NetworkRequestQueue.context = context;
        this.requestQueue = getRequestQueue();

        this.imageLoader = new ImageLoader(this.requestQueue,
                new ImageLoader.ImageCache()
                {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url)
                    {
                        return this.cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap)
                    {
                        this.cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkRequestQueue getInstance(Context context)
    {
        if(NetworkRequestQueue.instance == null)
        {
            NetworkRequestQueue.instance = new NetworkRequestQueue(context);
        }
        return NetworkRequestQueue.instance;
    }

    public RequestQueue getRequestQueue()
    {
        if(this.requestQueue == null)
        {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            this.requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory(context.getApplicationContext())));
        }
        return this.requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        this.getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader()
    {
        return this.imageLoader;
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