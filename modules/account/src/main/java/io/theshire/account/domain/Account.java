

package io.theshire.account.domain;

import io.theshire.common.domain.DomainObject;
import io.theshire.common.domain.type.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(name = "account_unique_user_id", columnNames = { "user_id" }) })
@Document(indexName = "account")




@Getter



@ToString



@NoArgsConstructor



@AllArgsConstructor
public class Account extends DomainObject {

 
  private static final long serialVersionUID = 4065061504085900744L;

 
  @NotNull
  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

 
  @NotNull
  @Size(max = 64)
  @Column(name = "first_name", nullable = false)
  private String firstName;

 
  @NotNull
  @Size(max = 64)
  @Column(name = "last_name", nullable = false)
  private String lastName;

 
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender = Gender.NOT_KNOWN;

 
  @Column(name = "birthday")
  private LocalDate birthday;

 
  @Column(name = "avatar_url")
  private String avatarUrl;

 
  public Account update(String firstName, String lastName, Gender gender, LocalDate birthday,
      final String avatarUrl) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.birthday = birthday;
    this.avatarUrl = avatarUrl;
    return this;
  }

}
