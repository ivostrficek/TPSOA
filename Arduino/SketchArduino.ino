#include <SPI.h>
#include <Ethernet.h>
//#include <Wire.h> 
//#include "RTClib.h"
//RTC_DS1307 RTC;
// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};
IPAddress ip(192, 168, 10, 50);

// Initialize the Ethernet server library
// with the IP address and port you want to use
// (port 80 is default for HTTP):
EthernetServer server(80);
EthernetClient client;
int sensorPin = A5;    // Pin del sensor de temperatur
int lampPin = 7;
int coolerPin = 8;
float temperatura = 0.0;
String sTemperatura;
int TempMax = 29;
int TempMin = 27;
int HoraInicio = 10;
int HoraFin = 22;
int forzar = 0;

void setup() {
  //Wire.begin(); // Inicia el puerto I2C
  //RTC.begin(); // Inicia la comunicación con el RTC
  Serial.begin(9600);
  while (!Serial) {
    ;
  }
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip);
  server.begin();
  Serial.print("server is at ");
  Serial.println(Ethernet.localIP());
  pinMode(lampPin, OUTPUT);  
  pinMode(coolerPin, OUTPUT);
  digitalWrite(lampPin, LOW);
  digitalWrite(coolerPin, LOW);
}

void ConectarCliente(){
  //Serial.println("Entre a conectar cliente");
  client = server.available();
  //Serial.println("Cliente conectado");
}

float CalcularTemperatura(){
  int lectura = analogRead(sensorPin);
  float voltage = lectura*(5.0/1023);
  float temp = voltage * 100;
  return temp;
}

void Responder(String respuesta){
    // enviar http responce header
    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: applicationo/json;charset=utf-8");
    client.println("Connection: close");  // the connection will be closed after completion of the response
    client.println("Server: Arduino");
    client.println("");
    client.println(respuesta);
}

void loop() {
  //DateTime now = RTC.now(); // Obtiene la fecha y hora del RTC
  
  temperatura = CalcularTemperatura();
  sTemperatura = temperatura;
  // listen for incoming clients
  ConectarCliente();
  if (client) {
    Serial.println("new client");
    boolean currentLineIsBlank = true;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        String str = "";
        int cuenta = 0;
        while(cuenta < 5){
          while(c != '\n'){
            c = client.read();
            str += c;
          }  
          cuenta ++;
        }
        int ComandoCorrecto = 0;  
        //Serial.println('\n' + str + '\n');
        int index = str.indexOf("/");
        String sub = str.substring(index);
        Serial.println("\nTexto recibido reducido:");
        Serial.println(sub);
        
        if(sub.startsWith("/temperatura")){
          Responder("La temperatura es " + sTemperatura);
          Serial.println("ENTRE A TEMPERATURA");
          ComandoCorrecto = 1;
        }
        if(sub.startsWith("/vent_on")){
          Responder("Se encendieron las luces");
          digitalWrite(lampPin, HIGH);
          //forzar = 1;
          Serial.println("ENTRE A VENT_ON");
          ComandoCorrecto = 1;
        }
        if(sub.startsWith("/vent_off") ){
          Responder("Se apagaron las luces");
          digitalWrite(lampPin, LOW);
          //forzar = 0;
          Serial.println("ENTRE A VENT_OFF");
          ComandoCorrecto = 1;
        }
        if(sub.startsWith("/rango")){
          int idxParentesis = str.indexOf("(");
          int idxComa = str.indexOf(",");
          int idxParentesis2 = str.indexOf(")");
          String Max = str.substring(idxParentesis + 1,idxComa);
          String Min = str.substring(idxComa + 1, idxParentesis2);
          Serial.println("Max: " + Max + " Min: " + Min);
          ComandoCorrecto = 1;
          TempMax = Max.toInt();
          TempMin = Min.toInt();
          Serial.println("Temperatura seteada. Max: " + Max + " Min: " + Min);
           Responder("Temperatura seteada. Max: " + Max + " Min: " + Min);
        }
        if(!ComandoCorrecto){
          Responder("Mandaste cualquiera");
        }
      }
      delay(1);
      client.stop();
    }
    delay(1);
    client.stop();
    Serial.println("cliente desconectado");
  }
  if(temperatura >= TempMax && forzar == 0){
    digitalWrite(coolerPin, HIGH);   
  }else if(temperatura <= TempMin && forzar == 0){
    digitalWrite(coolerPin, LOW);
  }
  //if(now.hour() >= HoraInicio && now.hour() <=  HoraFin){
  //  digitalWrite(lampPin, HIGH);
  //}else{
  //  digitalWrite(lampPin, LOW);
  //}
}
