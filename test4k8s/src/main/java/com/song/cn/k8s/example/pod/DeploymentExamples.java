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
        String master = "https://192.168.80.129:6443/";
        Config config = new ConfigBuilder()
                .withMasterUrl(master)
                .withClientCertFile("./conf/client.crt")
                .withClientKeyFile("./conf/client.key")
                .withCaCertFile("./conf/ca.crt")
                .build();
        KubernetesClient client = new DefaultKubernetesClient(config);

        String namespace = "test";
        String deploymentName = "nginx";

        try {
            // 创建一个Namespace
            Namespace ns = new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(namespace)
                    .addToLabels("this", "rocks")
                    .endMetadata()
                    .build();
            log("Created namespace", client.namespaces().createOrReplace(ns));

            //创建一个ServiceAccount
            ServiceAccount fabric8 = new ServiceAccountBuilder()
                    .withNewMetadata()
                    .withName("fabric8")
                    .endMetadata()
                    .build();
            log("Created serviceAccount",
                    client.serviceAccounts()
                            .inNamespace(namespace)
                            .createOrReplace(fabric8)
            );

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


                //创建Deployment
                deployment = client.apps().deployments().inNamespace(namespace).create(deployment);
                log("Created deployment", deployment);

                System.err.println("Scaling up:" + deployment.getMetadata().getName());

                //扩容Deployment到两个
                client.apps()
                        .deployments()
                        .inNamespace(namespace)
                        .withName(deploymentName)
                        .scale(2, true)
                ;


                log("Created replica sets:",
                        client.apps()
                                .replicaSets()
                                .inNamespace(namespace)
                                .list()
                                .getItems()
                );
                System.err.println("Deleting:" + deployment.getMetadata().getName());
                client.resource(deployment).delete();
            }
            log("Done.");

        } finally {
            client.namespaces().withName(namespace).delete();
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
