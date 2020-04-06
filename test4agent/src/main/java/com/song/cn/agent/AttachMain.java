package com.song.cn.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class AttachMain {
    public static void main(String[] args) throws Exception {
        List<VirtualMachineDescriptor> listBefore =
                VirtualMachine.list();
        // agentmain()方法所在jar包
        String jar = "D:\\Java\\SRC\\test\\test4agent\\target\\test-agent.jar";
        VirtualMachine vm = null;
        List<VirtualMachineDescriptor> listAfter = null;
        while (true) {
            listAfter = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : listAfter) {
                if (!listBefore.contains(vmd)) { // 发现新的JVM
                    vm = VirtualMachine.attach(vmd); // attach到新JVM
                    vm.loadAgent(jar); // 加载agentmain所在的jar包
                    vm.detach(); // detach
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }
}