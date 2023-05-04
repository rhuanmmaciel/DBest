package exceptions;

public class TreeException extends Exception{

	private static final long serialVersionUID = 3453003137947098627L;
	
	public TreeException() {
        super();
    }

    public TreeException(String mensagem) {
        super(mensagem);
    }

}
