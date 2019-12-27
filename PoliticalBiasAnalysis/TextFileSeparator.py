# -*- coding: utf-8 -*-

import tensorflow as tf
import tensorflow_datasets as tfds

import pandas as pd
import codecs
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.pyplot as plt
import os


def encode(tensor, label):
    encoded_text = encoder.encode(tensor.numpy())
    return encoded_text, label


def encode_map_fn(text, label):
    return tf.py_function(encode, inp=[text, label], Tout=(tf.int64, tf.int64))


csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\Analysis\\" \
          "xmlParser\\hansardData.csv"

column_names = ["text", "party"]

feature_name = "text"
label_name = "party"

batchSize = 1  # how many data-points it takes in at each iteration
step = 1  # How many pieces of data it will take in at a time

dataset = tf.data.TextLineDataset(csvPath)
df = pd.read_csv(csvPath, index_col=None)

textData = []
partyData = []


for i in range(0, df['text'].size):
    textItem = df['text'].get(i)
    print(textItem)
    textItem.encode("utf-8")
    partyItem = (df['party'].get(i)).strip()
    textData.append(str(textItem))
    partyData.append(str(partyItem))


dataset = tf.data.Dataset.from_tensor_slices((textData, partyData))
textDataset = tf.data.Dataset.from_tensor_slices(textData)
for ex in dataset.take(5):
    # print(ex)
    print(ex[0].numpy())

tokenizer = tfds.features.text.Tokenizer()

vocab_set = set()
for text_tensor, _ in dataset:
    tokens = tokenizer.tokenize(text_tensor.numpy())
    vocab_set.update(tokens)

vocab_size = len(vocab_set)
print(vocab_size)

encoder = tfds.features.text.TokenTextEncoder(vocab_set)

example = next(iter(dataset))[0].numpy()

example_encoded = encoder.encode(example)
print(encoder.decode(example_encoded))

encoded_dataset = dataset.map(encode_map_fn)



