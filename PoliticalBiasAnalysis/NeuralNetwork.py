import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt


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


def encode(text_tensor, label):
    encoded_text = encoder.encode(text_tensor.numpy())
    return encoded_text, label


def encode_map_fn(text, label):
    text_input = (text['text'])[0]
    label_input = tf.cast(label, tf.int64)
    return tf.py_function(encode, inp=[text_input, label_input], Tout=(tf.int64, tf.int64))


def pad_to_size(vec, size):
    zeros = [0] * (size - len(vec))
    vec.extend(zeros)
    return vec


def sample_predict(sentence, pad):
    encoded_sample_pred_text = encoder.encode(sample_pred_text)
    if pad:
        encoded_sample_pred_text = pad_to_size(encoded_sample_pred_text, 64)
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
BATCH_SIZE = 6

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\xmlParser\\hansardData.csv"

dataset = build_csv_dataset(csvPath)
print(dataset, type(dataset))
encodedData = []

encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

dataset = dataset.map(encode_map_fn)

train_data = dataset.take(6000).shuffle(BUFFER_SIZE)
train_data = train_data.padded_batch(BATCH_SIZE, padded_shapes=([None], [None]))
val_data = dataset.take(600).shuffle(BUFFER_SIZE)
val_data = val_data.padded_batch(BATCH_SIZE, padded_shapes=([None], [None]))
test_data = dataset.take(600).shuffle(BUFFER_SIZE)
test_data = test_data.padded_batch(BATCH_SIZE, padded_shapes=([None], [None]))

model = tf.keras.Sequential([
    tf.keras.layers.Embedding(encoder.vocab_size, 64
                              ),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64, activation='sigmoid')),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(2, activation='sigmoid')
])


model.compile(loss='binary_crossentropy',
              optimizer=tf.keras.optimizers.Adam(1e-4),
              metrics=['accuracy'])

history = model.fit(train_data, epochs=10,
                    validation_data=val_data,
                    steps_per_epoch=100,
                    validation_steps=100)

test_loss, test_acc = model.evaluate(test_data, steps=100)

print('Test Loss: {}'.format(test_loss))
print('Test Accuracy: {}'.format(test_acc))

sample_pred_text = 'Brexit is great!'
predictions = sample_predict(sample_pred_text, pad=False)
print(predictions)

plot_graphs(history, 'accuracy')