package exceptions;

public class MissingTableException extends Exception{

	private static final long serialVersionUID = 7427753327861776295L;
	
	public MissingTableException() {
        super();
    }

    public MissingTableException(String mensagem) {
        super(mensagem);
    }

}
