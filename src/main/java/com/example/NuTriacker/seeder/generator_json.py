from faker import Faker
from faker_food import FoodProvider
import random
from datetime import datetime, timedelta
import decimal
from decimal import Decimal
import json

# Configuration constants
NUM_USERS = 10
DAILY_LOGS_PER_USER = 10
MEALS_PER_LOG = 10
ITEMS_PER_MEAL = 10

fake = Faker()

# List of common food items with their typical nutritional values
FOOD_ITEMS = [
    "Chicken Breast", "Salmon", "Rice", "Pasta", "Broccoli",
    "Sweet Potato", "Eggs", "Oatmeal", "Greek Yogurt", "Banana",
    "Apple", "Almonds", "Avocado", "Quinoa", "Black Beans",
    "Spinach", "Bread", "Turkey", "Tuna", "Cottage Cheese"
]

fake = Faker()

def random_decimal(min_val, max_val):
    return Decimal(str(random.uniform(min_val, max_val))).quantize(Decimal('0.01'))

def generate_meal_items(start_id, count):
    return [{
        'id': item_id,
        'nutritionixFoodId': random.randint(10000, 99999),
        'foodName': random.choice(FOOD_ITEMS),
        'calories': str(random_decimal(50, 300)),
        'proteins': str(random_decimal(2, 20)),
        'carbs': str(random_decimal(5, 30)),
        'fats': str(random_decimal(1, 15))
    } for item_id in range(start_id, start_id + count)]

def generate_meals(start_id, count):
    meals = []
    for meal_id in range(start_id, start_id + count):
        meal = {
            'id': meal_id,
            'mealName': random.choice(['Breakfast', 'Lunch', 'Dinner', 'Snack']),
            'mealTime': fake.time(),
            'totalCalories': str(random_decimal(200, 800)),
            'totalProteins': str(random_decimal(10, 40)),
            'totalCarbs': str(random_decimal(20, 60)),
            'totalFats': str(random_decimal(5, 25)),
            'mealItems': generate_meal_items(meal_id * ITEMS_PER_MEAL, ITEMS_PER_MEAL)
        }
        meals.append(meal)
    return meals

def generate_daily_logs(start_id, count):
    logs = []
    for log_id in range(start_id, start_id + count):
        log = {
            'id': log_id,
            'date': (datetime.now() - timedelta(days=log_id)).strftime('%Y-%m-%d'),
            'totalDailyCalories': str(random_decimal(1500, 3000)),
            'totalDailyProteins': str(random_decimal(50, 200)),
            'totalDailyCarbs': str(random_decimal(100, 300)),
            'totalDailyFats': str(random_decimal(30, 100)),
            'meals': generate_meals(log_id * MEALS_PER_LOG, MEALS_PER_LOG)
        }
        logs.append(log)
    return logs

def generate_data():
    data = {'users': []}
    
    for user_id in range(1, NUM_USERS + 1):
        user = {
            'id': user_id,
            'firstName': fake.first_name(),
            'lastName': fake.last_name(),
            'email': fake.email(),
            'password': fake.password(length=12),
            'dailyLogs': generate_daily_logs(
                (user_id - 1) * DAILY_LOGS_PER_USER + 1, 
                DAILY_LOGS_PER_USER
            )
        }
        data['users'].append(user)
    
    return data

if __name__ == "__main__":
    data = generate_data()
    with open('seed.json', 'w') as f:
        json.dump(data, f, indent=2)
    print(f"""Data generated successfully in seed.json with:
- {NUM_USERS} users
- {DAILY_LOGS_PER_USER} daily logs per user
- {MEALS_PER_LOG} meals per daily log
- {ITEMS_PER_MEAL} items per meal""")