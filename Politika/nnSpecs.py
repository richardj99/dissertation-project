from tensorflow import int64, expand_dims, cast
import tensorflow as tf
from tensorflow.keras import models
from tensorflow_datasets import features
import sys, os

model = models.load_model("model.h5")
encoder = features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

if(os.path.exists("nnConfig")):
    os.remove("nnConfig")


file = open("nnConfig", "w+", encoding="utf-8")
file.write(str(encoder.vocab_size))
file.write("\n")
file.write(str(len(model.layers)))
for i in range(0, len(model.layers)):
    file.write("\n")
    l = model.get_layer(index=i).get_config()
    if(i == 0):
        file.write(str(l.get("name"))+" "+str(l.get("input_dim"))+" "+str(l.get("output_dim")))
    if(i == 1):
        l = l.get("layer").get("config")
        file.write(str(l.get("name"))+" "+str(l.get("units"))+" "+str(l.get("activation")))
    elif(i > 1):
        file.write(str(l.get("name"))+" "+str(l.get("units"))+" "+str(l.get("activation")))
file.close()