package be.seeseemelk.jtsc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import be.seeseemelk.jtsc.recompiler.Recompiler;

public class App
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("Error: incorrect arguments");
			System.err.printf("Arguments: <input directory> <output directory>%n");
			System.exit(1);
		}
		
		Path inputDirectory = Paths.get(args[0]);
		Path outputDirectory = Paths.get(args[1]);
		
		var recompiler = new Recompiler(outputDirectory);
		
		Files.walk(inputDirectory)
			.filter(Files::isRegularFile)
			.forEach(path ->
			{
				try
				{
					recompiler.recompile(Files.newInputStream(path));
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			});
	}
}
