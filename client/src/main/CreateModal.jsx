import axios from 'axios';
import { useEffect, useState } from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import ListGroup from 'react-bootstrap/ListGroup';
import Modal from 'react-bootstrap/Modal';
const SERVER_HOST = "http://localhost:8080";

const User = ({ id, name, checked, handleCheck }) => {
  return (
    <ListGroup.Item key={id}>
      <Form.Check
        type="checkbox"
        label={name}
        checked={checked}
        onChange={handleCheck}
      />
    </ListGroup.Item>
  )
}

const CreateForm = ({ label, setLabel, users, checkedUsers, handleUserCheck, userId }) => {
  console.log({users});
  return (<Form>
    <Form.Group controlId="formBasicText">
      <Form.Label>Label</Form.Label>
      <Form.Control type="text" value={label} onChange={(e) => setLabel(e.target.value)} />
    </Form.Group>
    <div className="users-group-wrapper">
      <Form.Group controlId="formListGroup">
        <ListGroup>
          {users.filter(({ id }) => id !== userId).map(({ id, username }, i) => (
            <User key={id} id={id} name={username} checked={checkedUsers.includes(id)}
              handleCheck={() => handleUserCheck(id)}></User>
          ))}
        </ListGroup>
      </Form.Group>
    </div>


  </Form>);
}

export default function CreateModal({ doc, users, userId, onClose, fetchFiles }) {
  // const [show, setShow] = useState(true);

  console.log({users});

  const [label, setLabel] = useState("");

  const [checkedUsers, setCheckedUsers] = useState([]); 

  // const users = [{ username: 'bob', id: 1 },
  //   { username: 'sida', id: 2 }, { username: 'jacky', id: 3 }, { username: 'dylan', id: 4 }, 
  //   { username: 'dylan', id: 5 }, { username: 'dylan', id: 6 }];

  const handleSubmit = async (label, checkedUsers) => { 
    console.log(JSON.stringify({checkedUsers}));   

    console.log("create");
    const result = await axios.post(`${SERVER_HOST}/file/createFile`, {
      filename: label, 
      sharedList: [userId, ...checkedUsers],
      contents: doc?.contents || '',
      createBy: userId,// userId
    });
  
    console.log(result);
    fetchFiles();
    onClose(false)
  };

  const handleClose = () => onClose(false);

  
  const handleUserCheck = (id) => {
    let newCheckedUsers = checkedUsers.slice();
    if (newCheckedUsers.includes(id)) {
      newCheckedUsers = newCheckedUsers.filter(el => el !== id);
    } else {
      newCheckedUsers.push(id);
    }
    setCheckedUsers(newCheckedUsers);
  };


  useEffect(() => {
    console.log(`checked users: ${JSON.stringify(checkedUsers)}`);
  }, [checkedUsers]);


  useEffect(() => {
    console.log(`label: ${label}`);
  }, [label]);

  return (<Modal show={true} onHide={handleClose}>
    <Modal.Header closeButton>
      <Modal.Title>Create New Document</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      <CreateForm userId={userId} label={label} setLabel={setLabel} users={users} checkedUsers={checkedUsers} handleUserCheck={handleUserCheck}></CreateForm>
    </Modal.Body>
    <Modal.Footer>
      <Button variant="secondary" onClick={handleClose}>
        Close
      </Button>
      <Button variant="primary" onClick={() => {
        handleSubmit(label, checkedUsers);
        } } >
        Create Document
      </Button>
    </Modal.Footer>
  </Modal>);

}