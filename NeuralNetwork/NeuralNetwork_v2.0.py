import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split
import os


def df_to_ds(inp_df, batch, buffer, steps):
    func_df = inp_df.copy()
    party_labels = func_df.pop('party')
    text = func_df.pop('text')
    party_ds = tf.data.Dataset.from_tensor_slices(party_labels)
    text_ds = tf.data.Dataset.from_tensor_slices(text)
    for item in text_ds:
        print(item, type(item))
    text_ds = text_ds.map(encode_map_fn)
    party_ds = party_ds.take(steps * batch)
    text_ds = text_ds.take(steps * batch)
    party_ds = party_ds.repeat(EPOCHS)
    text_ds = text_ds.repeat(EPOCHS)
    text_ds = text_ds.padded_batch(batch, padded_shapes=([None]))
    return text_ds, party_ds



def text_encode(text):
    text = text.numpy()
    encoded_text = encoder.encode(text)
    return encoded_text


def encode(text_tensor, label):
    encoded_text = encoder.encode(text_tensor.numpy())
    return encoded_text, label


def encode_map_fn(text):
    return tf.py_function(text_encode, inp=[text], Tout=(tf.int64))



def pad_to_size(vec, size):
    zeros = [0] * (size - len(vec))
    vec.extend(zeros)
    return vec


def sample_predict(sentence, pad):
    encoded_sample_pred_text = encoder.encode(sample_pred_text)
    if pad:
        encoded_sample_pred_text = pad_to_size(encoded_sample_pred_text, 1987)
    encoded_sample_pred_text = tf.cast(encoded_sample_pred_text, tf.float32)
    predictions = model.predict(tf.expand_dims(encoded_sample_pred_text, 0))
    return predictions


def plot_graphs(history, string, path):
    plt.plot(history.history[string])
    plt.plot(history.history['val_' + string], '')
    plt.xlabel("Epochs")
    plt.ylabel(string)
    plt.legend([string, 'val_' + string])
    plt.savefig(path + "/plot.png")
    plt.show()


BUFFER_SIZE = 10000

BATCH_SIZE = 32

TRAIN_SIZE = 6000
VAL_SIZE = 600
TEST_SIZE = 600

TRAIN_STEPS = 100
VAL_STEPS = 30
TEST_STEPS = 25

EPOCHS = 20
LAYER_SIZE = 5

logPath = "train_logs/conWeighted" + str(EPOCHS) + "EPOCHS_" + str(LAYER_SIZE) + "LayerSize_" + str(
    BATCH_SIZE) + "Batch_" + "1HL"
if not (os.path.isdir(logPath + "/I1")):
    logPath = logPath + "_I1"
elif not (os.path.isdir(logPath + "/I2")):
    logPath = logPath + "_I2"
elif not (os.path.isdir(logPath + "/I3")):
    logPath = logPath + "_I3"
elif not (os.path.isdir(logPath + "/I4")):
    logPath = logPath + "/I4"
elif not (os.path.isdir(logPath + "/I5")):
    logPath = logPath + "/I5"
#os.mkdir(logPath)
print(logPath)

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"
encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

df = pd.read_csv(csvPath, encoding='utf-8')
train_df, val_df = train_test_split(df, test_size=0.2)  # Transfers 20% of dataset to val_data, rest moves to train
train_df, test_df = train_test_split(train_df,
                                     test_size=0.2)  # Transfers 20% of dataset to test_data, rest moves to train

#train_data = pandas_dataset_builder(train_df, BATCH_SIZE, BUFFER_SIZE, TRAIN_STEPS, True)
#val_data = pandas_dataset_builder(val_df, BATCH_SIZE, BUFFER_SIZE, VAL_STEPS, True)
#test_data = pandas_dataset_builder(test_df, BATCH_SIZE, BUFFER_SIZE, TEST_STEPS, True)

train_data, train_labels = df_to_ds(train_df, BATCH_SIZE, BUFFER_SIZE, TRAIN_STEPS)
val_data, val_labels = df_to_ds(val_df, BATCH_SIZE, BUFFER_SIZE, VAL_STEPS)
test_data, test_labels = df_to_ds(test_df, BATCH_SIZE, BUFFER_SIZE, TEST_STEPS)

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(encoder.vocab_size, LAYER_SIZE),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(LAYER_SIZE, activation='tanh')),
    tf.keras.layers.Dense(LAYER_SIZE, activation='relu'),
    tf.keras.layers.Dense(2, activation='sigmoid')  # previously softmax
])

model.compile(loss='categorical_crossentropy',
              optimizer=tf.keras.optimizers.Adam(lr=1e-4),
              metrics=['accuracy', 'categorical_accuracy'])

for i in train_data.take(1):
    print(i)

print(model.summary())

history = model.fit(train_data, train_labels, epochs=EPOCHS,
                    validation_data=(val_data, val_labels),
                    steps_per_epoch=TRAIN_STEPS,
                    validation_steps=VAL_STEPS)

model.save(logPath + "/model.h5")

test_loss, test_acc, test_categorical_acc = model.evaluate(test_data, steps=TEST_STEPS)

print('Test Loss: {}'.format(test_loss))
print('Test Accuracy: {}'.format(test_acc))
print('Categorical Accuracy: {}'.format(test_categorical_acc))

testTextFile = open("testText", "r")
sample_pred_text = testTextFile.readline()

predictions = sample_predict(sample_pred_text, pad=True)
print(predictions)

plot_graphs(history, 'accuracy', logPath)
