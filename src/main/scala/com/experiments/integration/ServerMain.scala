package com.experiments.integration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object ServerMain extends App with Endpoints {
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

  val bindingFuture = Http().bindAndHandle(allRoutes, "localhost", 9000)
  bindingFuture
    .map(_.localAddress)
    .map(addr => s"Bound to $addr")
    .foreach(log.info)
}
