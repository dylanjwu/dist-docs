import React from 'react';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Login from './auth/Login';
import Signup from './auth/Signup';
import Home from './main/Home';


export default function App() {
    return ( <
        div className = "App" >
            <BrowserRouter >
                <Routes >
                <Route exact path = "/" element = { < Home/> }/> 
                <Route exact path = "/login" element = { < Login/> }/> 
                <Route exact path = "/signup" element = { < Signup/> }/> 
                </Routes> 
            </BrowserRouter> 
        </div>
    );
}