package org.mwangi.desktop.io;

import okhttp3.Response;

import java.io.IOException;

@FunctionalInterface
public interface RequestExecuter {
    Response execute() throws IOException;
}
