# Lights-Out-Ardunio
An arduino light puzzle where you need to figure out how to turn all of the lights off.

/*
  Light Puzzle: XOR Toggle Logic (4 Buttons x 4 LEDs)

  This puzzle uses XOR (toggle) logic for a solvable 4x4 matrix.
  Pressing a button XORs the current board state with the button's pattern mask.

  Pins are mapped as: D13=LED 4 (MSB), D10=LED 1 (LSB).
*/

// --- Button Pins ---
const int buttonPin1 = 2;   // Button 1
const int buttonPin2 = 3;   // Button 2
const int buttonPin3 = 4;   // Button 3
const int buttonPin4 = 5;   // Button 4 (NEW)

// --- LED PINS (Mapping to Bits) ---
const int ledPin1 = 10;     // LED 1 (Bit 0)
const int ledPin2 = 11;     // LED 2 (Bit 1)
const int ledPin3 = 12;     // LED 3 (Bit 2)
const int ledPin4 = 13;     // LED 4 (Bit 3)

// The win condition is when all LEDs are OFF (boardState = 0)
const int WIN_STATE = 0; 

// --- Pattern Masks (GUARANTEED SOLVABLE SET - CORRECTED VALUES) ---
// This set creates a solvable, invertible matrix.
const int MASK_B1 = 14;  // Binary 1110 (Toggles LEDs 2, 3, & 4)
const int MASK_B2 = 12;  // Binary 1100 (Toggles LEDs 3 & 4)
const int MASK_B3 = 3;   // Binary 0011 (Toggles LEDs 1 & 2)
const int MASK_B4 = 7;   // Binary 0111 (Toggles LEDs 1, 2, & 3)

// --- State Management ---
int boardState = 0; 

// --- Debouncing Variables (Using Pin Index for simplicity) ---
int buttonState[6] = {0};
int lastButtonState[6] = {LOW};
long lastDebounceTime[6] = {0};
const long debounceDelay = 50;

// Function to set the board state to a new random, solvable value
void setRandomStartState() {
    // Generates a number from 1 to 15 (4-bit range)
    boardState = random(1, 16); // Start from 1 to ensure it's not immediately won
    
    // Apply the new state to the physical LEDs
    updateLeds();

    Serial.print("\nInitial Random State (DEC): ");
    Serial.println(boardState); 
}

void setup() {
  // 1. IMPROVED RANDOMNESS: Use an analog pin to seed the random number generator
  randomSeed(analogRead(A0)); 
  
  // Initialize LED pins as outputs
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(ledPin3, OUTPUT);
  pinMode(ledPin4, OUTPUT);

  // Initialize pushbutton pins as inputs
  pinMode(buttonPin1, INPUT);
  pinMode(buttonPin2, INPUT);
  pinMode(buttonPin3, INPUT);
  pinMode(buttonPin4, INPUT); 
  
  // Start Serial for debugging the board state
  Serial.begin(9600);

  // Set the initial random starting state (and print to serial)
  setRandomStartState();
}

void updateLeds() {
  digitalWrite(ledPin1, (boardState >> 0) & 0x01); 
  digitalWrite(ledPin2, (boardState >> 1) & 0x01); 
  digitalWrite(ledPin3, (boardState >> 2) & 0x01); 
  digitalWrite(ledPin4, (boardState >> 3) & 0x01); 
}

void handleButtonPress(int pin, int mask) {
    int reading = digitalRead(pin);
    int pinIndex = pin; 

    if (reading != lastButtonState[pinIndex]) {
        lastDebounceTime[pinIndex] = millis();
    }

    if ((millis() - lastDebounceTime[pinIndex]) > debounceDelay) {
        if (reading != buttonState[pinIndex]) {
            buttonState[pinIndex] = reading;

            if (buttonState[pinIndex] == HIGH) {
                boardState = boardState ^ mask;
                
                Serial.print("Button ");
                Serial.print(pinIndex);
                Serial.print(" pressed. New State (DEC): ");
                Serial.println(boardState); 
            }
        }
    }
    lastButtonState[pinIndex] = reading;
}


void loop() {
  handleButtonPress(buttonPin1, MASK_B1);
  handleButtonPress(buttonPin2, MASK_B2);
  handleButtonPress(buttonPin3, MASK_B3);
  handleButtonPress(buttonPin4, MASK_B4); 

  updateLeds();

  if (boardState == WIN_STATE) 
  {
    
    // Flash LEDs for celebration (runs 4 times)
    for(int i = 0; i < 4; i++) // Run the flash sequence 4 times
    {
        // Alternating pattern 1 (1010 binary = 10 decimal)
        boardState = 0b1010; 
        updateLeds();
        delay(150);
        
        // Alternating pattern 2 (0101 binary = 5 decimal)
        boardState = 0b0101; 
        updateLeds();
        delay(150);
    }
    
    // Ensure all LEDs are off before announcing the reset
    boardState = 0;
    updateLeds();
    
    // Reset the puzzle to a new random state
    delay(500); // Small pause before resetting
    setRandomStartState();
  }
}
