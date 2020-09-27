module java.lang.Integer;

import java.lang.Object;

class Integer : _Object
{
    mixin autoReflector!Integer;

    private const int value;

    this(int value)
    {
        this.value = value;
    }

    static Integer valueOf(int value)
    {
        return new Integer(value);
    }

    static Integer valueOf(Integer integer)
    {
        return new Integer(integer.value);
    }

    int intValue()
    {
        return value;
    }
}
