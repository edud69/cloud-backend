

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.subscription.UserSubscription;
import io.theshire.authorization.domain.user.subscription.UserSubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
@Transactional
public class UserSubscriptionRepositoryImpl implements UserSubscriptionRepository {

 
  @Autowired
  private UserSubscriptionJpaRepository userSubcriptionJpaRepository;


  @Override
  public UserSubscription findById(Long id) {
    return userSubcriptionJpaRepository.findOne(id);
  }


  @Override
  public UserSubscription save(UserSubscription domainObject) {
    return userSubcriptionJpaRepository.save(domainObject);
  }


  @Override
  public UserSubscription findByUserAuthentication(UserAuthentication userAuthentication) {
    return userSubcriptionJpaRepository.findByUserId(userAuthentication.getId());
  }

}
