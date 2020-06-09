module java.lang.String;

import java.lang.Object;

import std.algorithm;
import std.array;

class String : _Object
{
    mixin autoReflector!String;

    private string str;

    this(string str)
    {
        this.str = str;
    }

    string getDString()
    {
        return str;
    }

    static String[] fromArray(string[] args)
    {
        return args
            .map!(arg => new String(arg))
            .array();
    }
}
