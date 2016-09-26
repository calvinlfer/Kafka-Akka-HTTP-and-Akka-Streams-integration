package com.experiments.integration.consumer.example

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.experiments.integration.Endpoints
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

/**
  * Created by cfernandes on 2016-09-26.
  */
object KafkaConsumer extends App with Endpoints {
  implicit val actorSystem = ActorSystem("test-actor-system")
  implicit val streamMaterializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher
  val log = actorSystem.log
  val consumerSettings = ConsumerSettings(
    system = actorSystem,
    keyDeserializer = new ByteArrayDeserializer,
    valueDeserializer = new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("calvin-consumer-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val control = Consumer.plainSource(consumerSettings, Subscriptions.topics("jokes"))
    .map(consumerRecord => consumerRecord.value())
    .map(str => {
      log.error(str)
      str
    })
    .to(Sink.ignore).run()
}
