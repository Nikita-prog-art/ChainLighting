#include <SoftwareSerial.h>

SoftwareSerial blueSerial(11, 12);

void makeLight(int pin){
  for (int i = 0; i < 255; ++i){
    analogWrite(pin, i);
    delay(6);
  }
}

void makeDark(int pin){
  for (int i = 255; i >= 0; --i){
    analogWrite(pin, i);
    delay(6);
  }
}

#define PR 3
#define PG 5
#define PB 6

void setup() {
  // put your setup code here, to run once:
  pinMode(3, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  Serial.begin(9600);
  blueSerial.begin(9600);
  //analogWrite(3, 168);
  //pinMode(6, OUTPUT);
  //pinMode(10, OUTPUT);
}

int PinChr(char c){
  if (c == 'R')
    return PR;
  if (c == 'G')
    return PG;
  if (c == 'B')
    return PB;
}

void loop(){
  char pr[100]/* = "1R1G1BDRDGDB"*/;
  int i = 0 /*12*/;
  char c = '_' /*E*/;
  while (c != 'E'){
    if (blueSerial.available()){
      c = blueSerial.read();
      Serial.write(c);
      if (c == '0' || c == '1' || c == 'L' || c == 'D' || c == 'R' || c == 'G' || c == 'B')
        pr[i++] = c;
      if (i == 100)
        i = 0;
    }
  }
  Serial.write("prog-");
  while (c != 'S'){
    if (blueSerial.available()){
      c = blueSerial.read();
      Serial.write(c);
      if (c == 'S')
        break;
    }
    for (int j = 0; j < i - 1; j += 2){
      while (j < i - 1 && !(pr[j] == '0' || pr[j] == '1' || c == 'L' || c == 'D' )){
        j++;
      }
      if (j == i - 1)
        break;
      int pin = PinChr(pr[j + 1]);
      Serial.write(pr[j]);
      Serial.write('-');
      Serial.write(pin);
      Serial.write('-');
      if (pr[j] == '0')
        analogWrite(pin, 0);
      if (pr[j] == '1')
        analogWrite(pin, 255);
      if (pr[j] == 'L')
        makeLight(pin);
      if (pr[j] == 'D')
        makeDark(pin);
    }
  }
  /*
  make_light(5);
  make_dark(3);
  make_light(6);
  make_dark(5);
  make_light(3);
  make_dark(6);
  */
  /*
  analogWrite(6, 0);    
  //analogWrite(10, 255);  
  delay(500);
  analogWrite(10, 0);  
  analogWrite(3, 168);
  delay(500);
  analogWrite(3, 0);
  analogWrite(5, 168);
  delay(500);
  analogWrite(5, 0);    
  analogWrite(6, 168);    
  delay(500);
  */
}
