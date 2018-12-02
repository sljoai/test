/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.examples.helloworld.future;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloWorldServer;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class HelloWorldClientFuture2 {
  private static final Logger logger = Logger.getLogger(HelloWorldClientFuture2.class.getName());

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterFutureStub futureStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public HelloWorldClientFuture2(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true));
  }
  /** Construct client for accessing RouteGuide server using the existing channel. */
  HelloWorldClientFuture2(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    futureStub = GreeterGrpc.newFutureStub(channel);
  }

  /** Say hello to server. */
  public void futureGreet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    try {
      com.google.common.util.concurrent.ListenableFuture<HelloReply>
              listenableFuture = futureStub.sayHello(request);
      listenableFuture.addListener(()->
      {
          try {
            HelloReply response = listenableFuture.get();
            logger.info("Greeting: " + response.getMessage());
          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
        }, Executors.newFixedThreadPool(1));
      TimeUnit.SECONDS.sleep(3);
      Runtime.getRuntime().exit(0);
    } catch (Exception e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e);
      return;
    }
  }
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    HelloWorldClientFuture2 client = new HelloWorldClientFuture2("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String user = "world";
      if (args.length > 0) {
        user = args[0]; /* Use the arg as the name to greet if provided */
      }
      client.futureGreet(user);
    } finally {
      client.shutdown();
    }
  }
}
