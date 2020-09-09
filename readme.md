![Politika Icon](https://github.com/richardj99/political-bias-detection/blob/master/Deliverable/res/icon.png)
# Detecting Political Bias in Text - Dissertation Project
Full documentation can be found in my **<ins>[Report](https://github.com/richardj99/political-bias-detection/blob/master/political-bias-detection.pdf)<ins>**
## Project Objectives
* Use Long Short-Term Memory Network to detect Left-Right Political Bias in Variable Length Text.
* Provide a UI that permits users to analyse their own texts against a trained model.
* Provide a simple database to store articles with relevant information as well as the results of analysis
## Key Points of Design & Implementation
### Libraries and Languages
* Swing UI Library Compiled through JetBrain's IntelliJ Form Designer **(OJDK 12)**
* TensorFlow w/ Keras & Pandas used for compiling and training Neural Network models **(Python 3.7)**
* Data retrieved using Java's XML and JSON Parser **(OJDK 12)**
### Data and Input Pipeline
* The Neural Network Train/Test/Validate data was compiled from the UK Parliamentary Debate Archive (Hansard) using the parlParse XML repository.
* The debate texts were retrieved from 2015-19 and labelled based on the political inclination to the political party of the speaker.
* Text data encoded using Tensorflow's subword-text encoder, to increase the longevity of the model used.
## Results
* All features of project implemented successfully.
* Final compiled model reached an accuracy of 73%