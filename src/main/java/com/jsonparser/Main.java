package com.jsonparser;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Sample JSON strings
        String jsonObjectString = """
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

        String jsonArrayString = """
                [
                    {"id": 1, "name": "Alice"},
                    {"id": 2, "name": "Bob"},
                    {"id": 3, "name": "Charlie"}
                ]
                """;

        System.out.println("1. Parsing JSON to JSONObject:");
        parseToJSONObject(jsonObjectString);

        System.out.println("\n2. Parsing JSON to Map<String, Object>:");
        parseToMap(jsonObjectString);

        System.out.println("\n3. Parsing JSON to JSONArray:");
        parseToJSONArray(jsonArrayString);

        System.out.println("\n4. Parsing JSON to List<Object>:");
        parseToList(jsonArrayString);

        System.out.println("\n5. Accessing nested objects and arrays:");
        accessNestedElements(jsonObjectString);

        System.out.println("\n6. Parsing JSON to a specified class:");
        parseToSpecifiedClass(jsonObjectString);

        System.out.println("\n7. Converting Java object to JSON string:");
        convertObjectToJSON();
    }

    private static void parseToJSONObject(String json) {
        JSONObject jsonObject = JSONParser.parseToJSONObject(json);
        System.out.println("Parsed JSONObject: " + jsonObject);
        System.out.println("Accessing 'name': " + jsonObject.get("name"));
        System.out.println("Accessing 'age': " + jsonObject.get("age"));
    }

    private static void parseToMap(String json) {
        Map<String, Object> map = JSONParser.parseToMap(json);
        System.out.println("Parsed Map: " + map);
        System.out.println("Accessing 'name': " + map.get("name"));
        System.out.println("Accessing 'age': " + map.get("age"));
    }

    private static void parseToJSONArray(String json) {
        JSONArray jsonArray = JSONParser.parseToJSONArray(json);
        System.out.println("Parsed JSONArray: " + jsonArray);
        System.out.println("First element: " + jsonArray.get(0));
        System.out.println("Array size: " + jsonArray.toList().size());
    }

    private static void parseToList(String json) {
        List<Object> list = JSONParser.parseToList(json);
        System.out.println("Parsed List: " + list);
        System.out.println("First element: " + list.get(0));
        System.out.println("List size: " + list.size());
    }

    private static void accessNestedElements(String json) {
        JSONObject jsonObject = JSONParser.parseToJSONObject(json);

        // Accessing nested object
        JSONObject address = (JSONObject) jsonObject.get("address");
        System.out.println("Street: " + address.get("street"));
        System.out.println("Zipcode: " + address.get("zipcode"));

        // Accessing array
        JSONArray grades = (JSONArray) jsonObject.get("grades");
        System.out.println("Grades: " + grades);
        System.out.println("First grade: " + grades.get(0));

        // Demonstrating type handling
        System.out.println("Is student? " + jsonObject.get("isStudent"));
        System.out.println("Age (as Long): " + jsonObject.get("age"));
    }

    private static void parseToSpecifiedClass(String json) {
        Person person = JSONParser.parseToClass(json, Person.class);
        System.out.println("Parsed Person object:");
        System.out.println("Name: " + person.getName());
        System.out.println("Age: " + person.getAge());
        System.out.println("City: " + person.getCity());
        System.out.println("Is student: " + person.isStudent());
        System.out.println("Grades: " + person.getGrades());
        System.out.println("Address: " + person.getAddress());
    }

    private static void convertObjectToJSON() {
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
        System.out.println("Serialized Person object:");
        System.out.println(json);
    }

    //-----------------
    // Inner classes for demonstration
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

        @Override
        public String toString() {
            return "Address{street='" + street + "', zipcode='" + zipcode + "'}";
        }
    }
}