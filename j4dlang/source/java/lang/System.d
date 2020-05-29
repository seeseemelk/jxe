module java.lang.System;

import java.lang.String;

class System
{
    static StandardOutputStream _out;

    static this()
    {
        _out = new StandardOutputStream;
    }
}

private class StandardOutputStream
{
    void println(String text)
    {
        import std.stdio : writeln;
        writeln(text.getDString());
    }
}
