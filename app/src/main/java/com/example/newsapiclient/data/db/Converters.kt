package com.example.newsapiclient.data.db

import androidx.room.TypeConverter
import com.example.newsapiclient.data.model.Source

//Provide this type converter class in ArticleDatabase inside @TypeConverter annotation.
class Converters {

    //Type converters are used when we have an object of a class as a member of data class which is a room entity. Here Source class
    //has two members. But we need only one to store in the database. So whenever room receives a source object, with the help of this
    //converter, room will insert only name as string in the table. ID is omitted. Similarly when room has to return values, it will send4
    //values according to the type converter function we define i.e. toSource() here which returns Source object.

    @TypeConverter
    fun fromSource(source: Source): String? {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}