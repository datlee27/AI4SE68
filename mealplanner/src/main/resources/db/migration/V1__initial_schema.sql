-- Create users table with a name that doesn't conflict with SQL keywords
CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create nutrition_goals table
CREATE TABLE nutrition_goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    calories_target INT NOT NULL,
    protein_target INT NOT NULL,
    carbs_target INT NOT NULL,
    fats_target INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- Create food table
CREATE TABLE food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    calories DOUBLE NOT NULL,
    protein DOUBLE NOT NULL,
    carbs DOUBLE NOT NULL,
    fats DOUBLE NOT NULL
);

-- Create ingredients table for food
CREATE TABLE food_ingredients (
    food_id BIGINT,
    ingredient VARCHAR(255),
    PRIMARY KEY (food_id, ingredient),
    FOREIGN KEY (food_id) REFERENCES food(id)
);

-- Create meals table
CREATE TABLE meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT,
    meal_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- Create meal_foods junction table
CREATE TABLE meal_foods (
    meal_id BIGINT,
    food_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (meal_id, food_id),
    FOREIGN KEY (meal_id) REFERENCES meal(id),
    FOREIGN KEY (food_id) REFERENCES food(id)
);

-- Create dietary_preferences table
CREATE TABLE dietary_preferences (
    user_id BIGINT,
    preference_type VARCHAR(50),
    preference_value VARCHAR(255),
    PRIMARY KEY (user_id, preference_type),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);