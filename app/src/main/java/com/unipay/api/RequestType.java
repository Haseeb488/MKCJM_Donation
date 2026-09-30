package unipay.api;

public enum RequestType {
	Sale("sale"),
	SaleAuth("sale-auth"),
	Capture("capture"),
	Credit("credit"),
	Void("void"),
	Refund("refund"),
	Tokenization("tokenization"),
	CloseCycle("close-cycle"),
	ClearBatch("clear-batch");

	private String value;

	RequestType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
