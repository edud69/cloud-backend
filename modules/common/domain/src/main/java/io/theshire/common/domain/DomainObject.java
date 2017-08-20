

package io.theshire.common.domain;

import lombok.Getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class DomainObject implements Serializable {

 
  private static final long serialVersionUID = -3741998725852830137L;

 
  @Id

 
  @Getter
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, true);
  }


  @Override
  public boolean equals(final Object object) {
    return EqualsBuilder.reflectionEquals(this, object);
  }
}