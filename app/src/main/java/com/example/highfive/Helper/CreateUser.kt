package com.example.highfive.Helper

import com.example.highfive.Models.Connection
import com.example.highfive.Models.User
import com.google.firebase.database.DataSnapshot

class CreateUser {
    companion object {
        fun createUser(p0: DataSnapshot, userId: String): User {
            var connectionsArray = getConnections(p0)
            return User(
                userId,
                p0.child("firstName").value.toString(),
                p0.child("lastName").value.toString(),
                p0.child("email").value.toString(),
                p0.child("phone").value.toString(),
                p0.child("website").value.toString(),
                p0.child("company").value.toString(),
                p0.child("jobTitle").value.toString(),
                p0.child("picture").value.toString(),
                connectionsArray
            )
        }

        fun createConnection(connection: DataSnapshot): Connection {
            return Connection(
                connection.child("userId").value.toString(),
                connection.child("name").value.toString(),
                connection.child("picture").value.toString(),
                connection.child("company").value.toString(),
                connection.child("phone").value.toString(),
                connection.child("email").value.toString(),
                connection.child("note").value.toString()
            )
        }

        fun getConnections(p0: DataSnapshot): ArrayList<Connection>{
            var connectionsArray: ArrayList<Connection> = ArrayList()
            if (p0.hasChild("connections")) {
                connectionsArray.clear()
                for (connection in p0.child("connections").children) {
                    var tempConnection = createConnection(connection)
                    connectionsArray.add(tempConnection)
                }
            }
            return connectionsArray
        }

        fun createConnection(user: User): Connection{
            return Connection(
                user.userId,
                user.firstName + " " + user.lastName,
                user.picture ?: "",
                user.company ?: "",
                user.phone,
                user.email,
                ""
            )
        }

        fun createConnection(user: User, note: String): Connection{
            return Connection(
                user.userId,
                user.firstName + " " + user.lastName,
                user.picture ?: "",
                user.company ?: "",
                user.phone,
                user.email,
                note
            )
        }
    }
}