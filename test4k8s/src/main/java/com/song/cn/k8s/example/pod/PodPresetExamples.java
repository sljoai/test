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

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.settings.PodPreset;
import io.fabric8.kubernetes.api.model.settings.PodPresetBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class PodPresetExamples {
    private static final Logger logger = LoggerFactory.getLogger(PodPresetExamples.class);

    public static void main(String args[]) {
//        System.setProperty("kubeconfig", "./conf/admin.conf");
//        System.setProperty("kubernetes.trust.certificates","true");
        String master = "https://192.168.80.129:6443/";

        Config config = new ConfigBuilder()
                .withMasterUrl(master)
                .withClientCertFile("./conf/client.crt")
                .withClientKeyFile("./conf/client.key")
                .withCaCertFile("./conf/ca.crt")
//                .withUsername("fabric8")
                .build();
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            String namespace = "default";
            log("namespace", namespace);

            //为指定的Pod预设一些属性
            PodPreset podPreset = new PodPresetBuilder()
                    .withNewMetadata()
                    //设置名称
                    .withName("allow-database")
                    .endMetadata()
                    .withNewSpec()
                    //设置Selector
                    .withNewSelector()
                    .withMatchLabels(Collections.singletonMap("role", "frontend"))
                    .endSelector()
                    //设置环境变量
                    .withEnv(new EnvVarBuilder().withName("DB_PORT").withValue("6379").build())
                    //设置挂载
                    .withVolumeMounts(new VolumeMountBuilder().withMountPath("/cache").withName("cache-volume").build())
                    //设置卷
                    .withVolumes(new VolumeBuilder().withName("cache-volume").withEmptyDir(new EmptyDirVolumeSourceBuilder().build()).build())
                    .endSpec()
                    .build();

            log("Creating Pod Preset : " + podPreset.getMetadata().getName());
            client.settings().podPresets().inNamespace(namespace).create(podPreset);

            Pod pod = client.pods()
                    .inNamespace(namespace)
                    .load(PodPresetExamples.class.getResourceAsStream("/pod-preset-example.yml"))
                    .get();
            log("Pod created");
            client.pods().inNamespace(namespace).create(pod);
            log(SerializationUtils.dumpAsYaml(pod));


            pod = client.pods().inNamespace(namespace).withName(pod.getMetadata().getName()).get();
            log("Updated pod: ");
            log(SerializationUtils.dumpAsYaml(pod));
        } catch (Exception e) {
            log("Exception occurred: ", e.getMessage());
            e.printStackTrace();
        }
    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }
}
