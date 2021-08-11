package be.seeseemelk.jtsc;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import be.seeseemelk.jtsc.recompiler.Instrumentation;
import be.seeseemelk.jtsc.recompiler.Recompiler;

public class App
{
	public static void main(String[] args) throws IOException
	{
		parseArgs(args).ifPresent(options ->
		{
			try
			{
				Path inputDirectory = Paths.get(options.input);
				Path outputDirectory = Paths.get(options.output);
	
				var recompiler = new Recompiler(outputDirectory, options.instrumentation);
	
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
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	private static void printHelp(PrintStream output)
	{
		output.println("Usage: java -jar <jarFile> [OPTION...] INPUTDIR OUTPUTDIR");
		output.println("Supported options:");
		output.println("  --help                 Show this help file");
		output.println("  --only-instrumented    Generate only instrumented code");
		output.println("  --only-native          Generate only native code");
	}
	
	private static Optional<Options> parseArgs(String[] args)
	{
		Options options = new Options();
		ParseState state = ParseState.NORMAL;
		for (int i = 0; i < args.length; i++)
		{
			if (state == ParseState.NORMAL)
			{
				switch (args[i])
				{
					case "--help":
						printHelp(System.out);
						return Optional.empty();
					case "--only-instrumented":
						options.instrumentation = Instrumentation.INSTRUMENTED;
						break;
					case "--only-native":
						options.instrumentation = Instrumentation.NATIVE;
						break;
					default:
						state = ParseState.INPUT;
				}
			}
			if (state == ParseState.INPUT)
			{
				options.input = args[i];
				state = ParseState.OUTPUT;
			}
			else if (state == ParseState.OUTPUT)
			{
				options.output = args[i];
				return Optional.of(options);
			}
		}
		System.err.println("Missing arguments");
		printHelp(System.err);
		System.exit(1);
		return Optional.empty();
	}
	
	private static class Options
	{
		Instrumentation instrumentation = Instrumentation.BOTH;
		String input;
		String output;
	}
	
	private enum ParseState
	{
		NORMAL,
		INPUT,
		OUTPUT
	}
}
