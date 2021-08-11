package be.seeseemelk.jtsc.recompiler;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;

/**
 * Tracks the state of a method during decompilation. It is a vital part in
 * during Java-bytecode into functional D-code.
 */
public class MethodState
{
	private Deque<String> stack = new LinkedList<>();
	private int nextLocalVariable = 0;
	private final SourceWriter writer;
	private boolean isStatic = false;
	private boolean isConstructor = false;
	private Map<Label, String> labels = new HashMap<>();
	private int nextLabelId = 0;
	
	public MethodState(SourceWriter writer)
	{
		this.writer = writer;
	}
	
	/**
	 * Checks whether the stack is empty or not.
	 * 
	 * @return `true` if the stack is empty, `false` if the stack contains any
	 *         expressions.
	 */
	public boolean isStackEmpty()
	{
		return stack.isEmpty();
	}
	
	/**
	 * Pushes an expression to the stack.
	 * 
	 * @param expression The expression to push.
	 */
	public void pushToStack(String expression)
	{
		stack.push(expression);
	}
	
	/**
	 * Pops an expression from the stack.
	 * 
	 * @return The popped expression.
	 */
	public String popFromStack()
	{
		return stack.pop();
	}
	
	/**
	 * Pops several items from the stack at once.
	 * 
	 * @param count The number of items to pop from the stack.
	 * @return A list of all the items that were poped.
	 */
	public List<String> popFromStack(int count)
	{
		LinkedList<String> items = new LinkedList<>();
		for (int i = 0; i < count; i++)
		{
			items.push(popFromStack());
		}
		return items;
	}
	
	/**
	 * Gets the name of a variable by it's index into the local array of variables.
	 * 
	 * @param index The index of the variable.
	 * @return The name of the variable.
	 */
	public String getVariableName(int index)
	{
		return "var" + index;
	}
	
	/**
	 * Creates a new variable. Useful when the output of an expression has to be
	 * used twice.
	 * 
	 * @return The name of the new variable.
	 */
	public String createLocalVariable()
	{
		nextLocalVariable++;
		return "local" + (nextLocalVariable - 1);
	}
	
	/**
	 * Gets the writer which will generate the source code output.
	 * 
	 * @return The writer to use.
	 */
	public SourceWriter getWriter()
	{
		return writer;
	}
	
	/**
	 * Checks whether the method this object is describing is a static method or
	 * not.
	 * 
	 * @return `true` if the method is static, `false` if it is not.
	 */
	public boolean isMethodStatic()
	{
		return isStatic;
	}
	
	/**
	 * Sets whether the method this object is describing is a static method or not.
	 * 
	 * @param isStatic `true` if the method is a static method, `false` if it isn't.
	 */
	public void setMethodStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
	
	/**
	 * Chets whether the method this object is describing is a constructor or not.
	 * 
	 * @return `true` if the mergod is a constructor, `false` if it is not.
	 */
	public boolean isConstructor()
	{
		return isConstructor;
	}
	
	/**
	 * Sets whether the method is a constructor or not.
	 * 
	 * @param isConstructor `true` if the method is a constructor, `false` if it is not.
	 */
	public void setConstructor(boolean isConstructor)
	{
		this.isConstructor = isConstructor;
	}
	
	/**
	 * Gets the name of the label.
	 * @param label The label object.
	 * @return The name of the label.
	 */
	public String getLabelName(Label label)
	{
		if (labels.containsKey(label))
			return labels.get(label);
		
		String name = "label" + nextLabelId++;
		labels.put(label, name);
		return name;
	}
}
