

package io.theshire.common.utils.jpa.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;


public class LocalDateTimeAttributeConverterTest {

 
  private LocalDateTimeAttributeConverter classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new LocalDateTimeAttributeConverter();
  }

 
  @Test
  public void shouldConvertToDatabase() {
    final LocalDateTime aLocalDateTime = LocalDateTime.now();
    final Timestamp toDb = this.classUnderTest.convertToDatabaseColumn(aLocalDateTime);
    Assert.assertEquals(Timestamp.valueOf(aLocalDateTime), toDb);
  }

 
  @Test
  public void shouldConvertFromDatabase() {
    final Timestamp aTimestamp = new Timestamp(1L);
    final LocalDateTime fromDb = this.classUnderTest.convertToEntityAttribute(aTimestamp);
    Assert.assertEquals(aTimestamp, Timestamp.valueOf(fromDb));
  }

}
