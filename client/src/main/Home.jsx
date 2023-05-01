import axios from 'axios';
import { useEffect, useState } from 'react';
import Button from 'react-bootstrap/Button';
import ListGroup from 'react-bootstrap/ListGroup';
import { Link, useNavigate } from 'react-router-dom';
import '../App.css';
import CreateModal from './CreateModal';
import FileInput from './FileInput';

const SERVER_HOST = 'http://localhost:8080';

const getFileGroups = (docs, currDoc, selectDoc, setEditingNewDoc) => {
  return docs.map((doc, index) => (
    <ListGroup.Item key={index} action active={currDoc?.id === doc.id} variant="secondary" className="file" onClick={() => { 
      console.log({currDoc: currDoc?.id});
      console.log({doc: doc?.id});
      setEditingNewDoc(false);
      selectDoc(doc, index);
      }}>
      {doc.fileName}
    </ListGroup.Item>
  ))
}


function Home() {

  const navigate = useNavigate();
  const [docs, setDocs] = useState([]);
  const [notifyMsg, setNotifyMsg] = useState('');
  const [currDoc, setDoc] = useState({});
  const [modalShown, showModal] = useState(false);
  const openModal = () => showModal(true);
  const closeModal = () => { showModal(false); console.log(modalShown); }
  const [allUsers, setAllUsers] = useState([]);
  const [editingNewDoc, setEditingNewDoc] = useState(false);
  const id = localStorage.getItem('user_id');
  const [messages, setMessages] = useState([]);

  const redirectToLogin = () => {
    if (!localStorage.getItem('user_id')) {
      navigate('/login')
    }
  }

  const fetchFiles = () => {
    axios.get(`${SERVER_HOST}/file/getAllFilesInfo?userId=${id}`).then(({ data }) => { 
        console.log(data);
        setDocs(data);
        setDoc(data[0]);
      });
  }

  const logout = () => {
    localStorage.removeItem('user_id');
  }

  const openNewDoc = (setEditingNewDoc) => {
    setDoc({ id: -1, fileName: 'New Doc', contents: ''} );
    setEditingNewDoc(true);
  }

  const connectToWS = () => {
    const websocket = new WebSocket(`ws://localhost:3005/listen_to_changes/${id}`);
    console.log(`ws://node_app:3005/listen_to_changes/${id}`);
    websocket.onmessage = (msg) => {
      console.log(msg.data);
      setMessages([...messages, msg.data]);
      setNotifyMsg(msg.data);
      fetchFiles();
      setTimeout(() => {
        setNotifyMsg("");
      },10000);
    } 
    websocket.onerror = (err) => {
      console.log("error: " + err);
    }
  }
  
  const selectDoc = (doc, index) => {
    console.log("selected: " + doc);
    console.log(index);
    setDoc(doc);
    console.log(currDoc);
  }

  useEffect(() => {
    console.log(`currDoc: ${currDoc}`);
  }, [currDoc]);

  const fetchAllUsers = () => {
    axios.get(`${SERVER_HOST}/user/getAllUsers`).then(({ data }) => setAllUsers(data));
  }

  const deleteDoc = async () =>  {
    console.log("delete");
    const result = await axios.delete(`${SERVER_HOST}/file/deleteFile?id=${currDoc.id}`);
    console.log(result);
  }

  const updateDoc = async () => {
    console.log("update");
    const result = await axios.put(`${SERVER_HOST}/file/updateFile`, {
      contents: currDoc.contents,
      fileId: currDoc.id,
    });
    console.log('updated');
    console.log(result);
  }


  useEffect(() => {
    redirectToLogin();
    fetchAllUsers();
    fetchFiles();
    connectToWS();
  }, []);
  
   return (
    <div className="app-container">
    {notifyMsg && <div className="popup-tag">{notifyMsg}</div>}
    <div className="header"><h2>Dist Doc</h2></div>
    <Link to="/login">
      <span onClick={logout}>Logout</span>
    </Link>
    {/* { <Notification messages={messages} /> } */}
    <div className="container">
      <div className="files-container">
        <Button variant="light" onClick={() => openNewDoc(setEditingNewDoc)}>New doc</Button>
        <div className="files-group-wrapper">
          <ListGroup className="files-group">
            {getFileGroups(docs, currDoc, selectDoc, setEditingNewDoc)}
          </ListGroup> 
        </div>
      </div>
      <FileInput currDoc={currDoc} setDoc={setDoc}></FileInput>
      { modalShown && <CreateModal doc={currDoc} onClose={closeModal} users={allUsers} userId={id} fetchFiles={fetchFiles} ></CreateModal>}
      <div className="buttons">
        <Button variant="light" disabled={!editingNewDoc} onClick={openModal}>Create</Button>
        <Button variant="light" disabled={editingNewDoc || docs.length === 0} onClick={ updateDoc }> Update</Button>
        <Button variant="light" disabled={editingNewDoc || docs.length === 0} onClick={ deleteDoc }>Delete</Button>
      </div>
    </div>
   </div>
  );
}

export default Home;
