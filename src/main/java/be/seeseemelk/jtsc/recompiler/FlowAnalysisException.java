package be.seeseemelk.jtsc.recompiler;

public class FlowAnalysisException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public FlowAnalysisException()
	{
	}

	public FlowAnalysisException(String message)
	{
		super(message);
	}

	public FlowAnalysisException(Throwable cause)
	{
		super(cause);
	}

	public FlowAnalysisException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FlowAnalysisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
