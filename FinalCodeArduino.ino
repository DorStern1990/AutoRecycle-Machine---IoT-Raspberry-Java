#include <Stepper.h>
#include <Servo.h>

#define STEPS 200
#define pulsePin A4
#define capPin A5
#define ledLight 6
#define pResistor A0
#define servoPin 9
#define soundDigitalPin 2

#define photoval 240
#define metalval -26500


void setup() {
	Serial.begin(9600);

// setup servo (to maintain hatch closed)
	Servo myservo;
	myservo.attach(servoPin);
	myservo.write(180);

// setup photo resistor
	pinMode(ledLight, OUTPUT);
	pinMode(pResistor,INPUT);

//setup metal detect 
	pinMode(pulsePin, OUTPUT); 
	digitalWrite(pulsePin, LOW);
	pinMode(capPin, INPUT);

//reset metal detect
	pinMode(capPin,OUTPUT);
	digitalWrite(capPin,LOW);
	delayMicroseconds(20);
	pinMode(capPin,INPUT);
	applyPulses();

//setup stepper motor 
	Stepper stepper(STEPS, 5, 4); 
	stepper.setSpeed(350);

//soundsensor
	pinMode(soundDigitalPin,INPUT);

}

int current_state 	// Possible values:
					// 0 - Nothing
					// [1-4] - Move item's box
					// [5-8] - Perform material tests

void loop() {
// read from raspberry to update the current_state 
	if(Serial.available()){
		delay(100);
		current_state = Serial.read()-'0';
	}
// Move item above the correct bin
	if(current_state == 0){}	// Do Nothing  
	if(current_state == 1){		// Aluminum
		move_first_bin();
	}
	if(current_state == 2){		// Cardboard
		move_second_bin();
	}
	if(current_state == 3){		// Plastic
		move_third_bin();
	}
	if(current_state == 4){		// Glass
		move_forth_bin();
	}

//Metal detector test
	if(current_state == 6){
		int val = 0;
		for(int i = 0; i < 256; i++){ 
			pinMode(capPin,OUTPUT);
			digitalWrite(capPin,LOW);
			delayMicroseconds(20);
			pinMode(capPin,INPUT);
			applyPulses();
			val += analogRead(capPin); //takes 13x8=104 microseconds
			delayMicroseconds(104);
		}
		Serial.println (val);
		if(val <= metalval) {
			Serial.println("METAL");
		}else{
			Serial.println("NOT");
		}
	}
//END Metal detector test

//Photoresistor - Cardboard test (opacity)
	if(current_state == 7){
		digitalWrite(ledLight,HIGH);
		int MinVal = 400;
		delay(1000);
		for(int i = 0; i < 4096; i++){
			int valLight = analogRead(pResistor);
			if(MinVal > valLight){
				MinVal = valLight;
			}
		}
		digitalWrite(ledLight,LOW);
		Serial.println(MinVal);
		if(MinVal < photoval){
			Serial.println("CARDBORD");
		}else{
			Serial.println("NOTC");
		}
	}
//END Photoresistor - Cardboard test

//Servo and Sound Sensor - Clink test
	int SensorDataDigital;
	if(current_state == 8){
		SensorDataDigital=digitalRead(soundDigitalPin);
		Serial.println(counter_for_sound_output);
	}
//END Servo and Sound Sensor - Clink test

	delay(500);
}


// Helping Functions

void applyPulses()
{
	for (int i=0;i<3;i++) 
    {
      digitalWrite(pulsePin,HIGH); //take 3.5 uS
      delayMicroseconds(3);
      digitalWrite(pulsePin,LOW);  //take 3.5 uS
      delayMicroseconds(3);
    }
}

void move_first_bin()
{
      myservo.write(15);
      delay(5000);   
      myservo.write(180);
}

void move_second_bin()
{
      delay(4000);
      stepper.step(5250);
      delay(4000);
      myservo.write(15);
      delay(5000);   
      myservo.write(180);
      delay(5000);
      stepper.step(-5250);
}

void move_third_bin()
{
      delay(4000);
      stepper.step(10800);
      delay(4000);
      myservo.write(15);
      delay(5000);   
      myservo.write(180);
      delay(5000);
      stepper.step(-10800);
}

void move_forth_bin()
{
      delay(4000);
      stepper.step(17000);
      delay(4000);
      myservo.write(15);
      delay(5000);   
      myservo.write(180);
      delay(5000);
      stepper.step(-17000);
}
