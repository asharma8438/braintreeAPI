package com.billpay;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;

import org.omg.stub.java.rmi._Remote_Stub;

import com.braintreegateway.AndroidPayCard;
import com.braintreegateway.ApplePayCard;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.Result;
import com.braintreegateway.ValidationError;
import com.braintreegateway.VisaCheckoutCard;

public class CustomerVaultOperations {

	private static String MerchantID = "pds8jy3923jg899g";
	private static String PublicKey  = "y8pgk7d9ybcfv8ps";
	private static String PrivateKey = "fbbfb6311c6761631f0562dbba66e5f8";
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.SANDBOX,
			MerchantID,
			PublicKey,
			PrivateKey);

	static String customerID="F5";
	static String PMT="7pk5tx";
	static String nonce="fake-valid-nonce";
	static String nonce1="tokencc_bj_6dhjxr_9qcfqf_zyt55f_5dkd3v_zx2";

	public static void main(String args[]){

		System.out.println(gateway);
		System.setProperty("https.protocols", "TLSv1.2");
		System.out.println("https.protocols="+System.getProperty("https.protocols"));
		//System.setProperty("proxySet","true");
		//System.setProperty("proxyHost", "10.136.64.150");
		//System.setProperty("proxyPort", "80");
		try {

			if(gateway!=null){

				/*String token = gateway.clientToken().generate();
				System.out.println("token="+token);
				ClientTokenRequest clientTokenRequest = new ClientTokenRequest()
						.customerId(customerID);
				String clientToken = gateway.clientToken().generate(clientTokenRequest);
				System.out.println("clientToken="+clientToken);*/

				//createNewCustomerVault(customerID);
				//findCustomer(customerID);
				//updateCustomer(customerID);
				//deleteCustomer(customerID);
				addPaymentMethod(customerID);
				//findCustomerPaymentMethod(PMT);
				//updatePaymentMethod(PMT);
				//deletePaymentMethod(PMT);
				//findPaymentOption("");
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

	public static void createNewCustomerVault(String customerID){
		try{
			CustomerRequest request = new CustomerRequest()
					.id(customerID)
					.firstName("")
					.lastName("")
					.paymentMethodNonce(nonce)
					.deviceData("")
					.fraudMerchantId("")
					.deviceSessionId("")
					.email("")
					.creditCard()
					.cardholderName("")
					//.billingAddress()
					//.streetAddress("")
					//.region("")
					//.postalCode("")
					//.done()
					.options()
					.makeDefault(true)
					.done()
					.done();

			Result<Customer> result = gateway.customer().create(request);

			System.out.println("Transaction="+result.getTransaction()+",Target="+result.getTarget()+",Message="+result.getMessage()
			+",Errors="+result.getErrors()+",Parameters="+result.getParameters()+",CCardV="+result.getCreditCardVerification()
			+",Subscription="+result.getSubscription());

			if(result !=null){
				if(	result.isSuccess()==true){
					System.out.println("Customer is created. PaymentOption="+findPaymentOption(result.getTarget().getDefaultPaymentMethod().getImageUrl()));
					findCustomer(customerID);

				}
				else
				{
					System.out.println("Couldnt Create customer");
					if(result.getErrors()!=null){
						System.out.println("Errors Count="+result.getErrors().size()+
								",DeepSize="+result.getErrors().deepSize());
						for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
							System.out.println("DeepValidationErrors="+error.getAttribute()+","+error.getMessage());
						}
						for (ValidationError error : result.getErrors().getAllValidationErrors()) {
							System.out.println("ValidationErrors="+error.getAttribute()+","+error.getMessage());
						}
					}

				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateCustomer(String customerID){
		//get the existing customer object from vault
		Customer customer = findCustomer(customerID);

		CustomerRequest request = new CustomerRequest()
				.id(customerID)
				.deviceData("deviceData")
				.email("abc@xyz.com")
				.firstName("First")
				.lastName("Last")
				.phone("0404040404")
				.paymentMethodNonce(nonce)
				.creditCard()
				.billingAddress()
				.streetAddress("streetAddress")
				.region("region")
				.postalCode("")
				.done()
				.options()
				.makeDefault(true)
				.updateExistingToken("cd8d5s")
				.done()
				.done()
				;

		Result<Customer> result = gateway.customer().update(customerID, request);

		if(result !=null){
			if(	result.isSuccess()==true){
				/*//Get the credit card that is added now.
				CreditCard c =result.getTarget().getCreditCards().get(0);
				//Existing List of cards
				List<CreditCard> ec=customer.getCreditCards();

				 Iterate through the existing cards to check if the new card is already in vault
				 * if yes, delete the old PMT 
				 
				for(int i=0;i<ec.size();i++){
					String ecUID=ec.get(i).getUniqueNumberIdentifier();
					System.out.println("ecUID="+ecUID);
					String cUID=c.getUniqueNumberIdentifier();
					System.out.println("newly created cUID="+cUID);
					if (cUID.equals(ecUID))
					{
						String newPMT = c.getToken(); 
						String oldPMT = ec.get(i).getToken();
						System.out.println("newPMT="+newPMT+",oldPMT="+oldPMT+
								",NewMaskedCC="+c.getMaskedNumber()+",oldMaskedCC="+ec.get(i).getMaskedNumber());
						//same card is being added, deleting old PMT - Delete in Telstra Vault as well
						Result<? extends PaymentMethod> pm = gateway.paymentMethod().delete(oldPMT);
						System.out.println("Existing CC PMT Deletion status="+pm.isSuccess());
					}
				}
				String newPMT=c.getToken();
				System.out.println("newPMT="+newPMT);

				//Make the newly added credit card as default

				PaymentMethodRequest updateRequest = new PaymentMethodRequest()
						  .paymentMethodToken(newPMT)
						  .options()
						  .makeDefault(true)
						  .done();

				CustomerRequest request1 = new CustomerRequest()
						.defaultPaymentMethodToken(newPMT);

				result =gateway.customer().update(customerID, request1);   */
				System.out.println("DefaultPMT="+result.getTarget().getDefaultPaymentMethod().getToken());

				findCustomer(result.getTarget().getId());
			}
			else
			{
				System.out.println("Couldnt Update customer");
				if(result.getErrors()!=null){
					System.out.println("Errors Count="+result.getErrors().size()+
							",DeepSize="+result.getErrors().deepSize());
					for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
						System.out.println("DeepValidationErrors="+error.getAttribute()+","+error.getMessage());
					}
					for (ValidationError error : result.getErrors().getAllValidationErrors()) {
						System.out.println("ValidationErrors="+error.getAttribute()+","+error.getMessage());
					}
				}

			}
		}
	}

	public static Customer findCustomer(String customerID){


		Customer customer = gateway.customer().find(customerID);

		if(customer!=null){
			String paymentOption=findPaymentOption(customer.getDefaultPaymentMethod().getImageUrl());
			System.out.println("findcustomer: Customer_ID="+customer.getId()+",PaymentOption="+paymentOption+ ",DefaultPaymentMethodToken="+customer.getDefaultPaymentMethod().getToken()
					+",Email="+customer.getEmail()+",FirstN="+customer.getFirstName()+",LastN="+customer.getLastName()+",CreatedAt="+dateformat.format(customer.getCreatedAt().getTime()).trim());
			ListIterator<CreditCard> ccl =customer.getCreditCards().listIterator();
			ListIterator<AndroidPayCard> anl =customer.getAndroidPayCards().listIterator();
			ListIterator<VisaCheckoutCard> vcl =customer.getVisaCheckoutCards().listIterator();
			ListIterator<ApplePayCard> apl =customer.getApplePayCards().listIterator();

			while(ccl.hasNext()){
				CreditCard c = ccl.next();
				System.out.println(",CUST_ID="+c.getCustomerId()+",Bin="+c.getBin()+",Last4="+c.getLast4()+",PMT="+c.getToken()+",Default="+c.isDefault()+",CreatedAt="
						+dateformat.format(c.getCreatedAt().getTime()).trim()+",UpdatedAt="+dateformat.format(c.getUpdatedAt().getTime()).trim()
						+",CardType="+c.getCardType()+",EX_M="+c.getExpirationMonth()+",EX_Y="+c.getExpirationYear()
						+",BillingAddress="+c.getBillingAddress().getRegion()+",CardholderName="+c.getCardholderName()+",Masked_PAN="+c.getMaskedNumber()+",UID="+c.getUniqueNumberIdentifier()+",SUB="+c.getSubscriptions()+",Expired="+c.isExpired()+",PrePaidCard="+c.getPrepaid()+",ImageURL="+c.getImageUrl());

			}
			while(vcl.hasNext()){
				VisaCheckoutCard c = vcl.next();
				System.out.println(",CUST_ID="+c.getCustomerId()+"Bin="+c.getBin()+",Last4="+c.getLast4()+",PMT="+c.getToken()+",Default="+c.isDefault()+",CreatedAt="
						+dateformat.format(c.getCreatedAt().getTime()).trim()+",UpdatedAt="+dateformat.format(c.getUpdatedAt().getTime()).trim()
						+",CardType="+c.getCardType()+",EX_M="+c.getExpirationMonth()+",EX_Y="+c.getExpirationYear()
						+",BillingAddress="+c.getBillingAddress().getRegion()+",CardholderName="+c.getCardholderName()+",Masked_PAN="+c.getMaskedNumber()+",UID="+c.getUniqueNumberIdentifier()+",SUB="+c.getSubscriptions()+",Expired="+c.isExpired()+",PrePaidCard="+c.getPrepaid()+",ImageURL="+c.getImageUrl());

			}
			while(anl.hasNext()){
				AndroidPayCard c = anl.next();
				System.out.println(",CUST_ID="+c.getCustomerId()+"Bin="+c.getBin()+",Last4="+c.getLast4()+",PMT="+c.getToken()+",Default="+c.isDefault()+",CreatedAt="
						+dateformat.format(c.getCreatedAt().getTime()).trim()+",UpdatedAt="+dateformat.format(c.getUpdatedAt().getTime()).trim()
						+",CardType="+c.getCardType()+",EX_M="+c.getExpirationMonth()+",EX_Y="+c.getExpirationYear()
						+",SourceCardType="+c.getSourceCardType()+",SourceCardLast4="+c.getSourceCardLast4()+",SourceDescription="+c.getSourceDescription()+",GoogleTransactionId="+c.getGoogleTransactionId()+",VirtualCardLast4="+c.getVirtualCardLast4()+",VirtualCardType"+c.getVirtualCardType()+",Sub="+c.getSubscriptions()+",ImageURL="+c.getImageUrl());

			}
			while(apl.hasNext()){
				ApplePayCard c = apl.next();
				System.out.println(",CUST_ID="+c.getCustomerId()+"Bin="+c.getBin()+",Last4="+c.getLast4()+",PMT="+c.getToken()+",Default="+c.isDefault()+",CreatedAt="
						+dateformat.format(c.getCreatedAt().getTime()).trim()+",UpdatedAt="+dateformat.format(c.getUpdatedAt().getTime()).trim()
						+",CardType="+c.getCardType()+",EX_M="+c.getExpirationMonth()+",EX_Y="+c.getExpirationYear()
						+",PaymentInstrumentName="+c.getPaymentInstrumentName()+",SourceDescription="+c.getSourceDescription()+",Expired="+c.getExpired()+",SUB="+c.getSubscriptions()+",ImageURL="+c.getImageUrl());

			}

			List<? extends PaymentMethod> pm= customer.getPaymentMethods();
			/*paymentMethods.forEach(s -> System.out.println(s));*/
			for(int i=0; i< pm.size();i++){
				System.out.println("PaymentMethodsList: PMT="+pm.get(i).getToken()+",Default="+pm.get(i).isDefault()+",ImageUrl="+pm.get(i).getImageUrl());			
			}

		}
		else
			System.out.println("Couldnt find customer");
		return customer;
	}

	public static void findCustomerPaymentMethod(String PMT){

		PaymentMethod paymentMethod = gateway.paymentMethod().find(PMT);

		if(paymentMethod!=null)
			System.out.println("PaymentMethod - "+paymentMethod.getToken()
			+ " ,belongs to CustomerID: "+paymentMethod.getCustomerId()+",isDefault="+paymentMethod.isDefault());
		//paymentMethod.getSubscriptions().
		findCustomer(paymentMethod.getCustomerId());

	}

	public static void deleteCustomer(String customerID){
		Result<Customer> result = gateway.customer().delete(customerID);
		System.out.println("DeleteCustomer status="+result.isSuccess());
	}

	public static void deletePaymentMethod(String PMT){

		Result<? extends PaymentMethod> result = gateway.paymentMethod().delete(PMT);
		System.out.println("PaymentMethodDeletion status="+result.isSuccess());
	}

	public static void updatePaymentMethod(String PMT){
		PaymentMethodRequest updateRequest = new PaymentMethodRequest()
				.customerId("customerId")
				.paymentMethodToken(PMT)
				.paymentMethodNonce(nonce)
				.deviceData("deviceData")
				.fraudMerchantId("fraudMerchantId")
				.deviceSessionId("deviceSessionId")
				.cardholderName("cardholderName")
				.options()
				.makeDefault(true)
				.done();
		Result<? extends PaymentMethod> result = gateway.paymentMethod().update(PMT, updateRequest);
		System.out.println("UpdatePaymentMethod status="+result.isSuccess()+","
				+result.getTarget().getToken()+",isDefault="+result.getTarget().isDefault());
		findCustomer(result.getTarget().getCustomerId());
	}

	public static void addPaymentMethod(String customerID){
		PaymentMethodRequest request = new PaymentMethodRequest()
				.customerId(customerID)
				.paymentMethodNonce(nonce)
				.deviceData("deviceData")
				.fraudMerchantId("fraudMerchantId")
				.deviceSessionId("deviceSessionId")
				.cardholderName("cardholderName")
				.options()
				.makeDefault(true)
				.done();

		Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);
		if(result.isSuccess()==true){
			System.out.println("addPaymentMethod: Success="+result.isSuccess()+",Message="+result.getMessage()+
					",CreditCardVerif="+result.getCreditCardVerification()+",Sub="+result.getSubscription()
					+",Target="+result.getTarget()+",Transaction="+result.getTransaction());
			findCustomer(result.getTarget().getCustomerId());
		}
		else
		{
			System.out.println("Couldnt create payment method");
			if(result.getErrors()!=null){
				System.out.println("Errors Count="+result.getErrors().size()+
						",DeepSize="+result.getErrors().deepSize());
				List<ValidationError> Verr = result.getErrors().getAllValidationErrors();
				List<ValidationError> Derr = result.getErrors().getAllDeepValidationErrors();
				for(int i=0; i< Verr.size();i++){
					System.out.println("ValidationErrors="+Verr.get(i).getAttribute()+","+Verr.get(i).getMessage()+","+Verr.get(i).getCode());			
				}
				for(int i=0; i< Derr.size();i++){
					System.out.println("DeepValidationErrors="+Derr.get(i).getAttribute()+","+Derr.get(i).getMessage()+","+Derr.get(i).getCode());			
				}
			}

		}

		/*Collection c = result.getParameters().values();
		    Iterator itr = c.iterator();
		    while (itr.hasNext()) {
		      System.out.println(itr.next());
		    }*/
	}

	public static String findPaymentOption(String ImageURL){
		//ImageURL="https://assets.braintreegateway.com/payment_method_logo/android_pay_card.png?environment=sandbox";
		String payOption=null;
		if(ImageURL!=null && !ImageURL.equals(""))
		{
			payOption= ImageURL.split("payment_method_logo/")[1].split(".png")[0];
			System.out.println("payOption="+payOption);
		}

		return payOption;
	}

}
