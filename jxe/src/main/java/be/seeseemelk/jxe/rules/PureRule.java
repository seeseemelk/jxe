package be.seeseemelk.jxe.rules;

import be.seeseemelk.jxe.ANSI;
import be.seeseemelk.jxe.codegen.JavaGenerator;
import be.seeseemelk.jxe.discovery.Flag;
import be.seeseemelk.jxe.discovery.Library;
import be.seeseemelk.jxe.types.DecompiledMethod;
import be.seeseemelk.jxe.types.VariableAccess.Action;

public class PureRule extends MethodRule
{
	@Override
	public void checkMethod(Library library, DecompiledMethod method)
	{
		if (!method.hasFlag(Flag.PURE))
			return;
		
		validateMethodReferences(library, method);
		validateFieldAccesses(library, method);
	}
	
	private void validateMethodReferences(Library library, DecompiledMethod method)
	{
		for (var ref : method.getMethodReferences())
		{
			var optional = library.findMethod(ref);
			if (optional.isEmpty())
			{
				throw new RuleCheckerMethodException(ref.getPosition(), method, "Missing reference to " + ref.toString() + ". Cannot validate pureness of method.",
						String.format("In order to validate the pureness of a methods, all invocations to other methods also have to be pure.%n"
								+ "However, some invocations could not be resolved, most likely because of a missing class file.%n"
								+ "Check that all class files are present and readable by the JXE Checker."));
			}
			
			var target = optional.get();
			if (!target.hasFlag(Flag.PURE))
			{
				String targetDefinition = JavaGenerator.generateFullMethodDefinition(target);
				throw new RuleCheckerMethodException(ref.getPosition(), method, "The pure method makes call to the unpure method " + ref.toString(),
						String.format("The pure method '%s' made a call to the unpure method '%s'.%n"
								+ "This is a problematic as a pure method can only call other pure methods, otherwise its pureness cannot be validated.%n"
								+ "%n"
								+ "You can attempt to resolve this by changing:%n"
								+ "   %s%n"
								+ "to:%n"
								+ "   %s %s%n"
								+ "in the class %s%n",
								ANSI.colourReference(method.toString()),
								ANSI.colourReference(target.toString()),
								ANSI.oldCode(targetDefinition),
								ANSI.code("@Pure"), ANSI.oldCode(targetDefinition),
								target.getOwner().toString()));
			}
		}
	}
	
	private void validateFieldAccesses(Library library, DecompiledMethod method)
	{
		for (var access : method.getFieldAccesses())
		{
			if (access.getAction() == Action.WRITE)
				throw new RuleCheckerMethodException(access.getPosition(), method, "Pure method writes to field " + access.getReference().toString(),
						String.format("The pure method writes to a field named %s.%n"
								+ "This is not allowed because a pure method cannot have any side effects.%n"
								+ "Writing to a field is a side effect.%n"
								+ "%n"
								+ "Either:%n"
								+ " - Remove %s from %s's definition%n"
								+ " - Remove the write to %s.%n"
								+ "%n"
								+ "Note that removing %s is probably not the right way to go about fixing this issue,%n"
								+ "you might want to consider refactoring your code so that no write to %s is necessary.",
								ANSI.colourReference(access.getReference().getPropertyName()),
								ANSI.code("@Pure"),
								ANSI.colourReference(method.getName()),
								ANSI.colourReference(access.getReference().getPropertyName()),
								ANSI.code("@Pure"),
								ANSI.colourReference(access.getReference().getPropertyName())));
		}
	}
}










