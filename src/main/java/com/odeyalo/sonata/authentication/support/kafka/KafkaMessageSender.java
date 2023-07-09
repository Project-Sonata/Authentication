package com.odeyalo.sonata.authentication.support.kafka;

public interface KafkaMessageSender {

    void send(String topic, Object body);

}
