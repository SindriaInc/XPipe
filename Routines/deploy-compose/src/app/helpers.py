import sys
import yaml
import datetime

# Global app config
APP_CONFIG = '/var/www/app/config/deployments.yaml'

# Get app config
def app():
  return load(APP_CONFIG)

# Get current datetime
def now():
  current = datetime.datetime.now()
  now = current.strftime("%Y-%m-%d %H:%M:%S")
  return now

# Load yaml file
def load(file):
  with open(file, 'r') as f:
    try:
      data = yaml.load(f, Loader=yaml.FullLoader)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)
  return data

# Save yaml file
def save(file, data):
  with open(file, 'w') as f:
    try:
      yaml.dump(data, f)
    except yaml.YAMLError as e:
      print(e)
      sys.exit(1)
  return True