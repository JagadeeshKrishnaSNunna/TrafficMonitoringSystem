package com.cep.Connector;

import io.nats.client.*;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

public class natsConnector implements SourceFunction<String> {
    private volatile boolean isRunning;

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        isRunning = true;

        if (isRunning) {


            try (Connection natsConn = Nats.connect()) {

                final LinkedBlockingQueue<String> inbox = new LinkedBlockingQueue<>();
                Dispatcher dispatcher=natsConn.createDispatcher(new MessageHandler() {
                    @Override
                    public void onMessage(Message message) throws InterruptedException {
                        final boolean enqueued = inbox.offer(new String(message.getData(), StandardCharsets.UTF_8));
                    }
                });
                dispatcher.subscribe("com.nats");


                while (isRunning) {
                    final String msg = inbox.poll();
                    if (msg != null) {
                        sourceContext.collect(msg);

                    }
                }

                natsConn.close();
            }
        }
    }

    @Override
    public void cancel() {
        isRunning=false;
    }
}