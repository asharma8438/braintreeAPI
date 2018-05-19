package com.billpay;

import java.util.List;
import java.util.ListIterator;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.Result;
import com.braintreegateway.ValidationError;

public class CustomerVaultOperations {

	private static String MerchantID = "pds8jy3923jg899g";
	private static String PublicKey  = "y8pgk7d9ybcfv8ps";
	private static String PrivateKey = "fbbfb6311c6761631f0562dbba66e5f8";

	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.SANDBOX,
			MerchantID,
			PublicKey,
			PrivateKey);

	static String customerID="C1";
	static String PMT="9hyps7";
	static String fakenonce="fake-valid-nonce";
	static String nonce="tokencc_bj_h8k5sq_vyxh43_r9pgcy_dkgv3t_j25";

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
				//addPaymentMethod(customerID);
				//findCustomerPaymentMethod(PMT);
				//updatePaymentMethod(PMT);
				//deletePaymentMethod(PMT);
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
					.paymentMethodNonce(nonce)
					.deviceData("deviceData")
					.email("abc@xyz.com")
					.firstName("")
					.lastName("");

			Result<Customer> result = gateway.customer().create(request);

			System.out.println(result.getMessage());
			if(result !=null){
				if(	result.isSuccess()==true){
					System.out.println("Customer is created.");
					Customer customer = findCustomer(customerID);

					List<? extends PaymentMethod> pm= customer.getPaymentMethods();
					/*paymentMethods.forEach(s -> System.out.println(s));*/
					for(int i=0; i< pm.size();i++){
						System.out.println(pm.get(i).getCustomerId());			
					}
				}
				else
				{
					System.out.println("Couldnt Create customer");
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
				.paymentMethodNonce(nonce)
				.deviceData("deviceData")
				.email("abc@xyz.com")
				.firstName("")
				.lastName("");

		Result<Customer> result = gateway.customer().update(customerID, request);

		if(result !=null){
			if(	result.isSuccess()==true){
				//Get the credit card that is added now.
				CreditCard c =result.getTarget().getCreditCards().get(0);
				//Existing List of cards
				List<CreditCard> ec=customer.getCreditCards();

				/* Iterate through the existing cards to check if the new card is already in vault
				 * if yes, delete the old PMT 
				 */
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

				/*PaymentMethodRequest updateRequest = new PaymentMethodRequest()
						  .paymentMethodToken(newPMT)
						  .options()
						  .makeDefault(true)
						  .done();*/

				CustomerRequest request1 = new CustomerRequest()
						.defaultPaymentMethodToken(newPMT);

				result =gateway.customer().update(customerID, request1);     
				System.out.println("DefaultPMT="+result.getTarget().getDefaultPaymentMethod().getToken());

				Customer customer1 = result.getTarget();
				CreditCard cc = result.getTarget().getCreditCards().get(0);
				System.out.println("Customer is updated. ResponseDetails,"+cc.getBin()+","+cc.getCardholderName()+","+cc.getCardType()
				+","+cc.getCustomerId()+","+cc.getExpirationDate()+","+cc.getLast4()+","+cc.getMaskedNumber()
				+","+cc.getToken()+","+cc.getUniqueNumberIdentifier()+","+cc.isDefault()+","+cc.isExpired()+","+cc.getPrepaid());

				List<? extends PaymentMethod> pm= customer1.getPaymentMethods();
				/*paymentMethods.forEach(s -> System.out.println(s));*/
				for(int i=0; i< pm.size();i++){
					System.out.println("PMT="+pm.get(i).getToken()+",Default="+pm.get(i).isDefault());			
				}
			}
			else
			{
				System.out.println("Couldnt Update customer");
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
		}
	}

	public static Customer findCustomer(String customerID){

		Customer customer = gateway.customer().find(customerID);
		if(customer!=null)
			System.out.println("findcustomer - "
					+ "DefaultPaymentMethodToken="+customer.getDefaultPaymentMethod().getToken());
		ListIterator<CreditCard> litr =customer.getCreditCards().listIterator();
		while(litr.hasNext()){
			CreditCard c = litr.next();
			System.out.println(c.getBin()+","+c.getCardholderName()+","+c.getCardType()
			+","+c.getCustomerId()+","+c.getExpirationDate()+","+c.getLast4()+","+c.getMaskedNumber()
			+","+c.getToken()+","+c.getUniqueNumberIdentifier()+","+c.isDefault()+","+c.isExpired()+","+c.getPrepaid());

		}
		return customer;
	}

	public static void findCustomerPaymentMethod(String PMT){

		PaymentMethod paymentMethod = gateway.paymentMethod().find(PMT);

		if(paymentMethod!=null)
			System.out.println("PaymentMethod - "+paymentMethod.getToken()
			+ " ,belongs to CustomerID: "+paymentMethod.getCustomerId()+",isDefault="+paymentMethod.isDefault());
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
				.paymentMethodToken(PMT)
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
				.cardholderName("Ash")
				.customerId(customerID)
				.paymentMethodNonce(nonce)
				.options()
				.failOnDuplicatePaymentMethod(true)	 
				.done();

		Result<? extends PaymentMethod> result = gateway.paymentMethod().create(request);
		if(result.isSuccess()==true){
			System.out.println("addPaymentMethod="+result.isSuccess()+","+findCustomer(result.getTarget().getCustomerId()));
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


}
