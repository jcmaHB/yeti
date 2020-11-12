
package one.microstream.examples.springcrud;

public class Customer
{
	private final long customerNumber;
	private String     firstName;
	private String     lastName;
	
	public Customer(final long customerNumber, final String firstName, final String lastName)
	{
		super();
		
		this.customerNumber = customerNumber;
		this.firstName      = firstName;
		this.lastName       = lastName;
	}
	
	public long getCustomerNumber()
	{
		return this.customerNumber;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}
	
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}
	
	@Override
	public String toString()
	{
		return "Customer [customerNumber=" + this.customerNumber
			+ ", firstName=" + this.firstName
			+ ", lastName=" + this.lastName + "]";
	}
}
