import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split
import os


def pandas_dataset_builder(inp_df, batch, buffer, Steps, shuffle=False):
    inp_df = inp_df.copy()
    labels = inp_df.pop('party')
    ds = tf.data.Dataset.from_tensor_slices((dict(inp_df), labels))
    ds = ds.map(encode_map_fn)
    ds = ds.take(Steps * BATCH_SIZE)
    ds = ds.repeat(EPOCHS)
    if shuffle:
        ds = ds.shuffle(buffer_size=buffer)
    ds = ds.padded_batch(BATCH_SIZE, padded_shapes=([None], []))
    return ds


def encode(text_tensor, label):
    encoded_text = encoder.encode(text_tensor.numpy())
    return encoded_text, label


def encode_map_fn(text, label):
    text_input = (text['text'])
    label_input = tf.cast(label, tf.int64)
    return tf.py_function(encode, inp=[text_input, label_input], Tout=(tf.int64, tf.int64))


def pad_to_size(vec, size):
    zeros = [0] * (size - len(vec))
    vec.extend(zeros)
    return vec


def sample_predict(sentence, pad):
    encoded_sample_pred_text = encoder.encode(sample_pred_text)
    if pad:
        encoded_sample_pred_text = pad_to_size(encoded_sample_pred_text, 1987)
    encoded_sample_pred_text = tf.cast(encoded_sample_pred_text, tf.float32)
    predictions = best_model.predict(tf.expand_dims(encoded_sample_pred_text, 0))
    return predictions


def plot_graphs(history, string, path):
    plt.plot(history.history[string])
    plt.plot(history.history['val_' + string], '')
    plt.xlabel("Epochs")
    plt.ylabel(string)
    plt.legend([string, 'val_' + string])
    plt.savefig(path + "/plot.png")
    plt.show()


BUFFER_SIZE = 1000

BATCH_SIZE = 15

TRAIN_SIZE = 6108
VAL_SIZE = 1909
TEST_SIZE = 1527

TRAIN_STEPS = round(TRAIN_SIZE/BATCH_SIZE-0.5)
VAL_STEPS = round(VAL_SIZE/BATCH_SIZE-0.5)
TEST_STEPS = round(TEST_SIZE/BATCH_SIZE-0.5)

EPOCHS = 30

logPath = "train_logs/" + "1HL_" + str(BATCH_SIZE) + "Batch_" + str(EPOCHS) + "EPOCHS"
if not (os.path.isdir(logPath)):
    os.mkdir(logPath)
if not (os.path.isdir(logPath + "/I1")):
    logPath = logPath + "/I1"
elif not (os.path.isdir(logPath + "/I2")):
    logPath = logPath + "/I2"
elif not (os.path.isdir(logPath + "/I3")):
    logPath = logPath + "/I3"
elif not (os.path.isdir(logPath + "/I4")):
    logPath = logPath + "/I4"
elif not (os.path.isdir(logPath + "/I5")):
    logPath = logPath + "/I5"
modelLoc = logPath + "/model.h5"
os.mkdir(logPath)
board_logs = ".\\logs\\"

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"
encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

df = pd.read_csv(csvPath, encoding='utf-8')
train_df, val_df = train_test_split(df, test_size=0.2)  # Transfers 20% of dataset to val_data, rest moves to train
train_df, test_df = train_test_split(train_df,
                                     test_size=0.2)  # Transfers 20% of dataset to test_data, rest moves to train

train_data = pandas_dataset_builder(train_df, BATCH_SIZE, BUFFER_SIZE, TRAIN_STEPS)
val_data = pandas_dataset_builder(val_df, BATCH_SIZE, BUFFER_SIZE, VAL_STEPS)
test_data = pandas_dataset_builder(test_df, BATCH_SIZE, BUFFER_SIZE, TEST_STEPS)

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(encoder.vocab_size, BATCH_SIZE),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(BATCH_SIZE, activation='tanh')),#, return_sequences=True)),
    #tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(BATCH_SIZE, activation='tanh')),
    tf.keras.layers.Dense(BATCH_SIZE*2, activation='relu'),
    #tf.keras.layers.Dense(BATCH_SIZE*2, activation='relu'),
    #tf.keras.layers.Dense(BATCH_SIZE*2, activation='relu'),
    tf.keras.layers.Dense(2, activation='sigmoid')  # previously sigmoid
])

model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),#'categorical_crossentropy',
              optimizer=tf.keras.optimizers.Adam(lr=1e-4),
              metrics=['accuracy'])

print(model.summary())

tensorboard_callback = tf.keras.callbacks.TensorBoard(log_dir=board_logs, histogram_freq=1)
early_stop_callback = tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience = 3)
mc = tf.keras.callbacks.ModelCheckpoint(modelLoc, monitor='val_loss', save_best_only=True)

history = model.fit(train_data, epochs=EPOCHS,
                    validation_data=val_data,
                    steps_per_epoch=TRAIN_STEPS,
                    validation_steps=VAL_STEPS,
                    callbacks=[tensorboard_callback, early_stop_callback, mc])

#model.save(logPath + "/model.h5")

best_model = tf.keras.models.load_model(modelLoc)

test_loss, test_acc = best_model.evaluate(test_data, steps=TEST_STEPS)

print('Best Model Test Loss: {}'.format(test_loss))
print('Best Model Test Accuracy: {}'.format(test_acc))

testTextFile = open("testText", "r")
sample_pred_text = testTextFile.readline()

predictions = sample_predict(sample_pred_text, pad=True)
print(predictions)

plot_graphs(history, 'accuracy', logPath)
