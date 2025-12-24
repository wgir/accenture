You are a senior developer expert in java 21 with srping-boot 3.4 build an api related in Requirements.md  and design arquitecture based on Rules.md


-----------------------------------------------------------------------------------------------------------------

You are an expert in Java 21, Spring Boot 3, JUnit 5, and Mockito.
Generate a complete and high-quality unit test for the following Spring Boot controller.

Requirements:
1. Use JUnit 5.
2. Use Mockito for mocking services.
3. Use WebTestClient and @WebFluxTest for controller testing.
4. Include tests for:
    - Success cases
    - Validation errors
    - Not found
    - Exception handling
6. Mock all service calls that the controller depends on.
7. Validate:
    - HTTP status codes
    - JSON response structure
    - Content type
8. Include Arrange–Act–Assert sections for clarity.
9. Add clear method names following 'shouldXyzWhenAbc' convention.
10. Use ObjectMapper for JSON serialization.
11. Include imports.

Here is the controller:

ProductController

Generate only the test class. Do not rewrite the controller.


--------------------------------------------------------------------------------