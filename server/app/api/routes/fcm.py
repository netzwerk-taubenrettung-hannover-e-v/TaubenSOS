import firebase_admin, os
from firebase_admin import credentials, messaging

cert = credentials.Certificate(os.getenv("HOME") + "/.firebase_credentials")
app = firebase_admin.initialize_app(cert)

def build_android_config(title, body, icon=None):
    notification = messaging.AndroidNotification(title=title, body=body, icon=icon)
    return messaging.AndroidConfig(notification=notification)

def send_to_token(token, title, body, icon=None):
    message = messaging.Message(token=token, android=build_android_config(title, body, icon=icon))
    response = messaging.send(message)
    print(f"Sent '{title}' notification to token {token}: {response}")

def send_to_topic(topic, title, body, icon=None):
    message = messaging.Message(topic=topic, android=build_android_config(title, body, icon=icon))
    response = messaging.send(message)
    print(f"Sent '{title}' notification to topic {topic}: {response}")

def subscribe_to_topic(topic, tokens):
    messaging.subscribe_to_topic(tokens, topic)

def unsubscribe_from_topic(topic, tokens):
    messaging.subscribe_to_topic(tokens, topic)