package blaze.examples

import blaze.channel._
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousChannelGroup
import blaze.channel.nio2.NIO2ServerChannelFactory
import blaze.pipeline.LeafBuilder

/**
 * @author Bryce Anderson
 *         Created on 1/5/14
 */
class HttpServer(port: Int) {

  private val f: BufferPipeline = () => LeafBuilder(new ExampleHttpServerStage(10*1024))

  val group = AsynchronousChannelGroup.withFixedThreadPool(10, java.util.concurrent.Executors.defaultThreadFactory())

  private val factory = new NIO2ServerChannelFactory(f)

  def run(): Unit = factory.bind(new InetSocketAddress(port)).run()
}

object HttpServer {
  def main(args: Array[String]): Unit = new HttpServer(8080).run()
}
