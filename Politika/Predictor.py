from tensorflow import int64, expand_dims, cast
import tensorflow as tf
from tensorflow.keras import models
from tensorflow_datasets import features
import sys, os


def encode_text(sentence):
    encoded_text = encoder.encode(sentence)
    return encoded_text

def sample_predict(sentence, pad):
    if pad:
        encoded_text = pad_to_size(sentence, 1987)
    encoded_text = tf.cast(encoded_text, tf.float32)
    predictions = model.predict(tf.expand_dims(encoded_text, 0))
    return predictions


def pad_to_size(vec, size):
    zeros = [0] * (size - len(vec))
    vec.extend(zeros)
    return vec


model = models.load_model("model.h5")
encoder = features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

if(os.path.exists("pwrite")):
    os.remove("pwrite")

if(os.path.exists("jwrite")):
    jWrite = open("jwrite", 'r', encoding='utf-8')
    input = jWrite.readline()
    jWrite.close()
    os.remove("jwrite")
    #print(input)
    encoded_input = encode_text(input)
    predictions = sample_predict(encoded_input, True)
    pwrite = open("pwrite", "w+", encoding="utf-8")
    for i in encoded_input:
        if i == 0:
            break
        pwrite.writelines(str(i)+"\t["+encoder.decode([i])+"],")
    pwrite.writelines("\n")
    #print(str(predictions.item(0)) + "," + str(predictions.item(1)))
    pwrite.writelines(str(predictions.item(0)) + "," + str(predictions.item(1)))
    pwrite.close()
