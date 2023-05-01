const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const kafka = require('kafka-node')
const Producer = kafka.Producer;
const client = new kafka.KafkaClient({ kafkaHost: 'broker:29092' });

const PORT = process.env.PORT || 3005;

const app = express();

require('express-ws')(app);

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

const userIdToSocket = {};

app.ws('/listen_to_changes/:userId', function(ws, req) {

    console.log('listen to changes');
    const { userId } = req.params;

    userIdToSocket[userId] = ws;
});

const createConsumers = async() => {

    const admin = new kafka.Admin(client);

    const topics = ['create', 'update', 'delete'].map(
        topic =>
        ({
            topic,
            partitions: 1,
            replicationFactor: 1
        })
    );

    try {
        await createTopicsAsync(admin, topics);
        console.log("Topics created successfully");
    } catch (err) {
        console.error("Failed to create topics ${err}");
    }


    const notifyAllOwnersOfDocument = (msg) => {
        console.log('NOTIFY');
        console.log(msg);

        const value = JSON.parse(msg.value);
        const { topic } = msg;
        const { userId, filename, sharedList } = value;
        console.log(userIdToSocket);
        Object.entries(userIdToSocket).forEach(([id, w]) => {
            const message = `Document ${filename} has been ${topic}d`;
            w.send(message);
            // }
        });
    }

    const consumer = new kafka.Consumer(
        client, topics, { autoCommit: true });
    consumer.on('message', notifyAllOwnersOfDocument);
    consumer.on('error', (err) => console.log('err: ' + err.message));
    console.log('consumers created')

}


const createTopicsAsync = (admin, topics) => {
    return new Promise((resolve, reject) => {
        admin.createTopics(topics, (err, results) => {
            if (err) {
                reject(err);
            } else {
                resolve(results);
            }
        });
    });
};


(async() => {
    try {
        await createConsumers();
    } catch (err) {
        console.error(`Failed to create consumers: ${err}`);
    }
})();



const createProducer = new Producer(client);
const deleteProducer = new Producer(client);
const updateProducer = new Producer(client);
createProducer.on('ready', () =>
    console.log("create producer ready"));

deleteProducer.on('ready', () =>
    console.log("delete producer ready"));

updateProducer.on('ready', () =>
    console.log("update producer ready"));

createProducer.on('error', function(err) { console.log(err); });
deleteProducer.on('error', function(err) { console.log(err); });
updateProducer.on('error', function(err) { console.log(err); });

// Routes
app.post('/notify', async(req, res) => {
    const { filename, sharedList, method, userId } = req.body;

    if (!['create', 'update', 'delete'].includes(method)) {
        res.status(404).json({ message: "topic does not exist for " + method })
        return;
    }

    console.log(req.body);

    console.log('notify');


    try {
        const message = { topic: method, messages: JSON.stringify({ userId, filename, sharedList }), partition: 0 }
        const producer = { 'create': createProducer, 'delete': deleteProducer, 'update': updateProducer }[method];
        await sendMessage(producer, message);
        res.sendStatus(200);
    } catch (error) {
        console.log(error);
        res.sendStatus(500);
    }
});


const sendMessage = (producer, message) => {
    const payloads = [message];
    return new Promise(
        (res, rej) => {
            producer.send(payloads, (err, data) => {
                if (err) {
                    console.log(err);
                    rej();
                } else res();
            })
        }
    )
};

app.listen(PORT, () => {
    console.log(`Server listening on port ${PORT}`);
});