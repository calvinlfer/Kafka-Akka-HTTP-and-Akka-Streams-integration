package com.experiments.integration
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkasse.ServerSentEvent
import de.heikoseeberger.akkasse.EventStreamMarshalling._

import scala.concurrent.duration._
import scala.language.postfixOps

trait Endpoints {
  implicit val actorSystem: ActorSystem
  implicit val streamMaterializer: ActorMaterializer
  val allRoutes = streamingKafka
  val log: LoggingAdapter
  val consumerSettings: ConsumerSettings[Array[Byte], String]

  /**
    * Kafka Topic ~> HTTP SSE Streaming Response
    * @return
    */
  def streamingKafka =
    path("streaming-kafka" / Remaining) { topicName =>
      get {
        val source = Consumer.plainSource(consumerSettings, Subscriptions.topics(topicName))
          .map(consumerRecord => consumerRecord.value())
          .map(s => ServerSentEvent(s, "exampleEvent"))
          .keepAlive(20 seconds, () => ServerSentEvent.Heartbeat)

        complete(source)
      }
    }
}
