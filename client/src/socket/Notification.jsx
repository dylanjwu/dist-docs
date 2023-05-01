import React from 'react';

const Notification = ({ messages }) => {
    // const [messages, setMessages] = useState(['messages']);

    // useEffect(() => {
    //     const ws = new WebSocket(`ws://localhost:3005/listen_to_changes/${userId}`);

    //     ws.onopen = () => {
    //         console.log('WebSocket connection opened');
    //     };

    //     ws.onmessage = (event) => {
    //         const message = event.data;
    //         console.log(message);
    //         const m = "message"
    //         setMessages([...messages, m]);
    //     };

    //     ws.onclose = () => {
    //         console.log('WebSocket connection closed');
    //     };

    //     return () => {
    //         ws.close();
    //     };
    // }, [userId]);

    return (
        <div>
            <h2>Notifications</h2>
            <ul>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
            </ul>
        </div>
    );
};
export default Notification;
