package com.springfeature.springfeature.config;

import com.springfeature.springfeature.model.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student,Student> {

    @Override
    public Student process(Student student) throws Exception {
        return student;
    }
}
