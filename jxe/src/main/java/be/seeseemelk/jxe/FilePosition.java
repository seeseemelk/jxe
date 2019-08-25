package be.seeseemelk.jxe;

public class FilePosition
{
	private String file;
	private int lineNumber;
	
	public FilePosition(String file, int lineNumber)
	{
		this.file = file;
		this.lineNumber = lineNumber;
	}
	
	public String getFile()
	{
		return file;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s:%s", file, lineNumber);
	}
}
