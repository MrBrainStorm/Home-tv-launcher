#!/usr/bin/env python3
"""Handle the Xiaomi microphone key that firmware consumes before accessibility."""
import subprocess
import time
import signal
import logging

TV = '192.168.92.135:5555'
reader = None
stopping = False
logging.basicConfig(level=logging.INFO, format='%(asctime)s %(message)s')

def stop(*_):
    global stopping
    stopping = True
    if reader is not None and reader.poll() is None:
        reader.terminate()

signal.signal(signal.SIGTERM, stop)
signal.signal(signal.SIGINT, stop)
last_press = 0.0
while not stopping:
    try:
        subprocess.run(['adb', 'connect', TV], capture_output=True, timeout=10)
        reader = subprocess.Popen(['adb', '-s', TV, 'shell', 'getevent', '-lt'],
                                  stdout=subprocess.PIPE, stderr=subprocess.DEVNULL,
                                  text=True, bufsize=1)
        for line in reader.stdout:
            if stopping:
                break
            if 'KEY_VOICECOMMAND' not in line or ' UP' not in line:
                continue
            now = time.monotonic()
            if now - last_press < 1.0:
                continue
            last_press = now
            result = subprocess.run(['adb', '-s', TV, 'shell', 'am', 'start',
                                     '-a', 'android.intent.action.MAIN',
                                     '-c', 'android.intent.category.LEANBACK_LAUNCHER',
                                     '-p', 'com.google.android.youtube.tv'],
                                    capture_output=True, text=True, timeout=15)
            logging.info('Microphone -> YouTube: %s',
                         'requested' if result.returncode == 0 else 'ADB unavailable')
        reader.wait(timeout=3)
    except (OSError, subprocess.SubprocessError) as error:
        logging.warning('Reconnecting to TV: %s', type(error).__name__)
    finally:
        if reader is not None and reader.poll() is None:
            reader.terminate()
            try:
                reader.wait(timeout=3)
            except subprocess.TimeoutExpired:
                reader.kill()
        reader = None
    if not stopping:
        time.sleep(3)
