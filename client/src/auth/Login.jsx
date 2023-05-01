import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';

const SERVER_HOST = 'http://localhost:8080';

export default function Login() {
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem('user_id'))  {
      navigate('/');
    }
  }, []);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  function handleUsernameChange(event) {
    setUsername(event.target.value);
  }

  function handlePasswordChange(event) {
    setPassword(event.target.value);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const {data} = await axios.post(`${SERVER_HOST}/user/login`, { username, password });

    if (data !== null) {
      localStorage.setItem('user_id', data.id);
      navigate('/');
    }
  }


  return (
    <Container>
      <Row className="justify-content-center mt-5">
        <Col xs={12} md={6}>
          <h1>Login</h1>
          <Form onSubmit={handleSubmit}>
            <Form.Group controlId="formBasicEmail">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="username"
                placeholder="Enter username"
                value={username}
                onChange={handleUsernameChange}
              />
            </Form.Group>

            <Form.Group controlId="formBasicPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={handlePasswordChange}
              />
            </Form.Group>

            <Button variant="primary" type="submit">
              Login
            </Button>

            <p>Don't have an account? Sign up <Link to='/signup'> <span> here </span> </Link></p>
          </Form>
        </Col>
      </Row>
    </Container>
  );
}

