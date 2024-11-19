from faker import Faker
import random
from datetime import datetime, timedelta
import decimal
from decimal import Decimal
import csv

# Configuration constants
NUM_USERS = 10
DAILY_LOGS_PER_USER = 10
MEALS_PER_LOG = 10
ITEMS_PER_MEAL = 10

FOOD_ITEMS = [
    "Chicken Breast", "Salmon", "Rice", "Pasta", "Broccoli",
    "Sweet Potato", "Eggs", "Oatmeal", "Greek Yogurt", "Banana",
    "Apple", "Almonds", "Avocado", "Quinoa", "Black Beans",
    "Spinach", "Bread", "Turkey", "Tuna", "Cottage Cheese"
]

fake = Faker()

def random_decimal(min_val, max_val):
    return Decimal(str(random.uniform(min_val, max_val))).quantize(Decimal('0.01'))

def generate_csv_data():
    with open('seed.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([
            'user_id', 'first_name', 'last_name', 'email', 'password',
            'log_id', 'log_date', 'daily_calories', 'daily_proteins', 'daily_carbs', 'daily_fats',
            'meal_id', 'meal_name', 'meal_time', 'meal_calories', 'meal_proteins', 'meal_carbs', 'meal_fats',
            'item_id', 'nutritionix_food_id', 'food_name', 'item_calories', 'item_proteins', 'item_carbs', 'item_fats'
        ])

        for user_id in range(1, NUM_USERS + 1):
            user_data = {
                'user_id': user_id,
                'first_name': fake.first_name(),
                'last_name': fake.last_name(),
                'email': fake.email(),
                'password': fake.password(length=12)
            }

            for log_day in range(DAILY_LOGS_PER_USER):
                log_id = (user_id - 1) * DAILY_LOGS_PER_USER + log_day + 1
                log_data = {
                    'log_id': log_id,
                    'log_date': (datetime.now() - timedelta(days=log_day)).strftime('%Y-%m-%d'),
                    'daily_calories': str(random_decimal(1500, 3000)),
                    'daily_proteins': str(random_decimal(50, 200)),
                    'daily_carbs': str(random_decimal(100, 300)),
                    'daily_fats': str(random_decimal(30, 100))
                }

                for meal_num in range(MEALS_PER_LOG):
                    meal_id = log_id * MEALS_PER_LOG + meal_num
                    meal_data = {
                        'meal_id': meal_id,
                        'meal_name': random.choice(['Breakfast', 'Lunch', 'Dinner', 'Snack']),
                        'meal_time': fake.time(),
                        'meal_calories': str(random_decimal(200, 800)),
                        'meal_proteins': str(random_decimal(10, 40)),
                        'meal_carbs': str(random_decimal(20, 60)),
                        'meal_fats': str(random_decimal(5, 25))
                    }

                    for item_num in range(ITEMS_PER_MEAL):
                        item_id = meal_id * ITEMS_PER_MEAL + item_num
                        item_data = {
                            'item_id': item_id,
                            'nutritionix_food_id': random.randint(10000, 99999),
                            'food_name': random.choice(FOOD_ITEMS),
                            'item_calories': str(random_decimal(50, 300)),
                            'item_proteins': str(random_decimal(2, 20)),
                            'item_carbs': str(random_decimal(5, 30)),
                            'item_fats': str(random_decimal(1, 15))
                        }

                        writer.writerow([
                            user_data['user_id'], user_data['first_name'], user_data['last_name'],
                            user_data['email'], user_data['password'],
                            log_data['log_id'], log_data['log_date'], log_data['daily_calories'],
                            log_data['daily_proteins'], log_data['daily_carbs'], log_data['daily_fats'],
                            meal_data['meal_id'], meal_data['meal_name'], meal_data['meal_time'],
                            meal_data['meal_calories'], meal_data['meal_proteins'], meal_data['meal_carbs'],
                            meal_data['meal_fats'],
                            item_data['item_id'], item_data['nutritionix_food_id'], item_data['food_name'],
                            item_data['item_calories'], item_data['item_proteins'], item_data['item_carbs'],
                            item_data['item_fats']
                        ])

if __name__ == "__main__":
    generate_csv_data()
    print(f"""Data generated successfully in seed.csv with:
- {NUM_USERS} users
- {DAILY_LOGS_PER_USER} daily logs per user
- {MEALS_PER_LOG} meals per daily log
- {ITEMS_PER_MEAL} items per meal""")