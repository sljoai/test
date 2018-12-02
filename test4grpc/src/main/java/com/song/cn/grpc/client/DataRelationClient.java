package com.song.cn.grpc.client;

import com.song.cn.grpc.DataRelationGreeterGrpc;
import com.song.cn.grpc.DataRelationReply;
import com.song.cn.grpc.DataRelationRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DataRelationClient {

    private final Logger LOG = LoggerFactory.getLogger(DataRelationClient.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    private final ManagedChannel channel;

    private final DataRelationGreeterGrpc.DataRelationGreeterBlockingStub blockingStub;

    public DataRelationClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();
        blockingStub = DataRelationGreeterGrpc.newBlockingStub(channel);
    }


    public ProgressStatus getProgressStatus(){
        LOG.info("获取程序运行状态！");
        DataRelationRequest request = DataRelationRequest.newBuilder().build();
        DataRelationReply response = blockingStub.getProgressStatus(request);
        ProgressStatus status = new ProgressStatus();
        status.setHandledTask(response.getHandledTask());
        status.setHandlingTask(response.getHandlingTask());
        status.setUnhandleTask(response.getUnhandlingTask());
        return status;
    }

    public void setTaskPath(String taskPath){
        DataRelationRequest request = DataRelationRequest.newBuilder().setScanningPath(taskPath).build();
        blockingStub.setTaskPath(request);
    }

    public void shutdown(){
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("关闭gRPC信道失败！");
        }
    }

    public static void main(String[] args) {
        DataRelationClient client =  new DataRelationClient("127.0.0.1",10080);

        client.setTaskPath("/opt/data/insight");

        ProgressStatus status = client.getProgressStatus();

        System.out.println(status);

        client.shutdown();


    }
}

class ProgressStatus {

    int unhandleTask = 0;

    int handlingTask = 0;

    int handledTask = 0;

    public void setUnhandleTask(int unhandleTask) {
        this.unhandleTask = unhandleTask;
    }

    public void setHandlingTask(int handlingTask) {
        this.handlingTask = handlingTask;
    }

    public void setHandledTask(int handledTask) {
        this.handledTask = handledTask;
    }

    @Override
    public String toString() {

        return "ProgressStatus{" +
                "unhandleTask=" + unhandleTask +
                ", handlingTask=" + handlingTask +
                ", handledTask=" + handledTask +
                '}';
    }
}
