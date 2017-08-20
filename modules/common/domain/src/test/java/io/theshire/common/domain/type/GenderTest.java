

package io.theshire.common.domain.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;


public class GenderTest {

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  private static class Person implements Serializable {
    private static final long serialVersionUID = 9188638490382613636L;
    private String name;
    private Gender gender;
  }

  @Test
  public void testGenderSerialization() {
    ObjectMapper mapper = new ObjectMapper();
    Person person = new Person("John Doe", Gender.MALE);
    try {
      String json = mapper.writeValueAsString(person);
      assertTrue(json.contains(Gender.MALE.name()));
    } catch (JsonProcessingException jpex) {
      fail(jpex.getMessage());
    }
  }

  @Test
  public void testGenderDeserialization() {
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"name\":\"Jane Doe\",\"gender\":\"FEMALE\"}";
    try {
      Person person = mapper.readValue(json, Person.class);
      assertEquals(Gender.FEMALE, person.getGender());
    } catch (IOException ioex) {
      fail(ioex.getMessage());
    }
  }

  @Test
  public void testDeserializeNotApplicableGender() {
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"name\":\"Jim Doe\",\"gender\":\"NOT_APPLICABLE\"}";
    try {
      Person person = mapper.readValue(json, Person.class);
      assertEquals(Gender.NOT_APPLICABLE, person.getGender());
    } catch (IOException ioex) {
      fail(ioex.getMessage());
    }
  }

  @Test(expected = InvalidFormatException.class)
  public void testDeserializeInvalidGender() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"name\":\"Claire Doe\",\"gender\":\"ZZZ\"}";
    mapper.readValue(json, Person.class);
  }

}
