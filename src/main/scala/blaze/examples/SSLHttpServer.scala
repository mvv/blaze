package blaze.examples

import blaze.channel._
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousChannelGroup
import blaze.pipeline.stages.SSLStage
import javax.net.ssl.{KeyManagerFactory, SSLContext}

import java.security.KeyStore
import blaze.util.BogusKeystore
import blaze.channel.nio2.NIO2ServerChannelFactory
import blaze.pipeline.TrunkBuilder

/**
 * @author Bryce Anderson
 *         Created on 1/5/14
 */
class SSLHttpServer(port: Int) {

  val sslContext: SSLContext = {
    val ksStream = BogusKeystore.asInputStream()
    val ks = KeyStore.getInstance("JKS")
    ks.load(ksStream, BogusKeystore.getKeyStorePassword)

    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    kmf.init(ks, BogusKeystore.getCertificatePassword)

    val context = SSLContext.getInstance("SSL")

    context.init(kmf.getKeyManagers(), null, null)
    context
  }


  private val f: BufferPipeline = { () =>
    val eng = sslContext.createSSLEngine()
    eng.setUseClientMode(false)

    TrunkBuilder(new SSLStage(eng)).cap(new ExampleHttpServerStage(10*1024))
  }

  val group = AsynchronousChannelGroup.withFixedThreadPool(10, java.util.concurrent.Executors.defaultThreadFactory())

  private val factory = new NIO2ServerChannelFactory(f)

  def run(): Unit = factory.bind(new InetSocketAddress(port)).run()
}

object SSLHttpServer {
  def main(args: Array[String]): Unit = new SSLHttpServer(4430).run()
}
