

package io.theshire.common.utils.transport.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


public abstract class TransportMessage implements Serializable {

 
  private static final long serialVersionUID = -7998307176571824321L;


  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }


  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

 
  @JsonSerialize
  @JsonProperty("$bindingClassName")
  public String getBindingClassName() {
    return this.getClass().getSimpleName();
  }
}
