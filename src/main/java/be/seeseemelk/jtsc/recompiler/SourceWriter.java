package be.seeseemelk.jtsc.recompiler;

import java.io.IOException;
import java.io.Writer;

public class SourceWriter
{
	private Writer writer;
	private int indentation = 0;
	
	public SourceWriter(Writer writer)
	{
		this.writer = writer;
	}
	
	public void write(Object... str) throws IOException
	{
		for (int i = 0; i < indentation; i++)
			writer.write("\t");
		for (Object obj : str)
			writer.write(obj.toString());
	}
	
	public void writeUnsafe(Object... str)
	{
		try
		{
			write(str);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void writeln(Object... str) throws IOException
	{
		write(str);
		writer.write("\n");
	}
	
	public void writelnUnsafe(Object... str)
	{
		try
		{
			writeln(str);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void indent()
	{
		indentation++;
	}
	
	public void undent()
	{
		indentation--;
	}
	
	public void close() throws IOException
	{
		writer.close();
	}
}
