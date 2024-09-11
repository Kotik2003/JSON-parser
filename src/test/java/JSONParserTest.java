import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import com.jsonparser.*;

class JSONParserTest {

    private String jsonObjectString;
    private String jsonArrayString;

    @BeforeEach
    void setUp() {
        jsonObjectString = """
                {
                    "name": "John Doe",
                    "age": 30,
                    "city": "New York",
                    "isStudent": false,
                    "grades": [85, 90, 78],
                    "address": {
                        "street": "123 Main St",
                        "zipcode": "10001"
                    }
                }
                """;

        jsonArrayString = """
                [
                    {"id": 1, "name": "Alice"},
                    {"id": 2, "name": "Bob"},
                    {"id": 3, "name": "Charlie"}
                ]
                """;
    }

    @Test
    void testParseToJSONObject() {
        JSONObject jsonObject = JSONParser.parseToJSONObject(jsonObjectString);
        assertNotNull(jsonObject);
        assertEquals("John Doe", jsonObject.get("name"));
        assertEquals(30, jsonObject.get("age"));
        assertEquals("New York", jsonObject.get("city"));
        assertFalse((Boolean) jsonObject.get("isStudent"));

        JSONArray grades = (JSONArray) jsonObject.get("grades");
        assertNotNull(grades);
        assertEquals(3, grades.toList().size());
        assertEquals(85, grades.get(0));

        JSONObject address = (JSONObject) jsonObject.get("address");
        assertNotNull(address);
        assertEquals("123 Main St", address.get("street"));
        assertEquals("10001", address.get("zipcode"));
    }

    @Test
    void testParseToMap() {
        Map<String, Object> map = JSONParser.parseToMap(jsonObjectString);
        assertNotNull(map);
        assertEquals("John Doe", map.get("name"));
        assertEquals(30, map.get("age"));
        assertEquals("New York", map.get("city"));
        assertFalse((Boolean) map.get("isStudent"));

        List<Object> grades = (List<Object>) map.get("grades");
        assertNotNull(grades);
        assertEquals(3, grades.size());
        assertEquals(85, grades.get(0));

        Map<String, Object> address = (Map<String, Object>) map.get("address");
        assertNotNull(address);
        assertEquals("123 Main St", address.get("street"));
        assertEquals("10001", address.get("zipcode"));
    }

    @Test
    void testParseToJSONArray() {
        JSONArray jsonArray = JSONParser.parseToJSONArray(jsonArrayString);
        assertNotNull(jsonArray);
        assertEquals(3, jsonArray.toList().size());

        JSONObject firstObject = (JSONObject) jsonArray.get(0);
        assertEquals(1, firstObject.get("id"));
        assertEquals("Alice", firstObject.get("name"));
    }

    @Test
    void testParseToList() {
        List<Object> list = JSONParser.parseToList(jsonArrayString);
        assertNotNull(list);
        assertEquals(3, list.size());

        Map<String, Object> firstObject = (Map<String, Object>) list.get(0);
        assertEquals(1, firstObject.get("id"));
        assertEquals("Alice", firstObject.get("name"));
    }

    @Test
    void testParseToClass() {
        Person person = JSONParser.parseToClass(jsonObjectString, Person.class);
        assertNotNull(person);
        assertEquals("John Doe", person.getName());
        assertEquals(30, person.getAge());
        assertEquals("New York", person.getCity());
        assertFalse(person.isStudent());

        List<Integer> grades = person.getGrades();
        assertNotNull(grades);
        assertEquals(3, grades.size());
        assertEquals(85, grades.get(0));

        Address address = person.getAddress();
        assertNotNull(address);
        assertEquals("123 Main St", address.getStreet());
        assertEquals("10001", address.getZipcode());
    }

    @Test
    void testSerialize() {
        Person person = new Person();
        person.setName("Jane Doe");
        person.setAge(28);
        person.setCity("San Francisco");
        person.setStudent(true);
        person.setGrades(List.of(95, 88, 92));

        Address address = new Address();
        address.setStreet("456 Elm St");
        address.setZipcode("94102");
        person.setAddress(address);

        String json = JSONSerializer.serialize(person);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Jane Doe\""));
        assertTrue(json.contains("\"age\":28"));
        assertTrue(json.contains("\"city\":\"San Francisco\""));
        assertTrue(json.contains("\"isStudent\":true"));
        assertTrue(json.contains("\"grades\":[95,88,92]"));
        assertTrue(json.contains("\"address\":{\"street\":\"456 Elm St\",\"zipcode\":\"94102\"}"));
    }

    @Test
    void testParseInvalidJSON() {
        String[] invalidJSONs = {
                "{\"name\": \"John\", \"age\": 30,}",
                "{\"name\": \"John\", \"age\": }",
                "{\"name\": \"John\", : 30}",
                "{\"name\": \"John\", \"age\": 30, \"city\":}",
                "{\"\": \"John\", \"age\": 30}",
                "{\"name\": \"John\", \"age\": 30, \"address\": {\"street\": \"Main St\",}}",
                "[1, 2, 3,]",
                "[1, 2, 3, ]",
                "[1, 2, 3, ,]",
                "{\"name\": \"John\", \"grades\": [1, 2, 3,]}",
        };

        for (String invalidJSON : invalidJSONs) {
            assertThrows(JSONException.class, () -> JSONParser.parseToJSONObject(invalidJSON),
                    "Failed to throw JSONException for invalid JSON: " + invalidJSON);
        }
    }

    @Test
    void testParseEmptyObject() {
        String emptyObject = "{}";
        JSONObject jsonObject = JSONParser.parseToJSONObject(emptyObject);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.toMap().isEmpty());
    }

    @Test
    void testParseEmptyArray() {
        String emptyArray = "[]";
        JSONArray jsonArray = JSONParser.parseToJSONArray(emptyArray);
        assertNotNull(jsonArray);
        assertTrue(jsonArray.toList().isEmpty());
    }

    @Test
    void testParseNestedStructures() {
        String nestedJSON = """
                {
                    "name": "John",
                    "details": {
                        "age": 30,
                        "hobbies": ["reading", "swimming"],
                        "address": {
                            "city": "New York",
                            "zip": "10001"
                        }
                    }
                }
                """;
        JSONObject jsonObject = JSONParser.parseToJSONObject(nestedJSON);
        assertNotNull(jsonObject);
        assertEquals("John", jsonObject.get("name"));

        JSONObject details = (JSONObject) jsonObject.get("details");
        assertNotNull(details);
        assertEquals(30, details.get("age"));

        JSONArray hobbies = (JSONArray) details.get("hobbies");
        assertNotNull(hobbies);
        assertEquals(2, hobbies.toList().size());
        assertEquals("reading", hobbies.get(0));

        JSONObject address = (JSONObject) details.get("address");
        assertNotNull(address);
        assertEquals("New York", address.get("city"));
        assertEquals("10001", address.get("zip"));
    }

    // Inner classes for testing
    public static class Person {
        private String name;
        private int age;
        private String city;
        private boolean isStudent;
        private List<Integer> grades;
        private Address address;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public boolean isStudent() {
            return isStudent;
        }

        public void setStudent(boolean student) {
            isStudent = student;
        }

        public List<Integer> getGrades() {
            return grades;
        }

        public void setGrades(List<Integer> grades) {
            this.grades = grades;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address {
        private String street;
        private String zipcode;

        // Getters and setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }
    }
}