package me.dfdx.metarank.store

import java.nio.ByteBuffer
import cats.effect.IO
import com.github.blemale.scaffeine.{Cache, Scaffeine}
import me.dfdx.metarank.aggregation.Scope
import me.dfdx.metarank.store.HeapBytesStore.{HeapBytesMapStore, HeapBytesValueStore}
import me.dfdx.metarank.store.state.codec.{Codec, KeyCodec}
import me.dfdx.metarank.store.state.{MapState, StateDescriptor, ValueState}

class HeapBytesStore extends Store {
  val byteCache = Scaffeine().build[String, ByteBuffer]()

  override def kv[K, V](desc: StateDescriptor.MapStateDescriptor[K, V], scope: Scope): MapState[K, V] =
    new HeapBytesMapStore(scope, byteCache, desc.kc, desc.vc)

  override def value[T](desc: StateDescriptor.ValueStateDescriptor[T], scope: Scope): ValueState[T] =
    new HeapBytesValueStore[T](scope, byteCache, desc.codec)
}

object HeapBytesStore {
  def apply() = new HeapBytesStore()
  class HeapBytesMapStore[K, V](scope: Scope, cache: Cache[String, ByteBuffer], kc: KeyCodec[K], vc: Codec[V])
      extends MapState[K, V] {
    override def get(key: K): IO[Option[V]] = IO {
      val kstr = s"${Scope.write(scope)}/${kc.write(key)}"
      cache.getIfPresent(kstr).map(bb => vc.read(bb.array()))
    }

    override def put(key: K, value: V): IO[Unit] = IO {
      val kstr = s"${Scope.write(scope)}/${kc.write(key)}"
      cache.put(kstr, ByteBuffer.wrap(vc.write(value)))
    }

    override def delete(key: K): IO[Unit] = IO {
      val kstr = s"${Scope.write(scope)}/${kc.write(key)}"
      cache.invalidate(kstr)
    }
  }

  class HeapBytesValueStore[T](scope: Scope, cache: Cache[String, ByteBuffer], c: Codec[T]) extends ValueState[T] {
    override def get(): IO[Option[T]] = IO {
      cache.getIfPresent(Scope.write(scope)).map(bb => c.read(bb.array()))
    }

    override def put(value: T): IO[Unit] = IO {
      cache.put(Scope.write(scope), ByteBuffer.wrap(c.write(value)))
    }

    override def delete(): IO[Unit] = IO {
      cache.invalidate(Scope.write(scope))
    }
  }
}
