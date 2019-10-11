import tensorflow as tf
import tensorflow_datasets as tfds



mnist = tfds.load(input_data)

learningRate = 0.5
epochs = 10
batchSize = 100


x = tf.placeholder(tf.float32, [None, 784])
y = tf.placeholder(tf.float32, [None, 10])

W1 = tf.Variable(tf.random_normal([784, 300], stddev=0.03), name='W1')
b1 = tf.Variable(tf.random_normal([300]), name='b1')
W2 = tf.Variable(tf.random_normal([300, 10], stddev=0.03), name='W2')
b2 = tf.Variable(tf.random_normal([10]), name='b2')

hiddenOut = tf.add(tf.matmul(x, W1), b1)
hiddenOut = tf.nn.relu(hiddenOut)

outputLayer = tf.nn.softmax(tf.add(tf.matmul(hiddenOut, W2), b2))
outputLayerClipped = tf.clip_by_value(outputLayer, 1e-10, 0.9999999)
crossEntropy = -tf.reduce_mean(tf.reduce_sum(y * tf.log(outputLayerClipped)
                                             + (1-y) * tf.log(1-outputLayerClipped), axis=1))

optimiser = tf.train.GradientDescentOptimizer(learning_rate=learningRate).minimize(crossEntropy)

init_op = tf.global_variables_initializer()
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(outputLayer, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

# start the session
with tf.Session() as sess:
    # initialise the variables
    sess.run(init_op)
    total_batch = int(len(mnist.train.labels) / batchSize)
    for epoch in range(epochs):
        avg_cost = 0
        for i in range(total_batch):
            batch_x, batch_y = mnist.train.next_batch(batch_size=batchSize)
            _, c = sess.run([optimiser, crossEntropy],
                            feed_dict={x: batch_x, y: batch_y})
        avg_cost += c / total_batch
    print("Epoch:", (epoch + 1), "cost =", "{:.3f}".format(avg_cost))
print(sess.run(accuracy, feed_dict={x: mnist.test.images, y: mnist.test.labels}))
