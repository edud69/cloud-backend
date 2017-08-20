

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.user.subscription.UserSubscriptionInvitation;
import io.theshire.authorization.domain.user.subscription.UserSubscriptionInvitationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
@Transactional
class UserSubscriptionInvitationRepositoryImpl implements UserSubscriptionInvitationRepository {

 
  @Autowired
  private UserSubscriptionInvitationJpaRepository userSubscriptionInvitationJpaRepository;


  @Override
  public UserSubscriptionInvitation findById(Long id) {
    return userSubscriptionInvitationJpaRepository.findOne(id);
  }


  @Override
  public UserSubscriptionInvitation save(UserSubscriptionInvitation domainObject) {
    return userSubscriptionInvitationJpaRepository.save(domainObject);
  }

}
