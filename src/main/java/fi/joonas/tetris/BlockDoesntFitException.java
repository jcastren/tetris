package fi.joonas.tetris;

public class BlockDoesntFitException extends Exception {

	private static final long serialVersionUID = 5858093680185919511L;

	public BlockDoesntFitException(String s) {
		super(s);
	}

}
