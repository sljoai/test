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

package com.song.cn.k8s.example.pod;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentExamples {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentExamples.class);

    public static void main(String[] args) throws InterruptedException {
//        System.setProperty("kubeconfig", "./conf/admin.conf");
        System.setProperty("kubeconfig", "./conf/insight.kubeconfig");
        String master = "https://192.168.80.129:6443/";
        Config config = new ConfigBuilder()
                .withMasterUrl(master)
//                .withClientCertFile("./conf/client.crt")
//                .withClientKeyFile("./conf/client.key")
//                .withCaCertFile("./conf/ca.crt")
                .build();
        KubernetesClient client = new DefaultKubernetesClient(config);

        String namespace = "test";
        String deploymentName = "nginx";
        String serviceAccountName = "fabric8";

        try {
            // 创建一个Namespace
            Namespace ns = new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(namespace)
                    .addToLabels("this", "rocks")
                    .endMetadata()
                    .build();
            Namespace oldName = client
                    .namespaces()
                    .withName(namespace)
                    .get();
            //Namespace不存在创建，存在就更新
            if (oldName == null) {
                log("Created namespace", client
                        .namespaces()
                        .createOrReplace(ns)
                )
                ;
            } else {
                log("Update namespace", client
                        .namespaces()
                        .withName(namespace)
                        .patch(ns)
                )
                ;
            }

            //创建一个ServiceAccount
            ServiceAccount fabric8 = new ServiceAccountBuilder()
                    .withNewMetadata()
                    .withName(serviceAccountName)
                    .endMetadata()
                    .build();
            ServiceAccount oldServiceAccount = client
                    .serviceAccounts()
                    .inNamespace(namespace)
                    .withName(serviceAccountName)
                    .get();
            //不存在创建，存在更新
            if (oldServiceAccount == null) {
                log("Created serviceAccount",
                        client.serviceAccounts()
                                .inNamespace(namespace)
                                .createOrReplace(fabric8)
                );

            } else {

                log("Update serviceAccount",
                        client.serviceAccounts()
                                .inNamespace(namespace)
                                .withName(serviceAccountName)
                                //.patch(fabric8)
                                .createOrReplaceWithNew()
                );
            }

            //创建Deployment
            for (int i = 0; i < 2; i++) {
                System.err.println("Iteration:" + (i + 1));
                //构建Deployment元数据信息
                Deployment deployment = new DeploymentBuilder()
                        .withNewMetadata()
                        .withName(deploymentName)
                        .endMetadata()
                        .withNewSpec()
                        .withReplicas(1)
                        .withNewTemplate()
                        .withNewMetadata()
                        .addToLabels("app", "nginx")
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withName(deploymentName)
                        .withImage("insight.registry.com:5000/default/nginx:1.7.9")
                        .addNewPort()
                        .withContainerPort(80)
                        .endPort()
                        .endContainer()
                        .endSpec()
                        .endTemplate()
                        .withNewSelector()
                        .addToMatchLabels("app", "nginx")
                        .endSelector()
                        .endSpec()
                        .build();

                //判断Deployment是否存在，存在先删除
                /*Deployment olderDeployment = client
                        .apps()
                        .deployments()
                        .withName(deploymentName)
                        .get();
                if(olderDeployment !=null){
                    client.resource(olderDeployment)
                            .deletingExisting()
                    ;
                }else{
                    deployment = client
                            .apps()
                            .deployments()
                            .inNamespace(namespace)
                            .createOrReplace(deployment)
                    ;
                }*/
                Deployment newDeployment = client
                        .apps()
                        .deployments()
                        .inNamespace(namespace)
                        .createOrReplace(deployment);

                //创建Deployment
                log("Created deployment", newDeployment);

                System.err.println("Scaling up:" + newDeployment.getMetadata().getName());

//                deployment.getSpec().setReplicas(2);
                //扩容Deployment到两个
                client.apps()
                        .deployments()
                        .inNamespace(namespace)
                        .withName(deploymentName)
//                        .patch(deployment)
                        //扩展到两个，等待直到创建完成
                        .scale(2, true)
                ;


                log("Created replica sets:",
                        client.apps()
                                .replicaSets()
                                .inNamespace(namespace)
                                .list()
                                .getItems()
                );
                System.err.println("Deleting:" + newDeployment.getMetadata().getName());

                // 删除Deployment
                int count = 0;
                while (true) {
                    //删除Deployment
                    Deployment oldDeployment = client.apps()
                            .deployments()
                            .inNamespace(namespace)
                            .withName(deploymentName)
                            .get();
                    if (oldDeployment == null || count > 10) {
                        if (count != 0) {
                            log("Deployment has been deleted!");
                        }
                        break;
                    }
                    //删除
                    if (count == 0) {
                        client.apps()
                                .deployments()
                                .inNamespace(namespace)
                                .withName(deploymentName)
                                .delete()
                        ;
                        //这个接口有问题
/*                        client.resource(deployment)
                                .withGracePeriod(10L)
                                .delete()
                        ;*/
                    }
                    count++;
                    Thread.sleep(1000);
                }
            }
            log("Done.");

        } catch (Exception e) {
            log("error: ", e.getMessage());
            e.printStackTrace();
        } finally {
            client.namespaces()
                    .withName(namespace)
                    .withGracePeriod(0)
                    .delete()
            ;
            client.close();
        }
    }


    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }
}
