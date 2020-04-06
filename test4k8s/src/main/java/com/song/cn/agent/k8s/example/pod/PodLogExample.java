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
package com.song.cn.agent.k8s.example.pod;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodLogExample {

    private static final Logger logger = LoggerFactory.getLogger(PodLogExample.class);

    public static void main(String[] args) {
        String podName = "nginx";
        String namespace = "default";
        System.setProperty("kubeconfig", "./conf/admin.conf");
        String master = "https://192.168.80.129:6443/";

        System.out.println("Log of pod " + podName + " in " + namespace + " is:");
        System.out.println("----------------------------------------------------------------");

        Config config = new ConfigBuilder().withMasterUrl(master).build();
        try (KubernetesClient client = new DefaultKubernetesClient(config);
             //TODO: 不是很理解这种用法的作用是什么
             LogWatch watch = client.pods()
                     .inNamespace(namespace)
                     .withName(podName)
                     .tailingLines(10)
                     .watchLog(System.out)) {

            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
