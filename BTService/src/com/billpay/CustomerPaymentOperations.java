package com.billpay;

import java.math.BigDecimal;
import java.util.ListIterator;

import com.braintreegateway.BinData;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodNonce;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.ResourceCollection;
import com.braintreegateway.Result;
import com.braintreegateway.ThreeDSecureInfo;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.TransactionSearchRequest;

public class CustomerPaymentOperations {

	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.SANDBOX,
			"pds8jy3923jg899g",
			"y8pgk7d9ybcfv8ps",
			"fbbfb6311c6761631f0562dbba66e5f8"
			);
	static String customerID="T99";
	static String PMT="3tbs7s";
	static String transid="avsktgmd";
	static String merchantAccountId="pds8jy3923jg899g";
	static String nonce="fake-valid-nonce";
	public static void main(String args[]){

		System.out.println(Environment.SANDBOX);
		System.out.println((Environment.SANDBOX).certificateFilenames[0]);;
		System.out.println(gateway);
		System.setProperty("https.protocols", "TLSv1.2");
		System.out.println("https.protocols"+System.getProperty("https.protocols"));
		System.setProperty("proxySet","true");
		//System.setProperty("proxyHost", "10.81.115.150");
		System.setProperty("proxyHost", "10.136.64.150");
		System.setProperty("proxyPort", "80");

		try {

			if(gateway!=null){

				/*String token = gateway.clientToken().generate();
				System.out.println("token="+token);*/
				ClientTokenRequest clientTokenRequest = new ClientTokenRequest().merchantAccountId(merchantAccountId);
				String clientToken = gateway.clientToken().generate(clientTokenRequest);
				System.out.println("clientToken="+clientToken);

				doPayment(customerID);
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

	public  static void doPayment(String customerID) {

		TransactionRequest request = new TransactionRequest()
				.customerId(customerID)
				.paymentMethodNonce(nonce)
				.amount(new BigDecimal("30.00"));

		Result<Transaction> result = gateway.transaction().sale(request);

		System.out.println("Payment for customerID:"+result.getMessage()+","
				+result.getErrors()+","//+result.getTransaction().getId()
				+result.getTarget().getId()+","
				+result.getTarget().getPaymentInstrumentType()+","
				+result.getTarget().getType()+","
				+result.getTarget().getAuthorizedTransactionId()+","
				+result.getTarget().getProcessorAuthorizationCode()+","
				+result.getTarget().getProcessorResponseCode()+","
				+result.getTarget().getProcessorResponseText()+","
				+result.getTarget().getProcessorSettlementResponseCode()+","
				+result.getTarget().getProcessorSettlementResponseText()+","
				);
		findTransaction(customerID,result.getTarget().getId());
	}
	public  static void findTransaction(String customerID,String transid){

		Transaction result = gateway.transaction().find(transid);
		System.out.println("findTransaction="+result.getId()+","
				+result.getAmount()+","
				+result.getPaymentInstrumentType()+","
				+result.getEscrowStatus()+","
				+result.getCreditCard().getMaskedNumber()+","
				+result.getCreditCard().getUniqueNumberIdentifier()+","
				+result.getCreditCard().getExpirationDate()+","
				+result.getCreditCard().getToken()+","
				+result.getCreditCard().getCardType()+","
				+result.getCreditCard().isExpired()
				);

		TransactionSearchRequest search= new TransactionSearchRequest()
				.customerId().is(customerID);
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
