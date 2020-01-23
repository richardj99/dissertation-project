import tensorflow as tf
import tensorflow_datasets as tfds
import pandas as pd


def build_data_lists(path):
    df = pd.read_csv(csvPath, index_col=None, encoding='latin_1')
    textList = []
    partyList = []
    for i in range(0, df['text'].size):
        print(i)
        textItem = df['text'].get(i)
        partyItem = (df['party'].get(i))
        if (partyItem == "liberal-democrats") or (partyItem == "conservative") or (partyItem == "labour") or \
                (partyItem == "scottish-national-party") or (partyItem == "dup"):
            textList.append(str(textItem))
            partyList.append(str(partyItem))
    return textList, partyList


csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\dissertation-project\\xmlParser\\hansardData.csv"

textData, partyData = build_data_lists(csvPath)

dataset = tf.data.Dataset.from_tensor_slices((textData, partyData))
textDataset = tf.data.Dataset.from_tensor_slices(textData)
for ex in dataset.take(5):
    print(ex[0].numpy())

tokenizer = tfds.features.text.Tokenizer()

vocab_set = set()
for text_tensor, _ in dataset:
    tokens = tokenizer.tokenize(text_tensor.numpy())
    vocab_set.update(tokens)

vocab_size = len(vocab_set)
print(vocab_size)

#encoder = tfds.features.text.TokenTextEncoder(vocab_set)

encoder = tfds.features.text.SubwordTextEncoder(vocab_set)

encoder.save_to_file("subword_text_encoder")
