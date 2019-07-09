package com.song.cn.basic.thread.actor;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

public class Test4Akka {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("actor-demo-java");
        //ActorRef hello = system.actorOf(Props.create(Hello.class));
        ActorRef hello = system.actorOf(Props.create(Hello2.class));
        hello.tell("Bob", ActorRef.noSender());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { /* ignore */ }
        system.shutdown();
    }

    private static class Hello extends UntypedActor {

        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                System.out.println("Hello " + message);
            }
        }
    }

    private static class Hello2 extends AbstractActor {

        public Hello2() {
            receive(ReceiveBuilder.
                    match(String.class, s -> {
                        System.out.println("Hello " + s);
                    }).
                    build());
        }
    }
}
