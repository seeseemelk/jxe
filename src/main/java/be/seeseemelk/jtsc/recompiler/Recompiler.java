package be.seeseemelk.jtsc.recompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.objectweb.asm.ClassReader;

public class Recompiler
{
	private Path input;
	private Path output;

	public Recompiler(Path input, Path output)
	{
		this.input = input;
		this.output = output;
	}
	
	public void recompile() throws IOException
	{
		generateHeader();
		generateSource();
	}
	
	public String getClassName()
	{
		return input.getFileName().toString().replaceAll("\\.class$", "");
	}
	
	private void generateHeader() throws IOException
	{
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			try (var outputStream = new FileOutputStream(output.resolve(getClassName() + ".h").toFile()))
			{
				var reader = new ClassReader(inputStream);
				reader.accept(new RecompilerClassVisitor(outputStream, true), 0);
			}
		}
	}
	
	private void generateSource() throws IOException
	{
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			try (var outputStream = new FileOutputStream(output.resolve(getClassName() + ".c").toFile()))
			{
				var reader = new ClassReader(inputStream);
				reader.accept(new RecompilerClassVisitor(outputStream, false), 0);
			}
		}
	}

}




















