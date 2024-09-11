# Custom JSON Parser

## Description
This project implements a custom JSON parser in Java. It provides functionality to parse JSON strings into Java objects, convert Java objects to JSON strings, and perform various JSON-related operations.

## Features
- Parse JSON strings to Java objects (JSONObject and JSONArray)
- Convert JSON to Map<String, Object> and List<Object>
- Parse JSON to specified Java classes
- Serialize Java objects to JSON strings
- Handle nested structures
- Detect and throw exceptions for invalid JSON

## Main Components

### JSONParser
The core class responsible for parsing JSON strings. It includes methods for:
- Parsing to JSONObject and JSONArray
- Converting to Map and List
- Parsing to specified classes

### JSONObject
Represents a JSON object. It implements the Map interface for easy manipulation.

### JSONArray
Represents a JSON array. It implements the List interface for convenient access.

### JSONException
Custom exception class for JSON-related errors.

### JSONSerializer
Handles the serialization of Java objects to JSON strings.

## Usage Examples

1. Parsing JSON to JSONObject:
   `JSONObject obj = JSONParser.parseToJSONObject(jsonString);`

2. Parsing JSON to Map:
   `Map<String, Object> map = JSONParser.parseToMap(jsonString);`

3. Parsing JSON to a specific class:
   `MyClass instance = JSONParser.parseToClass(jsonString, MyClass.class);`

4. Serializing an object to JSON:
   `String json = JSONSerializer.serialize(myObject);`

More examples - in `Main.java`. 

<details>
<summary>Click to see Main.java output</summary>

```
1. Parsing JSON to JSONObject:
Parsed JSONObject: {"address":{"zipcode":"10001","street":"123 Main St"},"city":"New York","name":"John Doe","grades":[85,90,78],"age":30,"isStudent":false}
Accessing 'name': John Doe
Accessing 'age': 30

2. Parsing JSON to Map<String, Object>:
Parsed Map: {name=John Doe, address={"zipcode":"10001","street":"123 Main St"}, grades=[85,90,78], city=New York, age=30, isStudent=false}
Accessing 'name': John Doe
Accessing 'age': 30

3. Parsing JSON to JSONArray:
Parsed JSONArray: [{"name":"Alice","id":1},{"name":"Bob","id":2},{"name":"Charlie","id":3}]
First element: {"name":"Alice","id":1}
Array size: 3

4. Parsing JSON to List<Object>:
Parsed List: [{"name":"Alice","id":1}, {"name":"Bob","id":2}, {"name":"Charlie","id":3}]
First element: {"name":"Alice","id":1}
List size: 3

5. Accessing nested objects and arrays:
Street: 123 Main St
Zipcode: 10001
Grades: [85,90,78]
First grade: 85
Is student? false
Age (as Long): 30

6. Parsing JSON to a specified class:
Parsed Person object:
Name: John Doe
Age: 30
City: New York
Is student: false
Grades: [85, 90, 78]
Address: Address{street='123 Main St', zipcode='10001'}

7. Converting Java object to JSON string:
Serialized Person object:
{"name":"Jane Doe","age":28,"city":"San Francisco","isStudent":true,"grades":[95,88,92],"address":{"street":"456 Elm St","zipcode":"94102"}}

Process finished with exit code 0
```

</details>

## Project Structure
```
src/
├── main/
│   └── java/
│       └── com/
│           └── jsonparser/
│               ├── JSONParser.java
│               ├── JSONObject.java
│               ├── JSONArray.java
│               ├── JSONException.java
│               └── JSONSerializer.java
└── test/
└── java/
└── com/
└── jsonparser/
└── JSONParserTest.java
```

## Testing
The project includes a comprehensive test suite (JSONParserTest.java) covering various scenarios including:
- Parsing valid JSON objects and arrays
- Handling nested structures
- Detecting invalid JSON (e.g., trailing commas)
- Serialization of Java objects to JSON

Testing results:

![img.png](img.png)

## Limitations
- Does not handle all possible JSON escape sequences
- May have limitations with very large JSON strings due to recursive parsing