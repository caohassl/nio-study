package me.caomr;

import me.caomr.handler.Handler;
import me.caomr.handler.HttpHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class mainTest {

    private static Logger logger = Logger.getLogger(mainTest.class);


    public static void main(String[] args) throws IOException {


        int port = 8000;
        logger.info("开始监听*:" + port + "...");
        NioServer nioServer = new NioServer(port);

        while (true) {
            nioServer.listen();
        }




    }


}
