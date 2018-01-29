package servises

import org.scalatest._
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}

import scala.concurrent.duration._

//trait LocalFunSuite extends FunSuite

class RpsServicesTest(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll
    //with FeatureSpec
    //with FunSuiteLike//
    //with FunSuite(_system)
    //with LocalFunSuite
    //FlatSpec
    with GivenWhenThen    //with BeforeAndAfterEach
    {
  def this() = this(ActorSystem("AkkaQuickstartSpec"))

  //override def beforeEach() {}
  //override def afterEach() {}

/*  override def afterAll: Unit = {
    shutdown(system)
  }*/

/*  feature("Methods tests") {
    scenario("main") {
      RestClientPost post1 = mock(RestClientPost.class);
      when(post1.postValidateJSON()).thenReturn(true);
      assertEquals(post1.postValidateJSON(), true);
      assertNotEquals(post1.postValidateJSON(), false);
    }
    scenario("args") {
    }
  }*/
}
