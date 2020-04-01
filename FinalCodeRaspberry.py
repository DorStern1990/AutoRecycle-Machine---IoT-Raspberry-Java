#Some imports require prior installing

import RPi.GPIO as GPIO

import time
from time import sleep

import serial
from serial import Serial

from picamera import PiCamera

from clarifai.rest import ClarifaiApp

from firebase import firebase

from azure.cognitiveservices.vision.customvision.prediction import CustomVisionPredictionClient

ButtonPin = 12
RaspberryServoPin = 37
Frequency = 50

ENDPOINT =	# Enter "***" (Enter Azure's endpoint)
			# where *** is (for example:) https://your-project-endpoint.cognitiveservices.azure.com/
prediction_key = "8*6*0*6a******1b4a31" # Enter prediction key of last made prediction
										#(from Azure : Predicition->View Prediction Endpoint-> Prediction API)

# initializing predictor (will be used for classify_image)										
predictor = CustomVisionPredictionClient(prediction_key, endpoint=ENDPOINT)

# initializing firebase (connection to your firebase-based application)
firebase = firebase.FirebaseApplication('https://Your-FireBase-Project-RealTime-Database.firebaseio.com/', None)

#System will start identifying material after button is pressed.


#setup camera
camera = PiCamera()
camera.resolution = (648, 484)
camera.rotation = 180

#setup arduino serial
arduino = Serial('/dev/ttyACM1', 9600) ## path might change (path to the connecetion between Arduino and RaspberryPi)

#clean serial
time.sleep(2)
arduino.flushInput()
time.sleep(0.5)

#RaspberryPi's board setup
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)
GPIO.setup(ButtonPin,GPIO.IN, pull_up_down = GPIO.PUD_UP)

#inner stepper setup
GPIO.setup(RaspberryServoPin,GPIO.OUT) # "Clink" Servo will receive commands from RaspberryPi's Pin No. 37 
p = GPIO.PWM(RaspberryServoPin,Frequency)
# #---------------------------start the all operation---------------------------------
try:
    while True:
	# #--------------------update the state in firebase before picture--------------------
        firebase.put('/state',"state","before") # initial state (before succesfull classification)
        firebase.put('/item',"item","no") 		# initial classification
        print("Please press button to continue") # Our print for instructing new users
        channel = GPIO.wait_for_edge(ButtonPin,GPIO.FALLING) # Waiting for button to be pressed
        if channel == ButtonPin:
            print(channel)
            # #------------------------------take picture----------------------------------------
            camera.start_preview()
            time.sleep(1)
            camera.capture('/home/pi/image-recognition.jpg')
            camera.stop_preview()
            # #-----------------------check the prediction in azure------------------------------
            best_pred_label = ''
            best_pred_prob = 0
            with open("/home/pi/image-recognition.jpg", "rb") as image_contents:
				# Classify image using your lastest training iteration
                results = predictor.classify_image("***Your-Iteration-ID***", "***Your-'Published-As'-Iteration***", image_contents.read())
                # Display the results.
                for prediction in results.predictions:
                    if prediction.probability * 100 > best_pred_prob:
                        best_pred_prob = prediction.probability * 100
                        best_pred_label = prediction.tag_name
                    print("\t" + prediction.tag_name +
                          ": {0:.2f}%".format(prediction.probability * 100))
            print(best_pred_label)
            print(best_pred_prob)
			
			# OPTIONAL - defaultly accept predictions with probability over a specific thrashold (i.e. 70)
			# We chose NOT to use this.
			# Labels should be identical to those defined your project's Azure CV's Classifier.
			
			#-----------------------check if prediction more then 70%---------------------------
            # if best_pred_prob > 70:
            #     if best_pred_label == "can":
            #         arduino.write('1')
            #     elif best_pred_label == "cardboard":
            #         arduino.write('2')
            #     elif best_pred_label == "plastic":
            #         arduino.write('3')
            #     elif best_pred_label == "glass":
            #         arduino.write('4')
            #     arduino.write('0')
            # else:
            
			#--------------------------check if metal--------------------------------
            metal = False;
            cardbord = False;
            arduino.write('6') #call arduino function to metal detect
            r = arduino.readline()
            # print(r) for debug
            r = arduino.readline()
            # print(r) for debug
            if r.startswith( 'METAL' ):
                metal= True
            arduino.write('0')
			#-------------------------check if cardboard-----------------------------
            arduino.write('7')
            r = arduino.readline()
            # print(r) for debug
            r = arduino.readline()
            # print(r) for debug
            if r.startswith( 'CARDBORD' ):
                  cardbord = True;
            arduino.write('0')
			#----------------------check if glass or plastic-------------------------
			glass = False
            i = 0
			make_noise = 0
            while i < 3:
				arduino.write('8') # Glass - Sound Test
                servo.start(0)
                servo.ChangeDutyCycle(3)
                sleep(0.5)
                servo.ChangeDutyCycle(7)
                sleep(0.5)
                servo.ChangeDutyCycle(3)
                sleep(0.5)
                servo.stop
				make_noise = make_noise or arduino.readline()
                i = i + 1
			glass = make_noise
			# move according to the item and update the firebase that added item
            if metal == True :
                 firebase.put('/item',"item","add")
				 arduino.write('1')
            elif best_pred_label == "plastic" and glass == False:
                 firebase.put('/item',"item","add")
                 arduino.write('3')
            elif best_pred_label == "glass" and glass == True:
                 firebase.put('/item',"item","add")
                 arduino.write('2')
            elif cardbord == True and best_pred_label == "cardboard" :
                 firebase.put('/item',"item","add")
				 arduino.write('4')
            elif best_pred_label == "cardboard" and best_pred_prob==100:
                 firebase.put('/item',"item","add")
                 arduino.write('4')
            else:
				# we get here if we dont recognize the item and let the user answer in app
				firebase.put('/predict',"label","unknown")
                firebase.put('/state',"state","after")
                label = firebase.get('/predict',"label")
                time.sleep(15) # Here user should pick material via app
                label = firebase.get('/predict',"label")
                state = firebase.get('/state',"state")
                if label == "can":
                    arduino.write('1')
                elif label == "cardboard":
                    arduino.write('4')
                elif label == "plastic":
                    arduino.write('3')
                elif label == "glass":
                    arduino.write('2')
            arduino.write('0')
            # print("end") for debug
except KeyboardInterrupt:
    GPIO.cleanup()
GPIO.cleanup()