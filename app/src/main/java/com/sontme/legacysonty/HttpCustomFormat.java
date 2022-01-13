package com.sontme.legacysonty;

import androidx.annotation.NonNull;

public class HttpCustomFormat {
    public String host;
    public int port;
    public String URL;
    public boolean METHOD_POST;
    public String postData;

    public HttpCustomFormat(final String host, final int port, final String URL,
                            final boolean METHOD_POST,
                            final String postData) {
        this.host = host;
        this.port = port;
        this.URL = URL;
        this.METHOD_POST = METHOD_POST;
        this.postData = postData;
    }

    @NonNull
    @Override
    public String toString() {
        return host + ":" + port + URL;
    }
}
