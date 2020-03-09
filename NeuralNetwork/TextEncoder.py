import tensorflow as tf
import tensorflow_datasets as tfds

csvPath = "C:\\Users\\richa\\OneDrive\\Documents\\Year 3\\CSC-30014\\dissertation-project\\Analysis\\" \
          "xmlParser\\hansardData.csv"

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