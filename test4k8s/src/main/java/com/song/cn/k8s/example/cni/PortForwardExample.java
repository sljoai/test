/**
 * Copyright (C) 2015 Red Hat, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.song.cn.k8s.example.cni;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PortForwardExample {
    private static final Logger logger = LoggerFactory.getLogger(PortForwardExample.class);

    public static void main(String args[]) {
        //设置配置文件路径
        System.setProperty("kubeconfig", "./conf/admin.conf");
        //API Server地址
        String master = "https://192.168.80.129:6443";

        // 创建配置对象config
        Config config = new ConfigBuilder().withMasterUrl(master).build();

        OkHttpClient okHttpClient = null;
        //创建访问客户端
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            String namespace = "default";
            log("namespace", namespace);
            //从本地读取yaml配置文件，填充Pod对象
            Pod pod = client.pods()
                    .inNamespace(namespace)
                    .load(PortForwardExample.class.getResourceAsStream("/portforward-example-pod.yml"))
                    .get();
            String podName = pod.getMetadata().getName();

            int count = 0;
            while (true) {
                //在创建新的Pod之前，先检查Pod是否存在
                Pod oldPod = client.pods()
                        .inNamespace(namespace)
                        .withName(podName)
                        .get();
                if (oldPod == null || count > 10) {
                    if (count != 0) {
                        log("pod has been deleted!");
                    }
                    break;
                }
                //删除
                if (count == 0) {
                    client.pods().inNamespace(namespace)
                            .withName(podName)
                            .delete();
                }
                count++;
            }

            //在指定的命名空间中创建Pod对象
            client.pods().inNamespace(namespace).create(pod);
            log("Pod created");

            //创建成功以后获取容器Port
            int containerPort = pod.getSpec()
                    .getContainers()
                    .get(0)
                    .getPorts()
                    .get(0)
                    .getContainerPort();

            //等待Pod创建成功
            client.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .waitUntilReady(10, TimeUnit.SECONDS);

            boolean isReady = false;
            while (!isReady) {
                isReady = client.pods()
                        .inNamespace(namespace)
                        .withName(podName)
                        .isReady();
            }
            log("Pod is ready");

            //将容器端口绑定到本地上
            LocalPortForward portForward = client.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .portForward(containerPort, 8080);
            log("Port forwarded for 60 seconds at http://127.0.0.1:" + portForward.getLocalPort());

            log("Checking forwarded port:-");

            //创建OkHttpClient
            okHttpClient = new OkHttpClient();
            //创建请求
            Request request = new Request.Builder()
                    .get()
                    .url("http://127.0.0.1:" + portForward.getLocalPort())
                    .build();
            //创建Call
            Call call = okHttpClient
                    .newCall(request);
            //发送请求，并返回响应
            Response response = call.execute();

            log(response.body().string());
            //response.close();

            Thread.sleep(100 * 1000);
        } catch (Exception e) {
            log("Exception occurred: ", e.getMessage());
        } finally {
            /*if (okHttpClient != null) {
                try {
                    //关闭调度器服务
                    okHttpClient.dispatcher().executorService().shutdown();
                    //关闭网络连接池
                    okHttpClient.connectionPool().evictAll();
                    //关闭缓存
                    okHttpClient.cache().close();
                } catch (Exception e) {
                    log(e.getMessage());
                }

            }*/
        }
    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }
}
