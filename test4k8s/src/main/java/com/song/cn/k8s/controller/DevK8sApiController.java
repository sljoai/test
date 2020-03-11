package com.song.cn.k8s.controller;

import com.song.cn.k8s.service.DevK8sApiService;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DevK8sApiController {

    @Autowired
    DevK8sApiService devK8sApiService;

    @ApiOperation(value = "Init", notes = "Init")
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String initK8s() {
        return devK8sApiService.init();
    }

    @ApiOperation(value = "ListNamespace", notes = "ListNamespace")
    @RequestMapping(value = "/listNamespace", method = RequestMethod.GET)
    public NamespaceList listk8snamespace() {
        return devK8sApiService.listNamespace();
    }

    @ApiOperation(value = "ListNode", notes = "ListNode")
    @RequestMapping(value = "/listNode", method = RequestMethod.GET)
    public NodeList listk8snode() {
        return devK8sApiService.listNode();
    }

    @ApiOperation(value = "CreatePod", notes = "CreatePod")
    @RequestMapping(value = "/createPod", method = RequestMethod.POST)
    public Pod createPod(@RequestParam(value = "Namespace") String namespace,
                         @RequestParam(value = "PodName") String podName,
                         @RequestParam(value = "ContainerName") String containerName,
                         @RequestParam(value = "ImageName") String imageName) {
        return devK8sApiService.createPod(namespace, podName, containerName, imageName);
    }

    @ApiOperation(value = "DeletePod", notes = "DeletePod")
    @RequestMapping(value = "/deletePod", method = RequestMethod.DELETE)
    public Pod deletePod(@RequestParam(value = "Namespace") String namespace,
                         @RequestParam(value = "PodName") String podName) {
        return devK8sApiService.deletePod(namespace, podName);
    }

}
