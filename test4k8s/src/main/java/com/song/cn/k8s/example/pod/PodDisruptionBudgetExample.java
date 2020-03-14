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

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.policy.PodDisruptionBudget;
import io.fabric8.kubernetes.api.model.policy.PodDisruptionBudgetBuilder;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;


/**
 * PDB能够限制同时中断的pod的数量,以保证集群的高可用性
 */
public class PodDisruptionBudgetExample {
    private static final Logger logger = LoggerFactory.getLogger(PodDisruptionBudgetExample.class);

    public static void main(String args[]) throws InterruptedException {
        System.setProperty("kubeconfig", "./conf/admin.conf");
        String master = "https://192.168.80.129:6443/";
        if (args.length == 1) {
            master = args[0];
        }

        log("Using master with url ", master);
        Config config = new ConfigBuilder().withMasterUrl(master).build();
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            final String namespace = "default";

            PodDisruptionBudget podDisruptionBudget = new PodDisruptionBudgetBuilder()
                    .withNewMetadata()
                    .withName("zk-pkb")
                    .endMetadata()
                    .withNewSpec()
                    .withMaxUnavailable(new IntOrString("1%"))
                    .withNewSelector()
                    .withMatchLabels(Collections.singletonMap("app", "zookeeper"))
                    .endSelector()
                    .endSpec()
                    .build();

            log("Current namespace is", namespace);
            client.policy().podDisruptionBudget().inNamespace(namespace).create(podDisruptionBudget);

        } catch (KubernetesClientException e) {
            log("Could not create resource", e.getMessage());
        }
    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }
}