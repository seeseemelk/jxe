
package be.seeseemelk.jxe.discovery;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import be.seeseemelk.jxe.ANSI;
import be.seeseemelk.jxe.ClassInfoStore;
import be.seeseemelk.jxe.references.MethodReference;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledMethod;
import be.seeseemelk.jxe.types.VariableAccess.Action;

public class MethodScanner
{
	//private Consumer<DecompiledMethod> consumer;
	private final Library library;
	private List<DecompiledMethod> methods;
	private List<DecompiledMethod> checkedMethods = new LinkedList<>();
	private List<DecompiledMethod> pureMethods = new LinkedList<>();
	
	public enum Result
	{
		ACCEPT, REJECT, IGNORE
	}
	
	public MethodScanner(Library library)
	{
		this.library = library;
		methods = library.getClasses().stream()
				.flatMap(klass -> klass.getMethods().stream())
				.collect(Collectors.toList());
		
		for (var method : methods)
		{
			for (var ref : method.getMethodReferences())
			{
				if (library.findMethod(ref).isEmpty())
				{
					System.err.println("Unresolved method " + ref.toString());
					return;
				}
			}
		}
		
		while (!methods.isEmpty())
		{
			var iterator = methods.iterator();
			while (iterator.hasNext())
			{
				var method = iterator.next();
				var result = check(method);
				
				if (result == Result.ACCEPT)
				{
					System.out.println(" + " + ANSI.GREEN + method.toString() + ANSI.RESET);
					pureMethods.add(method);
				}
				
				if (result == Result.REJECT || allReferencesChecked(method))
				{
					if (result != Result.ACCEPT)
						System.out.println(" - " + ANSI.RED + method.toString() + ANSI.RESET);
					checkedMethods.add(method);
					iterator.remove();
				}
			}
		}
		
		var store = new ClassInfoStore(Paths.get("data"));
		try
		{
			for (var methods : checkedMethods)
			{
				System.out.println(methods.toString());
				store.storeClass(methods.getOwner());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private Result check(DecompiledMethod method)
	{
		// Check method references
		for (var ref : method.getMethodReferences())
		{
			if (!isPure(ref))
			{
				return Result.IGNORE;
			}
		}
		
		// Check field accesses
		for (var access : method.getFieldAccesses())
		{
			if (access.getAction() == Action.WRITE)
			{
				var field = library.findField(access.getReference());
				if (field.isEmpty() || !field.get().isFinal())
				{
					return Result.REJECT;
				}
			}
		}
		
		return Result.ACCEPT;
	}
	
	private boolean allReferencesChecked(DecompiledMethod method)
	{
		return method.getMethodReferences().stream()
				.allMatch(this::wasChecked);
	}
	
	private boolean isPure(MethodReference target)
	{
		return pureMethods.stream()
				.filter(method -> method.getName().equals(target.getMethodName()))
				.map(DecompiledMethod::getOwner)
				.anyMatch(klass -> klass.getFullyQualifiedName().equals(target.getClassReference().getClassFQN()));
	}
	
	private boolean wasChecked(MethodReference target)
	{
		return checkedMethods.stream()
				.filter(method -> method.getName().equals(target.getMethodName()))
				.map(DecompiledMethod::getOwner)
				.anyMatch(klass -> klass.getFullyQualifiedName().equals(target.getClassReference().getClassFQN()));
	}
}






