# Akka HTTP + Akka Streams + Apache Kafka Integration Example
This application reads from an existing Kafka topic from the earliest
offset and streams the response over HTTP with the help of Server-Sent 
Events.

## How to use
Create a topic and publish some data on it. 
Perform `GET /streaming-kafka/<topicName>` in order to see the data that
is published on the Kafka topic. Note that it is an infinite streaming
response so any newly published data on the Kafka topic will continue 
showing up in the streaming response.

*Note:* I use the same consumer group so if two users hit the same 
endpoint then only one user may see all the data or both users may see
only some part of the data due to how the consumer group binds each
consumer to partitions in the topic.
