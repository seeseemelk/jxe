package be.seeseemelk.jxe.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import be.seeseemelk.jxe.references.ClassReference;
import be.seeseemelk.jxe.references.FieldReference;
import be.seeseemelk.jxe.references.MethodReference;
import be.seeseemelk.jxe.rules.BaseRule;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledField;
import be.seeseemelk.jxe.types.DecompiledMethod;

public class Library
{
	private Set<DecompiledClass> classes = new HashSet<>();
	
	public void discover(Path input) throws IOException
	{
		Files.walk(input, FileVisitOption.FOLLOW_LINKS)
				.filter(Files::isRegularFile)
				.map(this::discoverClass)
				.forEach(optional -> optional.ifPresent(this::registerClassFromDiscoverer));
	}
	
	public void discover(String filename, InputStream input) throws IOException
	{
		discoverClass(filename, input).ifPresent(this::registerClassFromDiscoverer);
	}
	
	private Optional<ClassDiscoverer> discoverClass(Path input)
	{
		try
		{
			return Optional.of(ClassDiscoverer.discoverClass(input));
		}
		catch (IOException e)
		{
			return Optional.<ClassDiscoverer>empty();
		}
	}
	
	private Optional<ClassDiscoverer> discoverClass(String filename, InputStream input)
	{
		try
		{
			return Optional.of(ClassDiscoverer.discoverClass(filename, input));
		}
		catch (IOException e)
		{
			return Optional.<ClassDiscoverer>empty();
		}
	}
	
	private void registerClassFromDiscoverer(ClassDiscoverer discoverer)
	{
		discoverer.getDecompiledClass().ifPresent(classes::add);
	}
	
	public Set<DecompiledClass> getClasses()
	{
		return classes;
	}
	
	public void check(BaseRule rule)
	{
		classes.forEach(klass -> rule.checkClass(this, klass));
	}
	
	public Optional<DecompiledClass> findClass(ClassReference reference)
	{
		return classes.stream()
				.filter(klass -> klass.getFullyQualifiedName().equals(reference.getClassFQN()))
				.findAny();
	}
	
	public Optional<DecompiledMethod> findMethod(MethodReference reference)
	{
		return findClass(reference.getClassReference())
				.flatMap(klass -> 
					klass.getMethods().stream().filter(method -> method.getName().equals(reference.getMethodName())).findAny()
				);
	}
	
	public Optional<DecompiledField> findField(FieldReference reference)
	{
		return findClass(reference.getClassReference())
				.flatMap(klass ->
						klass.getFields().stream()
						.filter(field -> field.getName().equals(reference.getPropertyName()))
						.findAny());
	}
}









