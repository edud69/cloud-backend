

package io.theshire.common.service;

import java.util.function.Function;


public class OutPort<I, O> {

 
  private final Function<I, O> convert;

 
  private O output;

 
  public static <I> OutPort<I, Object> ignore() {
    return new OutPort<I, Object>(received -> new Object());
  }

 
  public static <I, O> OutPort<I, O> create(final Function<I, O> convert) {
    return new OutPort<I, O>(convert);
  }

 
  private OutPort(final Function<I, O> convert) {
    this.convert = convert;
  }

 
  public final void receive(final I received) {
    this.output = this.convert.apply(received);
  }

 
  public final O get() {
    return this.output;
  }

}
