package me.caomr.handler;

import me.caomr.entity.ResponseBuilder;
import me.caomr.mainTest;
import me.caomr.utils.FileUtil;
import me.caomr.utils.HttpUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import static me.caomr.entity.ResponseBuilder.*;
import static me.caomr.entity.ResponseBuilder.CONTENT_LENGTH;

public class HttpHandler implements Handler<String> {

    private static AtomicInteger ai = new AtomicInteger(0);
    private static Logger logger = Logger.getLogger(mainTest.class);

    @Override
    public String read(byte[] read) {
        return HttpUtil.findPath(read);
    }

    @Override
    public Boolean write(String path, SocketChannel channel) throws IOException {
        String contentType = FileUtil.contentType(path);
        File file = new File(path);
        byte[] body = null;
        ResponseBuilder builder = new ResponseBuilder();
        builder.addHeader(CONNECTION, KEEP_ALIVE);

        if (file.isFile()) {
            body = FileUtil.file2ByteArray(file, false);
        } else if (file.isDirectory()) {
            body = FileUtil.directoryList(file, false);
            contentType = FileUtil.HTML_CONTENT;
        } else {
            body = "".getBytes();
            contentType = FileUtil.HTML_CONTENT;
            builder.setStatus(ResponseBuilder.NOT_FOUND_404);
        }

        builder.addHeader(CONTENT_TYPE, contentType);
        builder.addHeader(CONTENT_LENGTH, body.length);


        try {
            channel.write(ByteBuffer.wrap(builder.getHeader()));
            channel.write(ByteBuffer.wrap(body));

        } catch (Exception e) {
            channel.close();
            logger.error("something happen", e);
            return false;
        }

        logger.info("count:{}" + ai.addAndGet(1) + ",path:" + path + ",rusult: 200");
        return true;


    }


}
