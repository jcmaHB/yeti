# MicroStream Crud Spring Example

This example shows how to use MicroStream with Spring. 
The goal is just to provide a simple example how to connect these two frameworks.

## pom.xml
At first we need a new Maven project. The MicroStream repository must be added into the 
pom.xml. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>

    <groupId>one.microstream</groupId>
    <artifactId>microstream-spring-crud</artifactId>
    <version>0.0.1</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.0.RELEASE</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <microstream.version>04.00.00-MS-GA</microstream.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>one.microstream</groupId>
            <artifactId>storage.embedded</artifactId>
            <version>${microstream.version}</version>
        </dependency>
        <dependency>
            <groupId>one.microstream</groupId>
            <artifactId>storage.embedded.configuration</artifactId>
            <version>${microstream.version}</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>microstream-maven-releases</id>
            <url>https://repo.microstream.one/repository/maven-public/</url>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## Java Code
After creating the Maven project we need to add four Java files.

Customer Pojo:

```java
public class Customer
{
    private String firstName;
    private String lastName;
    private Long   customerNumber;
    
    public Customer(final String firstName, final String lastName, final Long customerNumber)
    {
        super();
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.customerNumber = customerNumber;
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
    
    public Long getCustomerNumber()
    {
        return this.customerNumber;
    }
    
    public void setCustomerNumber(final Long customerNumber)
    {
        this.customerNumber = customerNumber;
    }
    
    @Override
    public String toString()
    {
        return "Customer [firstName=" + this.firstName + ", lastName=" + this.lastName + ", customerNumber="
            + this.customerNumber + "]";
    }
}
```

Customer repository:

```java
public interface CustomerRepository
{
    public void add(Customer customer);
    
    public List<Customer> findAll();
    
    public List<Customer> findByFirstName(String firstName);
    
    public void deleteAll();
    
    public void storeAll();
}
```

And its implementation:

```java
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
```

In the constructor we start our storage with root object customers and as second parameter we provide the location, where data should be stored.
The @Value annotation is a Spring annotation which injects the value from the configuration file.
To save our data we just call storage.store(Object). 
The method findByFirstName shows the mechanism to obtain data from MicroStream.
Instead of writing some query in SQL or other query language, we just use plain old Java.

And at last the Spring application itself:

```java
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
            repository.add(new Customer("Thomas", "Wresler" , customerNr++));
            repository.add(new Customer("Jim"   , "Joe"     , customerNr++));
            repository.add(new Customer("Kamil" , "Limitsky", customerNr++));
            repository.add(new Customer("Karel" , "Ludvig"  , customerNr++));
            
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
```

Now we need one more file: application.properties. Here we define the path for the storage files, where MicroStream should save our data.
This setting is used in the CustomerRepositoryImpl's constructor. 
 
```
microstream.store.location=${user.home}/microstream-spring-crud-store
```
