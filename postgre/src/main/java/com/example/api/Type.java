package com.example.api;

import java.io.Serializable;

/**
 Enumeration class Type representing the type of operation in the task.
 The possible operations are:
 PUT: to store a key-value pair in the database
 GET: to retrieve the value of a key
 DELETE: to delete a key-value pair from the database
 */
public enum Type  implements Serializable {
    CREATE, READ, DELETE, UPDATE
}
