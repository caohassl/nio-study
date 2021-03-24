**nio-study**

单线程

**netty学习心得**

netty将线程，selector进行了封装

selector重点方法
io.netty.channel.nio.NioEventLoop#processSelectedKeys
io.netty.channel.nio.NioEventLoop#register

多线程方法
io.netty.channel.nio.NioEventLoopGroup#newChild->返回封装的线程池

启动多线程及register select的流程在下列方法
io.netty.bootstrap.AbstractBootstrap#doBind0
