import tensorflow as tf
import tensorflow_datasets as tfds
import pandas as pd


def build_data_lists(path):
    df = pd.read_csv(csvPath, index_col=None, encoding='utf-8')
    textList = []
    partyList = []
    for i in range(0, df['text'].size):
        textItem = df['text'].get(i)
        partyItem = (df['party'].get(i))
        textList.append(str(textItem))
        partyList.append(str(partyItem))
    return textList, partyList


csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"

textData, partyData = build_data_lists(csvPath)
dataset = tf.data.Dataset.from_tensor_slices((textData, partyData))
textDataset = tf.data.Dataset.from_tensor_slices(textData)

tokenizer = tfds.features.text.Tokenizer()

vocab_set = set()
for text_tensor, _ in dataset:
    tokens = tokenizer.tokenize(text_tensor.numpy())
    vocab_set.update(tokens)
vocab_size = len(vocab_set)

#encoder = tfds.features.text.TokenTextEncoder(vocab_set)

encoder = tfds.features.text.SubwordTextEncoder(vocab_set)

encoder.save_to_file("subword_text_encoder")
