

package io.theshire.common.utils.jpa.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;


public class LocalDateAttributeConverterTest {

 
  private LocalDateAttributeConverter classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new LocalDateAttributeConverter();
  }

 
  @Test
  public void shouldConvertToDatabase() {
    final LocalDate aLocalDate = LocalDate.now();
    final Date toDb = this.classUnderTest.convertToDatabaseColumn(aLocalDate);
    Assert.assertEquals(Date.valueOf(aLocalDate).getTime(), toDb.getTime());
  }

 
  @Test
  public void shouldConvertFromDatabase() {
    final Date aDate = Date.valueOf(LocalDate.now());
    final LocalDate fromDb = this.classUnderTest.convertToEntityAttribute(aDate);
    Assert.assertEquals(aDate.getTime(), Date.valueOf(fromDb).getTime());
  }

}
