

package io.theshire.common.utils.web.url;

import org.junit.Assert;
import org.junit.Test;


public class UrlUtilsTest {

 
  @Test
  public void shouldFindParameterInQueryUrlCaseOne() {
    final String rawQuery = "token=1234askasjd712&param2=28sdcxn8987&param3=129dsafm";
    final String tokenValue = UrlUtils.extractParameterValueFromQueryUri(rawQuery, "token");
    Assert.assertEquals("1234askasjd712", tokenValue);
  }

 
  @Test
  public void shouldFindParameterInQueryUrlCaseTwo() {
    final String rawQuery = "token=1234askasjd712";
    final String tokenValue = UrlUtils.extractParameterValueFromQueryUri(rawQuery, "token");
    Assert.assertEquals("1234askasjd712", tokenValue);
  }

 
  @Test
  public void shouldFindParameterInQueryUrlCaseThree() {
    final String rawQuery = "param2=28sdcxn8987&token=1234askasjd712&param3=129dsafm";
    final String tokenValue = UrlUtils.extractParameterValueFromQueryUri(rawQuery, "token");
    Assert.assertEquals("1234askasjd712", tokenValue);
  }

 
  @Test
  public void shouldNotFindParameterInQueryUrl() {
    final String rawQuery = "param1=1234askasjd712&param2=28sdcxn8987&param3=129dsafm";
    final String tokenValue = UrlUtils.extractParameterValueFromQueryUri(rawQuery, "token");
    Assert.assertEquals(null, tokenValue);
  }

}
