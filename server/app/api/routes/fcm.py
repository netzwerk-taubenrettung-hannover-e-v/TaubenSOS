import firebase_admin, os
from firebase_admin import credentials, messaging

cert = credentials.Certificate(os.getenv("HOME") + "/.firebase_credentials")
app = firebase_admin.initialize_app(cert)

def build_android_config(title, body, icon=None):
    if isinstance(title, str) and isinstance(body, str):
        notification = messaging.AndroidNotification(
            title=title,
            body=body,
            icon=icon)
    elif isinstance(title, str) and isinstance(body, list):
        notification = messaging.AndroidNotification(
            title=title,
            body_loc_key=body[0],
            body_loc_args=body[1:],
            icon=icon)
    elif isinstance(title, list) and isinstance(body, str):
        notification = messaging.AndroidNotification(
            title_loc_key=title[0],
            title_loc_args=title[1:],
            body=body,
            icon=icon)
    elif isinstance(title, list) and isinstance(body, list):
        notification = messaging.AndroidNotification(
            title_loc_key=title[0],
            title_loc_args=title[1:],
            body_loc_key=body[0],
            body_loc_args=body[1:],
            icon=icon)
    return messaging.AndroidConfig(notification=notification)

def send_to_token(token, title, body, icon=None):
    message = messaging.Message(token=token, android=build_android_config(title, body, icon=icon))
    try:
        response = messaging.send(message)
    except messaging.ApiCallError as e:
        print(f"Caught ApiCallError: {e}")
    else:
        print(f"Successfully sent notification: {response}")

def send_to_topic(topic, title, body, icon=None):
    message = messaging.Message(topic=topic, android=build_android_config(title, body, icon=icon))
    try:
        response = messaging.send(message)
    except messaging.ApiCallError as e:
        print(f"Caught ApiCallError: {e}")
    else:
        print(f"Successfully sent notification: {response}")

def subscribe_to_topic(topic, token):
    if isinstance(token, str):
        token = [token]
    try:
        messaging.subscribe_to_topic(token, topic)
    except messaging.ApiCallError as e:
        print(f"Caught ApiCallError: {e}")

def unsubscribe_from_topic(topic, token):
    if isinstance(token, str):
        token = [token]
    try:
        messaging.unsubscribe_from_topic(token, topic)
    except messaging.ApiCallError as e:
        print(f"Caught ApiCallError: {e}")