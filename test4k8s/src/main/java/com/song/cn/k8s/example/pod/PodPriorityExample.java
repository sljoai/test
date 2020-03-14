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

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.scheduling.PriorityClass;
import io.fabric8.kubernetes.api.model.scheduling.PriorityClassBuilder;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class PodPriorityExample {
    private static final Logger logger = LoggerFactory.getLogger(PodDisruptionBudgetExample.class);

    public static void main(String args[]) throws InterruptedException {
        String master = "https://192.168.99.100:8443/";
        if (args.length == 1) {
            master = args[0];
        }

        log("Using master with url ", master);
        Config config = new ConfigBuilder().withMasterUrl(master).build();
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            PriorityClass priorityClass = new PriorityClassBuilder()
                    .withNewMetadata()
                    .withName("high-priority")
                    .endMetadata()
                    .withValue(new Integer(100000))
                    .withGlobalDefault(false)
                    .withDescription("This priority class should be used for XYZ service pods only.")
                    .build();
            client.scheduling().priorityClass().create(priorityClass);

            client.pods().inNamespace("default").create(new PodBuilder()
                    .withNewMetadata()
                    .withName("nginx")
                    .withLabels(Collections.singletonMap("env", "test"))
                    .endMetadata()
                    .withNewSpec()
                    .addToContainers(new ContainerBuilder()
                            .withName("nginx")
                            .withImage("nginx")
                            .withImagePullPolicy("IfNotPresent")
                            .build())
                    .withPriorityClassName("high-priority")
                    .endSpec()
                    .build()
            );
        } catch (KubernetesClientException e) {
            e.printStackTrace();
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