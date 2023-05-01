import React from 'react';
import '../App.css';

function FileInput(props) {
  const { currDoc, setDoc } = props;



  return (
    <div className="textbox-container">
      {/* <label>Enter Text:</label> */}
      <textarea
        className="textbox"
        type="text"
        value={currDoc?.contents || ''}
        onChange={(e) => setDoc({ ...currDoc, contents: e.target.value} )}
        />
      
    </div>
  );
}

export default FileInput;