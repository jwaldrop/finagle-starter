package com.yunrang.social.echo

import com.yunrang.social.EchoService
import com.yunrang.social.EchoService.ServiceIface
import org.apache.thrift.protocol.TBinaryProtocol
import com.twitter.finagle.thrift.{ThriftClientFramedCodec, ThriftServerFramedCodec}
import com.twitter.common.zookeeper.{ServerSetImpl, ZooKeeperClient}
import com.twitter.finagle.zookeeper.ZookeeperServerSetCluster
import com.twitter.finagle.builder.{Server, ClientBuilder, ServerBuilder}
import com.twitter.common.quantity.{Time, Amount}
import com.twitter.util.Future
import scala.collection.JavaConversions._
import java.net.{InetAddress, NetworkInterface, InetSocketAddress}

class ClusterHelper {
  val addr = new InetSocketAddress("127.0.0.1", 2181)
  val zookeeperClient: ZooKeeperClient = new ZooKeeperClient(Amount.of(4000, Time.MILLISECONDS), addr)
  val serverSet = new ServerSetImpl(zookeeperClient, "/dong/echo/")
  val cluster = new ZookeeperServerSetCluster(serverSet)
}

object EchoServer {
  def main(args: Array[String]) {
    val processor = new ServiceIface {
      def echo(input: String) = Future.value {
        input.reverse
      }
    }


    val service = new EchoService.Service(processor, new TBinaryProtocol.Factory())
    val address = new InetSocketAddress(InetAddress.getLocalHost.getCanonicalHostName, 1233)
    val server: Server = ServerBuilder()
      .name("echo")
      .bindTo(address)
      .codec(ThriftServerFramedCodec())
      .build(service)

    val helper = new ClusterHelper
    helper.cluster.join(server.localAddress)
  }
}

object EchoClient {
  def main(args: Array[String]) {
    val helper = new ClusterHelper

    val service = ClientBuilder()
      .cluster(helper.cluster)
      .codec(ThriftClientFramedCodec())
      .hostConnectionLimit(10)
      .hostConnectionCoresize(10)
      .build()

    val client = new EchoService.ServiceToClient(service, new TBinaryProtocol.Factory())

    (1 to 100) foreach {
      i =>
        client.echo("hello") onSuccess {
          resp =>
          println("hi " + i)
        } onFailure {
          e =>
            println("failed: " + i)
            e.printStackTrace
            Thread.sleep(1000)
        }
    }
  }

}
