# JIEnc
A Java image encoder programmed in Java using Swing as GUI Toolkit. JIEnc allows to encode messages in a PNG image and decode them later.

## Execution
You can download the pre-build .JAR file.
For the execution you must have a jre installed and type the following command:
```
java -jar JIEnc.jar
```

## Usage example
To encode an image:
1. Choose a valid .PNG image with the 'Browse' button.
2. Type a message in the 'Message' field.
3. Press 'Encode' and choose the destination path.

To decode an image:
1. Choose a valid .PNG image with the 'Browse' button.
2. Press 'Decode'. The decoded message will appear in the 'Message' field.

## Source code
Source code is available and you can import it to your Eclipse workspace.

## How does this work?
Each pixel of the image is represented by 3 colors and each color is stored in one byte of information. This application uses the last bit of each color to encode a message. Therefore, 3 bits are available per pixel.

_Example of uncoded image:_
![alt text](https://github.com/Jorgeortiz97/JIEnc/blob/master/doc/uncoded.png?raw=true "Uncoded image")

_Example of encoded image:_
![alt text](https://github.com/Jorgeortiz97/JIEnc/blob/master/doc/encoded.png?raw=true "Encoded image")

Changes are not visible to the naked eye.
