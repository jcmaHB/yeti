
package one.microstream.examples.springcrud;

import java.util.List;


public interface CustomerRepository
{
	public void add(Customer customer);
	
	public List<Customer> findAll();
	
	public List<Customer> findByFirstName(String firstName);
	
	public void deleteAll();
	
	public void storeAll();
}
