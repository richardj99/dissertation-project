import pandas as pd
import tensorflow_datasets as tfds

path = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"

csv = pd.read_csv(path)

text_columns = csv['text']

encoder = tfds.features.text.SubwordTextEncoder.load_from_file("subword_text_encoder")

longest_sentence = 0

for sentence in text_columns:
    word_array = encoder.encode(sentence)
    if len(word_array) > longest_sentence:
        longest_sentence = len(word_array)
        print(longest_sentence)

# 1987