import tensorflow as tf
import tensorflow_datasets as tfds

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.pyplot as plt
import os


def showBatch(dataset):
    for batch, label in dataset.take(1):
        for key, value in batch.items():
            print("{:20s}: {}".format(key, value.numpy()))
        print(label)



csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\Analysis\\" \
          "xmlParser\\hansardData.csv"

column_names = ["text", "party"]

feature_name = "text"
label_name = "party"

batchSize = 1  # how many data-points it takes in at each iteration
step = 1  # How many pieces of data it will take in at a time

df = pd.read_csv(csvPath, index_col=None)
df.head()

print(df)

df_slices = tf.data.Dataset.from_tensor_slices(df)



'''

for feature_batch in df_slices.take(1):
    for key, value in feature_batch.items():
        print("  {!r:20s}: {}".format(key, value))
        
trainDataset = tf.data.experimental.CsvDataset(
    filenames=data_csv,
    record_defaults=[tf.string, tf.string],
    select_cols=[1,2],
    field_delim=",",
    header=True
)




print(type(trainDataset))

print(trainDataset.numpy())

tokenizer = tfds.features.text.Tokenizer()

vocabSet = set()
for text_tensor, _ in trainDataset:
    item = tf.convert_to_tensor((np.asarray(text_tensor.popitem())).item(1))
    print(item)
    print(type(item))
    someTokens = tokenizer.tokenize(item.numpy())
    vocabSet.update(someTokens)

vocabSize = len(vocabSet)
print(vocabSize)



reader = tf.TextLineReader()
_, lines = reader.read(data_csv)
tensor = tf.string_split(lines, ...)
print(tensor)

'''
