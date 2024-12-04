package com.springfeature.springfeature.config;


import com.springfeature.springfeature.model.Student;
import com.springfeature.springfeature.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {


    private StudentRepository studentRepository;

    //reader
    @Bean
    public FlatFileItemReader<Student> reader() {
        FlatFileItemReader<Student> reader = new FlatFileItemReader<>();
        reader.setResource (new FileSystemResource("src/main/resources/student.csv")); //
        reader.setName("csv-reader");
        reader.setLinesToSkip (1);
        reader.setLineMapper (lineMapper());
        return reader;
    }


    @Bean
    public LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
        BeanWrapperFieldSetMapper<Student> studentFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        studentFieldSetMapper.setTargetType(Student.class);
        lineMapper.setLineTokenizer (delimitedLineTokenizer);
        lineMapper.setFieldSetMapper(studentFieldSetMapper);
        return lineMapper;
    }

    //processor

    @Bean
    public StudentProcessor processor(){
        return  new StudentProcessor();
    }

    //write

    public RepositoryItemWriter<Student> writer(){
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<Student>();
        writer.setRepository (studentRepository);
        writer.setMethodName("save");
        return writer;
    }


    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Student, Student>chunk(10, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(10);
        return simpleAsyncTaskExecutor;
    }


    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("importCSV", jobRepository)
                .flow(step1(jobRepository, platformTransactionManager))
                .end()
                .build();
    }

}
