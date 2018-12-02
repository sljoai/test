package com.song.cn.grpc.server;

import com.song.cn.grpc.DataRelationGreeterGrpc;
import com.song.cn.grpc.DataRelationReply;
import com.song.cn.grpc.DataRelationRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DataRelationServer {


    private final Logger LOG = LoggerFactory.getLogger(DataRelationServer.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    private int port = 10080;

    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new DataRelationImpl())
                .build()
                .start();
        LOG.info("服务开始运行！");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                LOG.error("JVM被关闭导致 gRPC Server 停止运行！");
                DataRelationServer.this.stop();
            }
        });
    }

    private void stop(){
        if(server!=null){
            server.shutdown();
        }
    }

    private void blockUntilShutdown(){
        if(server!=null){
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                LOG.error("关闭 gRPC Servers 失败！");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final DataRelationServer server = new DataRelationServer();
        server.start();
        server.blockUntilShutdown();
    }

    //实现 或 定义一个服务接口类

    private class DataRelationImpl extends DataRelationGreeterGrpc.DataRelationGreeterImplBase{

        private final Logger LOG = LoggerFactory.getLogger(DataRelationImpl.class);
        @Override
        public void getProgressStatus(DataRelationRequest request, StreamObserver<DataRelationReply> responseObserver) {
            LOG.info("获取程序运行的状态！");
            DataRelationReply reply = DataRelationReply.newBuilder()
                    .setHandledTask(2)
                    .setHandlingTask(4)
                    .setUnhandlingTask(5)
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void setTaskPath(DataRelationRequest request, StreamObserver<DataRelationReply> responseObserver) {
            String scanningPath = request.getScanningPath();
            LOG.info("设置程序扫描的路径：{}",scanningPath);
            DataRelationReply reply = DataRelationReply.newBuilder().build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
