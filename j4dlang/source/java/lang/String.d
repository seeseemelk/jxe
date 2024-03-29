module java.lang.String;

import java.lang.Object;

import std.algorithm;
import std.array;

final class String : _Object
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

	static Array!String fromArray(string[] args)
	{
		String[] arr = args
			.map!(arg => new String(arg))
			.array();
		return new Array!String(arr);
	}
}
