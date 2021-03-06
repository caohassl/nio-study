package me.caomr.handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Handler<T> {

    public T read(byte[] read);

    public Boolean write(T t, SocketChannel channel) throws IOException;
}
