package com.unipay.api.fields;

/**
 * @author UniDoc generator
 * @company UnitedThinkers
 * @since 20 Aug, 2017
 */

public final class TerminalFieldName {

	public final FieldName AccountId = new FieldName("accountId", "Account Id", "merchantAccountCode");
	public final FieldName AccountType = new FieldName("accountType", "Account Type");
	public final FieldName Amount = new FieldName("amount", "Amount");
	public final FieldName Button1 = new FieldName("button1", "Button 1");
	public final FieldName Button2 = new FieldName("button2", "Button 2");
	public final FieldName ButtonCancel = new FieldName("buttonCancel", "Button Cancel");
	public final FieldName ButtonClear = new FieldName("buttonClear", "Button Clear");
	public final FieldName ButtonOk = new FieldName("buttonOk", "Button Ok");
	public final FieldName CashbackAmount = new FieldName("cashbackAmount", "Cashback Amount");
	public final FieldName Code = new FieldName("code", "Code");
	public final FieldName Content = new FieldName("content", "Content");
	public final FieldName CustomerAccountCode = new FieldName("customerAccountCode", "Customer Account Code");
	public final FieldName CustomerAccountInternalCode = new FieldName("customerAccountInternalCode", "Customer Account Internal Code");
	public final FieldName Description = new FieldName("description", "Description");
	public final FieldName Descriptor = new FieldName("descriptor", "Descriptor");
	public final FieldName DiscountAmount = new FieldName("discountAmount", "Discount Amount");
	public final FieldName DiscountRate = new FieldName("discountRate", "Discount Rate");
	public final FieldName Email = new FieldName("email", "Email");
	public final FieldName HolderName = new FieldName("holderName", "Holder Name");
	public final FieldName HolderVerificationModeType = new FieldName("holderVerificationModeType", "Holder Verification Mode Type");
	public final FieldName IsCredit = new FieldName("isCredit", "Is Credit");
	public final FieldName ItemCode = new FieldName("itemCode", "Item Code");
	public final FieldName ItemId = new FieldName("itemId", "Item Id");
	public final FieldName ItemNumber = new FieldName("itemNumber", "Item Number");
	public final FieldName LaneCode = new FieldName("laneCode", "Lane Code");
	public final FieldName Memo = new FieldName("memo", "Memo");
	public final FieldName OriginalTransactionCode = new FieldName("originalTransactionCode", "Original Transaction Code");
	public final FieldName PartialAuthorizationPolicy = new FieldName("partialAuthorizationPolicy", "Partial Authorization Policy");
	public final FieldName Password = new FieldName("password", "Password");
	public final FieldName ProcessingMode = new FieldName("processingMode", "Processing Mode");
	public final FieldName Quantity = new FieldName("quantity", "Quantity");
	public final FieldName ReceiptMode = new FieldName("receiptMode", "Receipt Mode");
	public final FieldName RequestType = new FieldName("requestType", "Request Type");
	public final FieldName SettlementGroupCode = new FieldName("settlementGroupCode", "Settlement Group Code");
	public final FieldName SplitTransactionSequenceNumber = new FieldName("splitTransactionSequenceNumber", "Split Transaction Sequence Number");
	public final FieldName TaxAmount = new FieldName("taxAmount", "Tax Amount");
	public final FieldName TaxRate = new FieldName("taxRate", "Tax Rate");
	public final FieldName TerminalId = new FieldName("terminalId", "Terminal Id", "terminalCode");
	public final FieldName TerminalMessage = new FieldName("terminalMessage", "Terminal Message");
	public final FieldName TicketId = new FieldName("ticketId", "Ticket Id");
	public final FieldName TicketNumber = new FieldName("ticketNumber", "Ticket Number");
	public final FieldName Timeout = new FieldName("timeout", "Timeout");
	public final FieldName TipAmount = new FieldName("tipAmount", "Tip Amount");
	public final FieldName TipRecipientCode = new FieldName("tipRecipientCode", "Tip Recipient Code");
	public final FieldName Title = new FieldName("title", "Title");
	public final FieldName TotalAmount = new FieldName("totalAmount", "Total Amount");
	public final FieldName TransactionCode = new FieldName("transactionCode", "Transaction Code");
	public final FieldName TransactionDate = new FieldName("transactionDate", "Transaction Date");
	public final FieldName TransactionId = new FieldName("transactionId", "Transaction Id", "referenceNumber");
	public final FieldName TransactionIndustryType = new FieldName("transactionIndustryType", "Transaction Industry Type");
	public final FieldName TransactionInternalCode = new FieldName("transactionInternalCode", "Transaction Internal Code");
	public final FieldName TransactionOriginCode = new FieldName("transactionOriginCode", "Transaction Origin Code");
	public final FieldName UnitCostAmount = new FieldName("unitCostAmount", "Unit Cost Amount");
	public final FieldName UnitMeasure = new FieldName("unitMeasure", "Unit Measure");
	public final FieldName UserCode = new FieldName("userCode", "User Code");
	public final FieldName UserName = new FieldName("userName", "User Name");
	public final FieldName VoidReasonCode = new FieldName("voidReasonCode", "Void Reason Code");
	public final FieldName MerchantAccountCode = new FieldName("merchantAccountCode", "Merchant Account Code");
	public final FieldName ReferenceNumber = new FieldName("referenceNumber", "Reference Number");
	public final FieldName TerminalCode = new FieldName("terminalCode", "Terminal Code");

	public final Terminal Terminal = new Terminal();

	TerminalFieldName() {
	}

	public static final class Terminal {
		public final FieldName Code = new FieldName("code", "Code");
		public final FieldName Description = new FieldName("description", "Description");
		public final FieldName DiscountAmount = new FieldName("discountAmount", "Discount Amount");
		public final FieldName DiscountRate = new FieldName("discountRate", "Discount Rate");
		public final FieldName IsCredit = new FieldName("isCredit", "Is Credit");
		public final FieldName ItemNumber = new FieldName("itemNumber", "Item Number");
		public final FieldName Quantity = new FieldName("quantity", "Quantity");
		public final FieldName TaxAmount = new FieldName("taxAmount", "Tax Amount");
		public final FieldName TaxRate = new FieldName("taxRate", "Tax Rate");
		public final FieldName TotalAmount = new FieldName("totalAmount", "Total Amount");
		public final FieldName UnitCostAmount = new FieldName("unitCostAmount", "Unit Cost Amount");
		public final FieldName UnitMeasure = new FieldName("unitMeasure", "Unit Measure");

		private Terminal() {
		}
	}

}
