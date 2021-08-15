package be.seeseemelk.jtsc.recompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;

import be.seeseemelk.jtsc.instructions.Instruction;

public class InstructionStream
{
	private List<Instruction> instructions = new ArrayList<>();
	private Map<Label, Integer> labels = new HashMap<>();
	private int locals = 0;

	public void add(Instruction instruction)
	{
		instructions.add(instruction);
	}

	public void addLabel(Label label)
	{
		labels.put(label, instructions.size());
	}

	public int size()
	{
		return instructions.size();
	}

	public Instruction get(int index)
	{
		return instructions.get(index);
	}

	public int getIndex(Label label)
	{
		return labels.get(label);
	}

	public void setLocals(int locals)
	{
		this.locals = locals;
	}

	public int getLocals()
	{
		return locals;
	}
}
