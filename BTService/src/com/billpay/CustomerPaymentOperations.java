package com.billpay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;

public class CustomerPaymentOperations {

	private static String MerchantID = "pds8jy3923jg899g";
	private static String PublicKey  = "y8pgk7d9ybcfv8ps";
	private static String PrivateKey = "fbbfb6311c6761631f0562dbba66e5f8";
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.SANDBOX,
			MerchantID,
			PublicKey,
			PrivateKey);

	static String customerID="F4";
	static String PMT="97vsss";
	static String transid="ggc1y81a";
	static String nonce="fake-valid-nonce";
	static String Settle="N";
	static String Amount="30.01";
	
	static String PartialAmount="02.00";
	public static void main(String args[]){

		System.out.println(gateway);
		System.setProperty("https.protocols", "TLSv1.2");
		System.out.println("https.protocols"+System.getProperty("https.protocols"));
		//System.setProperty("proxySet","true");
		//System.setProperty("proxyHost", "10.136.64.150");
		//System.setProperty("proxyPort", "80");

		try {

			if(gateway!=null){

				/*String token = gateway.clientToken().generate();
				System.out.println("token="+token);*/
				ClientTokenRequest clientTokenRequest = new ClientTokenRequest().merchantAccountId(MerchantID);
				String clientToken = gateway.clientToken().generate(clientTokenRequest);
				System.out.println("clientToken="+clientToken);

				TransactionRequest request = null;
				//Payload for instant settlement
				if(Settle.equals("Y")){
					request = new TransactionRequest()
							.amount(new BigDecimal(Amount))
							.customerId(customerID)
							//.amount(request.queryParams("amount"))
							//.paymentMethodNonce(request.queryParams("paymentMethodNonce"))
							.options()
							.submitForSettlement(true)
							// .storeInVaultOnSuccess(true)
							.done();
					/*.amount(new BigDecimal(Amount))
							.customerId(customerID)
							.paymentMethodToken(PMT)
							.options()
							.submitForSettlement(true)
							.done();*/
				}
				//Payload for only authorization
				else{
					request = new TransactionRequest()
							.amount(new BigDecimal(Amount))
							.customerId(customerID)
							//.paymentMethodToken(PMT)
							.deviceData("deviceData")
							.fraudMerchantId(MerchantID)
							.deviceSessionId("deviceSessionId")
							.merchantAccountId("infosys")//required if multiple merchant accounts exists
							/*.descriptor()
							.name("name")
							.done()
							*/.orderId("orderId") //invoice number
							.purchaseOrderNumber("pOrderNum")
							.transactionSource("merchant")
							.options()
							.submitForSettlement(false)
							.done()
							//.recurring(true)
							//.skipAdvancedFraudChecking(true) //can skip fraud check
							//.lineItem() // this can include product details/description/productCode/Amount
							;





				}
				//payment via customerID : Settle 'N' = Authorize
				CustomerPayment(request);
				//payment via PMT
				//Advice(transid);
				//PartialAdvice(transid);
				//Void(transid);
				//Refund(transid);
				//findTransaction(customerID,transid);

			}
			else
			{
				System.out.println("Couldnt connect to Gateway");
			}

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public  static void CustomerPayment(TransactionRequest request) {


		Result<Transaction> result = gateway.transaction().sale(request);
		try{
			if(result !=null){
				if(	result.isSuccess()==true){
					System.out.println(result.getTransaction()+","+result.getTarget());
					System.out.println("Payment for customerID:");
					resultSuccess(result);
					transid=result.getTarget().getId();
					findTransaction(transid);
				}
				else
				{
					System.out.println("Couldnt perform customer payment");
					resultError(result);
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  static void Advice (String transid){

		Result<Transaction> result = gateway.transaction().submitForSettlement(
				transid, new BigDecimal(Amount)
				);
		if (result.isSuccess()) {
			// transaction successful
			System.out.println("Advice for customerID:");
			resultSuccess(result);
			findTransaction(transid);

		} else {
			System.out.println("Couldnt perform Advice");
			resultError(result);
		}
	}

	public  static void PartialAdvice (String transid){
		Result<Transaction> result = gateway.transaction().submitForPartialSettlement(
				transid, new BigDecimal(PartialAmount)
				);
		if (result.isSuccess()) {
			// transaction successfully voided
			System.out.println("PartialAdvice for customerID:");
			resultSuccess(result);
			findTransaction(transid);
		} else {
			System.out.println("Couldnt perform Partial Advice");
			resultError(result);

		}
	}

	public  static void Void (String transid){

		Result<Transaction> result = gateway.transaction().voidTransaction(transid);

		if (result.isSuccess()) {
			// transaction successfully voided

			System.out.println("Void for customerID:");
			resultSuccess(result);
			findTransaction(transid);
		} else {
			System.out.println("Couldnt perform Void");
			resultError(result);
		}
	}

	public  static void Refund (String transid){
		Result<Transaction> result = gateway.transaction().refund(transid);

		if (result.isSuccess()) {
			// transaction successfully voided
			System.out.println("Refund for customerID:");
			resultSuccess(result);
			findTransaction(transid);
		} else {
			System.out.println("Couldnt perform Refund");
			resultError(result);
		}
	}
	public  static void findTransaction(String transid){

		Transaction result = gateway.transaction().find(transid);
		System.out.println("findTransaction="+result.getId()+","+result.getStatus()+","
				+result.getAmount()+","
				+result.getPaymentInstrumentType()+","
				+result.getMerchantAccountId()+","
				+result.getOrderId()+","
				+result.getPurchaseOrderNumber()+","
				+result.getRefundedTransactionId()
				+result.getCvvResponseCode()+","
				+result.getProcessorAuthorizationCode()+","
				+result.getProcessorResponseCode()+","
				+result.getProcessorResponseText()+","
				+result.getProcessorSettlementResponseCode()+","
				+result.getProcessorSettlementResponseText()+","
				+result.getAdditionalProcessorResponse()+","
				+result.getCreditCard().getMaskedNumber()+","
				+result.getCreditCard().getUniqueNumberIdentifier()+","
				+result.getCreditCard().getExpirationDate()+","
				+result.getCreditCard().getExpirationMonth()+","
				+result.getCreditCard().getToken()+","
				+result.getCreditCard().getCardType()+","
				+result.getCreditCard().getCardholderName()+","
				+result.getSettlementBatchId()+","
				+dateformat.format(result.getUpdatedAt().getTime()).trim()+","
				+dateformat.format(result.getCreatedAt().getTime()).trim()
				);

		/*TransactionSearchRequest search= new TransactionSearchRequest()
				.customerId().is(customerID);*/
		/*ResourceCollection<Transaction> collection = gateway.transaction().search(search);

		for (Transaction result1 : collection) {
			  System.out.println("findTransaction-Collection="+result1.getId()+","
			  			+result1.getAmount()+","
						+result1.getPaymentInstrumentType()+","
						+result1.getEscrowStatus()+","
						+result1.getCreditCard().getMaskedNumber()+","
						+result1.getCreditCard().getUniqueNumberIdentifier()+","
						+result1.getCreditCard().getExpirationDate()+","
						+result1.getCreditCard().getToken()+","
						+result1.getCreditCard().getCardType()+","
						+result1.getCreditCard().isExpired());
			}*/
	}

	public static void resultSuccess(Result<Transaction> result){

		System.out.println(customerID
				+","//+result.getTransaction().getId()
				+result.getTarget().getId()+","
				/*+result.getTarget().getOrderId()+","
				+result.getTarget().getChannel()+","
				+result.getTarget().getPlanId()+","
				+result.getTarget().getPurchaseOrderNumber()+","*/
				+result.getTarget().getCreditCard().getMaskedNumber()+","
				+result.getTarget().getCreditCard().getExpirationDate()+","
				+result.getTarget().getCreditCard().getToken()+","
				+result.getTarget().getCreditCard().getUniqueNumberIdentifier()+","
				+result.getTarget().getStatus()+","
				+result.getTarget().getPaymentInstrumentType()+","
				+result.getTarget().getType()+","
				+result.getTarget().getAuthorizedTransactionId()+","
				+result.getTarget().getProcessorAuthorizationCode()+","
				+result.getTarget().getProcessorResponseCode()+","
				+result.getTarget().getProcessorResponseText()+","
				+result.getTarget().getProcessorSettlementResponseCode()+","
				+result.getTarget().getProcessorSettlementResponseText()+","
				+result.getTarget().getEscrowStatus()
				);
	}
	public static void resultError(Result<Transaction> result){
		if(result.getErrors()!=null){
			System.out.println("Errors Count="+result.getErrors().size()+
					",DeepSize="+result.getErrors().deepSize());
			for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
				System.out.println("DeepValidationErrors="+error.getCode()+","+error.getAttribute()+","+error.getMessage());
			}
			for (ValidationError error : result.getErrors().getAllValidationErrors()) {
				System.out.println("ValidationErrors="+error.getCode()+","+error.getAttribute()+","+error.getMessage());
			}
		}
	}
	/*public static String createPMN(String PMT){
	Result<PaymentMethodNonce> result = gateway.paymentMethodNonce().create(PMT);
	String nonce = result.getTarget().getNonce();
	System.out.println("nonce="+nonce);
	return nonce;
}*/
	/*public static void createCustomer(){
	CustomerRequest request = new CustomerRequest()
			.firstName("Ash")
			.paymentMethodNonce("fake-valid-nonce")
			;
	Result<Customer> result = gateway.customer().create(request);

	if(result.isSuccess()){
		result.getTarget().getId();
		System.out.println(result.getTarget().getId());
	}
	else
		System.out.println("Couldnt Create customer");
}*/
}
