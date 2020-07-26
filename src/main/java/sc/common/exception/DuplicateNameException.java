package sc.common.exception;

public class DuplicateNameException extends RuntimeException {

	private static final long serialVersionUID = 1882153240006350935L;
	
	public DuplicateNameException() {
		super();
	}
	
	public DuplicateNameException(String message) {
		super(message);
	}
}
