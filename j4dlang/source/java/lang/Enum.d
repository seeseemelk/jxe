module java.lang.Enum;

import java.lang.Object;
import java.lang.String;
import java.lang.Class;

import std.exception;

/**
A base class used by all Enums.
*/
abstract class Enum : _Object
{
    mixin autoReflector!Enum;

    this(String name, int ordinal)
    {
        // TODO
    }

    /**
    Converts a string to an enum.
    */
    static Enum valueOf(Class enumerator, String value)
    {
        throw new Exception("Not implemented");
    }
}
