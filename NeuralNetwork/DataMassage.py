import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.model_selection import train_test_split
import os

def pandas_dataset_builder(inp_df, batch, buffer, Steps):
    inp_df = inp_df.copy()
    labels = inp_df.pop('party')
    ds = tf.data.Dataset.from_tensor_slices((dict(inp_df), labels))
    ds = ds.map(encode_map_fn)
    ds = ds.take(Steps * BATCH_SIZE)
    ds = ds.repeat(EPOCHS)
    ds = ds.padded_batch(BATCH_SIZE, padded_shapes=([None], []))
    return ds

def sample_predict(sentence, pad, model):
    if pad:
        encoded_text = pad_to_size(sentence, 1987)
    encoded_text = tf.cast(encoded_text, tf.float32)
    predictions = model.predict(tf.expand_dims(encoded_text, 0))
    return predictions

def pad_to_size(vec, size):
    zeros = [0] * (size - len(vec))
    vec.extend(zeros)
    return vec

def encode_text(sentence):
    encoded_text = encoder.encode(sentence)
    return encoded_text


BUFFER_SIZE = 10000

BATCH_SIZE = 1

TRAIN_SIZE = 6000
VAL_SIZE = 600
TEST_SIZE = 600

TRAIN_STEPS = 100
VAL_STEPS = 30
TEST_STEPS = 25


libFile = open("dataMassage/LibWeighted.csv", "w+", encoding="utf-8")
conFile = open("dataMassage/ConWeighted.csv", "w+", encoding="utf-8")

libModel = tf.keras.models.load_model("train_logs\\libWeighted20EPOCHS_5LayerSize_32Batch_1HL_I1\\model.h5")
conModel = tf.keras.models.load_model("train_logs\\conWeighted20EPOCHS_5LayerSize_32Batch_1HL_I1\\model.h5")

encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"
encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

df = pd.read_csv(csvPath, encoding="utf-8")
train_df, val_df = train_test_split(df, test_size=0.2)  # Transfers 20% of dataset to val_data, rest moves to train
train_df, test_df = train_test_split(train_df, test_size=0.2)  # Transfers 20% of dataset to test_data, rest moves to train

for index, row in test_df.iterrows():
    encodedText = encode_text(row[0])
    
    libPredictions = sample_predict(encodedText, True, libModel)
    for con, lib in libPredictions:
        libDifference = float(lib) - float(con)
    
    conPredictions = sample_predict(encodedText, True, conModel)
    for con, lib in conPredictions:
        conDifference = float(con) - float(lib)

    #print(libDifference, conDifference)
    libFile.write(str(libDifference) +","+ str(row[1]) +",\""+ str(row[0]) + "\"\n")
    conFile.write(str(conDifference) +","+ str(row[1]) +",\""+ str(row[0]) + "\"\n")
conFile.close()
libFile.close()