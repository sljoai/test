package com.song.cn.k8s.controller;

import com.song.cn.k8s.service.DevK8sApiService;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DevK8sApiController {

    DevK8sApiService devK8sApiService;

    @ApiOperation(value = "Init", notes = "Init")
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String initK8s() {
        return devK8sApiService.init();
    }

    @ApiOperation(value = "ListNamespace", notes = "ListNamespace")
    @RequestMapping(value = "/listnamespace", method = RequestMethod.GET)
    public NamespaceList listk8snamespace() {
        return devK8sApiService.listNamespace();
    }

    @ApiOperation(value = "ListNode", notes = "ListNode")
    @RequestMapping(value = "/listnode", method = RequestMethod.GET)
    public NodeList listk8snode() {
        return devK8sApiService.listNode();
    }
}
