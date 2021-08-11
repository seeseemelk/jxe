package be.seeseemelk.jtsc.recompiler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.objectweb.asm.ClassReader;

public class Recompiler
{
	private final Path outputDirectory;
	private final Path sourceDirectory;
	private final Instrumentation instrumentation;
	
	public Recompiler(Path outputDirectory, Instrumentation instrumentation) throws IOException
	{
		this.outputDirectory = outputDirectory;
		this.sourceDirectory = outputDirectory.resolve("source");
		this.instrumentation = instrumentation;
		Files.createDirectories(outputDirectory);
		Files.createDirectories(sourceDirectory);
		createBuildScript("some_project");
	}
	
	public void recompile(InputStream input) throws IOException
	{
		var reader = new ClassReader(input);
		
		var imports = new ImportFinder();
		reader.accept(imports, ClassReader.SKIP_DEBUG);
		
		var firstPass = new DClassVisitor(sourceDirectory, imports.getImports(), instrumentation);
		reader.accept(firstPass, ClassReader.SKIP_DEBUG);
	}
	
	private void createBuildScript(String projectName) throws IOException
	{
		try (var writer = Files.newBufferedWriter(outputDirectory.resolve("dub.sdl"), StandardOpenOption.CREATE))
		{
			writer.append("name \"" + projectName + "\"\n");
			writer.append("dependency \"j4dlang\" version=\"*\"\n");
		}
	}
}
