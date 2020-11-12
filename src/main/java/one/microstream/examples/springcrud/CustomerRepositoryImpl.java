
package one.microstream.examples.springcrud;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;


@Component
public class CustomerRepositoryImpl implements CustomerRepository
{
	private final List<Customer>         customers;
	private final EmbeddedStorageManager storage;
	
	public CustomerRepositoryImpl(@Value("${microstream.store.location}") final String location)
	{
		super();
		
		this.customers = new ArrayList<>();
		
		this.storage   = EmbeddedStorage.start(
			this.customers,
			Paths.get(location)
		);
	}
	
	@Override
	public void storeAll()
	{
		this.storage.store(this.customers);
	}
	
	@Override
	public void add(final Customer customer)
	{
		this.customers.add(customer);
		this.storeAll();
	}
	
	@Override
	public List<Customer> findAll()
	{
		return this.customers;
	}
	
	@Override
	public void deleteAll()
	{
		this.customers.clear();
		this.storeAll();
	}
	
	@Override
	public List<Customer> findByFirstName(final String firstName)
	{
		return this.customers.stream()
			.filter(c -> c.getFirstName().equals(firstName))
			.collect(Collectors.toList());
	}
}
