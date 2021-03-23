package me.caomr;

import me.caomr.handler.Handler;
import me.caomr.handler.HttpHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServer {

    private static Logger logger = Logger.getLogger(NioServer.class);

    private static AtomicInteger ai = new AtomicInteger(0);
    private static Handler<String> handler = new HttpHandler();

    private ServerSocketChannel serverChannel;
    private Selector readSelector;
    private Selector writeSelector;
    private Selector selector;

    private ByteBuffer readBuffer;


    public NioServer(int port) throws IOException {
        readSelector = Selector.open();
        writeSelector = Selector.open();
        selector = Selector.open();


        readBuffer = ByteBuffer.allocate(8912);
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    public void listen() throws IOException {
        int select = selector.select();
        if (select > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();

                iterator.remove();
                if (!next.isValid()) {
                    logger.error("this key is valid" + next.toString());
                    continue;
                }
                //sun.nio.ch.ServerSocketChannelImpl cannot be cast to java.nio.channels.SocketChannel
                if (next.isAcceptable()) {
                    SocketChannel channel = serverChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                }
                if (next.isReadable()) {
                    SocketChannel channel = (SocketChannel) next.channel();
                    Buffer clear = readBuffer.clear();
                    int read = channel.read(readBuffer);
                    byte[] content = readBuffer.array();
                    String path = handler.read(content);
                    // 转写
                    SelectionKey writeKey = channel.register(selector, SelectionKey.OP_WRITE);
                    writeKey.attach(path);
                }
                if (next.isWritable()) {
                    SocketChannel channel = (SocketChannel) next.channel();
                    String path = (String) next.attachment();
                    handler.write(path, channel);
                    channel.close();
                }
//                next.cancel();


            }
        }
    }

    /**
     * 打开连接
     */
    public void accept() throws IOException {
        logger.info("start count:{}," + ai.addAndGet(1));
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
//        channel.register(readSelector, SelectionKey.OP_READ);
        channel.register(selector, SelectionKey.OP_READ);
    }


    /**
     * 开始读取
     *
     * @throws IOException
     */
    public void read(Handler<String> handler) throws IOException {
//        int select = readSelector.select();
        int select = selector.select();
        if (select > 0) {
//            Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                SocketChannel channel = (SocketChannel) next.channel();
                Buffer clear = readBuffer.clear();

                int read = channel.read(readBuffer);
                byte[] content = readBuffer.array();
                String path = (String) handler.read(content);
//                next.cancel();
                iterator.remove();
                // 转写
//                SelectionKey writeKey = channel.register(writeSelector, SelectionKey.OP_WRITE);
                SelectionKey writeKey = channel.register(selector, SelectionKey.OP_WRITE);
                writeKey.attach(path);
            }
        }

    }

    public void write(Handler<String> handler) throws IOException {
//        int select = writeSelector.select();
        int select = selector.select();
        if (select > 0) {
//            Iterator<SelectionKey> iterator = writeSelector.selectedKeys().iterator();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();

                SocketChannel channel = (SocketChannel) next.channel();
                String path = (String) next.attachment();

                handler.write(path, channel);


                // 转读
//                next.cancel();
                channel.close();
                iterator.remove();
            }
        }
    }


}
