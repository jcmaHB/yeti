
package one.microstream.examples.springcrud;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application
{
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	
	public static void main(final String[] args)
	{
		final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class);
		ctx.close();
		System.exit(0);
	}
	
	@Bean
	public CommandLineRunner crudDemo(final CustomerRepository repository)
	{
		return (args) ->
		{
			long customerNr = 1L;
			repository.add(new Customer(customerNr++, "Thomas", "Wresler"));
			repository.add(new Customer(customerNr++, "Jim"   , "Joe"));
			repository.add(new Customer(customerNr++, "Kamil" , "Limitsky"));
			repository.add(new Customer(customerNr++, "Karel" , "Ludvig"));
			
			final Consumer<Customer> logAll = c -> LOG.info(c.toString());
			
			LOG.info("Our customers:");
			repository.findAll().forEach(logAll);
			LOG.info(" ");
			
			LOG.info("Find some specific customer:");
			repository.findByFirstName("Karel").forEach(logAll);
			LOG.info(" ");
			
			LOG.info("Update name of all customers:");
			repository.findAll().forEach(c -> c.setFirstName("Johan"));
			repository.storeAll();
			repository.findAll().forEach(logAll);
			LOG.info(" ");
			
			LOG.info("Delete customers:");
			repository.deleteAll();
			repository.findAll().forEach(logAll);
			LOG.info(" ");
		};
	}
}
