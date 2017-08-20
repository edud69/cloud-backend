

package io.theshire.account.domain.impl;

import io.theshire.account.domain.Account;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AccountRepositoryImplTest {

 
  @Mock
  private AccountJpaRepository accountJpaRepository;

 
  @InjectMocks
  private final AccountRepositoryImpl classUnderTest = new AccountRepositoryImpl();

 
  @Test
  public void findByIdTest() {
    final Long existingId = 1L;
    final Account expectedAccount = new Account();

    Mockito.when(accountJpaRepository.findOne(existingId)).thenReturn(expectedAccount);

    final Account actualAccount = classUnderTest.findById(existingId);
    Assert.assertEquals(expectedAccount, actualAccount);
  }

 
  @Test
  public void saveTest() {
    final Account expectedAccount = new Account();

    Mockito.when(accountJpaRepository.save(expectedAccount)).thenReturn(expectedAccount);

    final Account actualAccount = classUnderTest.save(expectedAccount);
    Assert.assertEquals(expectedAccount, actualAccount);
  }

}
