package sexpr;

public class ParseException extends Exception {
	public ParseException(java.lang.String s, Object ... objs) {
		super(java.lang.String.format(s, objs));
	}
}
