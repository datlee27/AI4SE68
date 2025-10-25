# Meal Planner Application

[Slide](https://www.canva.com/design/DAG2tadhBMk/JYUzGvMeSq4QStvYW1Q6GQ/edit?utm_content=DAG2tadhBMk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

## Overview
This is a Spring Boot application for meal planning that helps users manage their daily meals and nutrition.

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Running the Application

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Run Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report

mvn test jacoco:report
# Run single test class
mvn test -Dtest=MealPlannerServiceTest

# Run specific test method
mvn test -Dtest=MealPlannerServiceTest#should_CreateMeal_When_ValidInput

# Run tests in continuous mode (watch)
mvn test -Dtest.continuous=true
```

The coverage reports will be generated in the `target/site/jacoco` directory.
You can open `target/site/jacoco/index.html` in a browser to view the detailed coverage report.

## Limitations and Risks

### Technical Limitations
1. Database Performance
   - Concurrent user access might affect performance
   - Large datasets may require pagination

2. API Rate Limits
   - No rate limiting implemented yet
   - Could be vulnerable to API abuse

### Security Risks
1. Data Security
   - User data must be properly encrypted
   - Session management needs monitoring

2. Input Validation
   - All user inputs must be sanitized
   - SQL injection prevention needed

3. API Security
   - Authentication required for all endpoints
   - CORS policies must be properly configured

### Business Limitations
1. Meal Planning
   - Limited to basic meal tracking
   - No support for complex dietary restrictions

2. User Management
   - Basic user roles only
   - No support for team/group meal planning

## Development Guidelines
1. Always write unit tests for new features
2. Maintain test coverage above 80%
3. Document API changes
4. Follow code style guidelines

## Testing Strategy
1. Unit Tests
   - Controller layer
   - Service layer
   - Repository layer

2. Integration Tests
   - API endpoints
   - Database operations

3. Coverage Requirements & Reports

Coverage can be viewed in `target/site/jacoco/index.html` with following metrics:

a) Overall Coverage Requirements:
    Coverage: Minimum 88%
 
b) Core Features (Must have 100% coverage):
   - com.example.mealplanner.service.MealPlannerService
     * addMeal()
     * updateMeal()
     * deleteMeal()
     * getUserMeals()
   - com.example.mealplanner.service.NutritionService
     * calculateCalories()
     * calculateNutrients()

c) How to Read Coverage Report:
   - Red: No coverage (0%)
   - Yellow: Partial coverage
   - Green: Full coverage (100%)
   
d) Coverage Indicators:
   - Instructions: Java bytecode instructions
   - Branches: if/else and switch conditions
   - Lines: Source code lines
   - Methods: Class methods/functions
   - Complexity: Code path complexity
