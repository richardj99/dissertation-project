import tensorflow.keras as keras

(x_train, y_train), (x_test, y_test) = keras.datasets.mnist.load_data()

print(type(x_train), x_train)