package unipay.exception.names;

public class ReferenceName extends Name {

	public static final ReferenceName Intent = new ReferenceName("Intent");
	public static final ReferenceName Params = new ReferenceName("Params");
	public static final ReferenceName Credentials = new ReferenceName("Credentials");
	public static final ReferenceName Defaults = new ReferenceName("Defaults");
	public static final ReferenceName ResultCode = new ReferenceName("ResultCode");

	protected ReferenceName(String value) {
		super(value);
	}
}
