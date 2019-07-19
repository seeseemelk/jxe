package be.seeseemelk.jxe.recompiler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;

public class Recompiler
{
	private Path input;
	private Path outputDirectory;
	//private List<Pair<Path, List<ClassImport>>> filesToCompile = new ArrayList<>();
	private List<Path> sourceFiles = new ArrayList<>();

	public Recompiler(Path input, Path outputDirectory)
	{
		this.input = input;
		this.outputDirectory = outputDirectory;
	}
	
	public void recompile() throws IOException
	{
		Files.createDirectories(outputDirectory);
		recompilePath(Path.of("jxelib"));
		recompilePath(input);
	}
	
	private void recompilePath(Path input) throws IOException
	{
		if (Files.isRegularFile(input))
			generate(input);
		else
		{
			try (var stream = Files.walk(input))
			{
				stream.filter(Files::isRegularFile)
					.filter(file -> file.toString().endsWith(".class"))
					.forEach(file -> {
						try
						{
							generate(file);
						}
						catch (IOException e)
						{
							System.err.printf("Failed to process file '%s', reason: %s", file.toString(), e.getMessage());
							e.printStackTrace();
						}
					});
			}
		}
		copyResources(outputDirectory);
		generateMakefile();
	}
	
	public String getClassName()
	{
		return input.getFileName().toString().replaceAll("\\.class$", "");
	}
	
	private void generate(Path input) throws IOException
	{
		System.out.printf("CLASS %s%n", input.toString());
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			var reader = new ClassReader(inputStream);
			var firstPass = new FirstPassClassVisitor(input, outputDirectory, sourceFiles::add);
			reader.accept(firstPass, 0);
		}
	}
	
	private void generateMakefile() throws IOException
	{
		try (var output = new PrintStream(outputDirectory.resolve("makefile").toFile()))
		{
			output.println("all: program");
			output.println();
			output.print("program:");
			
			var builder = new StringBuilder();
			for (var sourceFile : sourceFiles)
			{
				var sourceFilename = outputDirectory.relativize(sourceFile).toString().replace(".cpp", "");
				builder.append(' ').append(sourceFilename).append(".o");
			}
			var files = builder.toString();
			output.println(files);
			output.printf("\tg++ ${CPPFLAGS} -I. -o program%s", files);
			output.println();
			output.println();
				
			for (var sourceFile : sourceFiles)
			{
				var sourceFilename = outputDirectory.relativize(sourceFile).toString().replace(".cpp", "");
				output.printf("%s.o: %s.cpp%n", sourceFilename, sourceFilename);
				output.printf("\tg++ ${CPPFLAGS} -I. -c -o %s.o %s.cpp%n%n", sourceFilename, sourceFilename);
			}
		}
	}
	
	/*private void generateHeader() throws IOException
	{
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			try (var outputStream = new FileOutputStream(output.resolve(getClassName() + ".hpp").toFile()))
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
			try (var outputStream = new FileOutputStream(output.resolve(getClassName() + ".cpp").toFile()))
			{
				var reader = new ClassReader(inputStream);
				reader.accept(new RecompilerClassVisitor(outputStream, false), 0);
			}
		}
	}*/

	private void copyResources(Path dir) throws IOException
	{
		copyResource("jtsc_core.cpp", dir);
		copyResource("jtsc_core.hpp", dir);
	}
	
	private void copyResource(String name, Path dir) throws IOException
	{
		Files.copy(ClassLoader.getSystemResourceAsStream("common/" + name), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
	}
}




















