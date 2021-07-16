package rawinng.lab.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import rawinng.lab.batch.model.Hello;
import rawinng.lab.batch.repo.HelloRepository;

@EnableBatchProcessing
@SpringBootApplication
public class LabPracticeBatchApplication {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired 
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private HelloRepository helloRepository;

	@Autowired
	private PlatformTransactionManager txMgr;

	public static void main(String[] args) {
		SpringApplication.run(LabPracticeBatchApplication.class, args);
	}

	@Bean
	public Job job1() {
		return this.jobBuilderFactory.get("job1")
					.incrementer(new RunIdIncrementer())
					.start(step1())
					.build();
					
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
		.transactionManager(txMgr)
		.allowStartIfComplete(true)
		.<Hello, Hello>chunk(10)
		.reader(reader())
		.processor(process())
		.writer(writer())
		.build();		
	}

	@Bean
	public JsonItemReader<Hello> reader() {
		return new JsonItemReaderBuilder<Hello>()
			.jsonObjectReader(new JacksonJsonObjectReader<>(Hello.class))
			.resource(new ClassPathResource("hello.json"))
			.name("reader1")
			.build();
	} 

	@Bean 
	public RepositoryItemWriter<Hello> writer() {
		return new RepositoryItemWriterBuilder<Hello>()
			.repository(helloRepository)
			.build();
	}

	@Bean 
	public ItemProcessor<Hello, Hello> process() {
		return new ItemProcessor<Hello,Hello>(){
			@Override
			public Hello process(Hello item) throws Exception {
				System.out.println(item);
				return item;
			}
		};
	}
}
