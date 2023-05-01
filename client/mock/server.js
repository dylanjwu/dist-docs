const express = require('express');
const bodyParser = require('body-parser');

const app = express();
const port = 8080;
const cors = require('cors');

app.use(bodyParser.json());

app.use(cors());

// In-memory database of files and users
let files = [{ filename: 'file1', sharedList: [1, 2, 3], contents: 'blah blah blah', fileId: 1, createdBy: [1] },
    { filename: 'file2', sharedList: [1, 3], contents: 'hey hey', fileId: 2, createdBy: [1] },
    { filename: 'file3', sharedList: [1, 2, 3], contents: 'bye bye', fileId: 3, createdBy: [2] }
];
let users = [{ id: 1, username: 'dylan', password: '123' }, { id: 2, username: 'jacky', password: '123' }, { id: 3, username: 'sida', password: '123' }];

// File endpoints
app.post('/file/createFile', (req, res) => {
    const { filename, sharedList, contents, fileId, createdBy } = req.body;

    const newFile = {
        id: fileId,
        filename,
        sharedList,
        contents,
        createdBy,
    };

    files.push(newFile);

    res.status(201).send('File created successfully');
});

app.put('/file/updateFile', (req, res) => {
    const { filename, sharedList, contents, fileId, createdBy } = req.body;

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (file.id = fileId) {
            files[i] = {...files[i], contents };
        }
    }

    res.status(201).send('File created successfully');
});


app.delete('/file/deleteFile', (req, res) => {
    const { id } = req.query.id;

    const fileIndex = files.findIndex((file) => file.id === id);

    if (fileIndex === -1) {
        res.status(404).send('File not found');
        return;
    }

    files.splice(fileIndex, 1);

    res.status(200).send('File deleted successfully');
});



// User endpoints
app.post('/user/createUser', (req, res) => {
    const { username, password } = req.body;

    const newUser = {
        id: users.length + 1,
        username,
        password,
    };

    users.push(newUser);

    res.status(201).send({ message: 'User created successfully', id: 1 });
});

app.get('/user/getUserInfo', (req, res) => {
    const { id } = req.query.id;

    const user = users.find((user) => user.id === id);

    if (!user) {
        res.status(404).send('User not found');
        return;
    }

    res.status(200).send(user);
});

app.get('/file/getAllFilesInfo', (req, res) => {
    const { id } = req.query;


    // if (!user) {
    //     res.status(404).send('User not found');
    //     return;
    // }

    // console.log(id)
    // const filesForUser = files.filter((({ sharedList }) => sharedList.includes(1)));
    // console.log(filesForUser);

    res.status(200).send(files);
});

app.get('/user/getAllUsers', (req, res) => {
    res.status(200).send(users);
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});