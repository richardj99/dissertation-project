import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split


def build_csv_dataset(file_path, **kwargs):
    dataset = tf.data.experimental.make_csv_dataset(
        file_path,
        batch_size=1,
        label_name='party',
        na_value="?",
        num_epochs=1,
        ignore_errors=True,
        select_columns=['party', 'text'],
        **kwargs)
    return dataset


def pandas_dataset_builder(inp_df, batch, buffer, shuffle=True):
    inp_df = inp_df.copy()
    labels = inp_df.pop('party')
    ds = tf.data.Dataset.from_tensor_slices((dict(inp_df), labels))
    ds = ds.map(encode_map_fn)
    if shuffle:
        ds = ds.shuffle(buffer_size=buffer)
#    ds = ds.padded_batch(batch, padded_shapes=([1987], [None]))
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
    predictions = model.predict(tf.expand_dims(encoded_sample_pred_text, 0))
    return predictions


def plot_graphs(history, string):
    plt.plot(history.history[string])
    plt.plot(history.history['val_' + string], '')
    plt.xlabel("Epochs")
    plt.ylabel(string)
    plt.legend([string, 'val_' + string])
    plt.show()


BUFFER_SIZE = 10000
BATCH_SIZE = 12

TRAIN_SIZE = 6000
VAL_SIZE = 600
TEST_SIZE = 600

TRAIN_STEPS = 10
VAL_STEPS = 10
TEST_STEPS = 5

EPOCHS = 5

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"
encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

#dataset = build_csv_dataset(csvPath)

df = pd.read_csv(csvPath, encoding='latin_1')
train_df, val_df = train_test_split(df, test_size=0.2) # Transfers 20% of dataset to val_data, rest moves to train
train_df, test_df = train_test_split(train_df, test_size=0.2) # Transfers 20% of dataset to test_data, rest moves to train

train_data = pandas_dataset_builder(train_df, BATCH_SIZE, BUFFER_SIZE, True)
val_data = pandas_dataset_builder(val_df, BATCH_SIZE, BUFFER_SIZE, True)
test_data = pandas_dataset_builder(test_df, BATCH_SIZE, BUFFER_SIZE, True)

for text, label in train_data.take(3):
    print(text)
    print(label)

#dataset = dataset.map(encode_map_fn)

#train_data = dataset.take(6000).shuffle(BUFFER_SIZE)
#train_data = train_data.padded_batch(BATCH_SIZE, padded_shapes=([1987], [None]))
#val_data = dataset.take(600).shuffle(BUFFER_SIZE)
#val_data = val_data.padded_batch(BATCH_SIZE, padded_shapes=([1987], [None]))
#test_data = dataset.take(600).shuffle(BUFFER_SIZE)
#test_data = test_data.padded_batch(BATCH_SIZE, padded_shapes=([1987], [None]))

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(encoder.vocab_size, 64),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64, activation='tanh')),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(2, activation='softmax')
])


model.compile(loss='categorical_crossentropy',
              optimizer=tf.keras.optimizers.Adam(1e-10),
              metrics=['accuracy', 'categorical_accuracy'])

print(model.summary())

history = model.fit(train_data, epochs=EPOCHS,
                    validation_data=val_data,
                    steps_per_epoch=TRAIN_STEPS,
                    validation_steps=VAL_STEPS)

#test_loss, test_acc = model.evaluate(test_data, steps=TEST_STEPS)#
#
#print('Test Loss: {}'.format(test_loss))
#print('Test Accuracy: {}'.format(test_acc))

sample_pred_text = 'Brexit is great!'
predictions = sample_predict(sample_pred_text, pad=True)
print(predictions)

plot_graphs(history, 'accuracy')