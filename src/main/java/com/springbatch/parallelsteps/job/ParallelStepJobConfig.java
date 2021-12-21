package com.springbatch.parallelsteps.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@EnableBatchProcessing
@Configuration
public class ParallelStepJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job parallelStepJob(@Qualifier("migrarPessoaStep") Step migrarPessoaStep,
                               @Qualifier("migrarDadosBancariosStep") Step migrarDadosBancariosStep) {
        return jobBuilderFactory
                .get("parallelStepJob")
                .start(stepParalelos(migrarPessoaStep, migrarDadosBancariosStep))
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    private Flow stepParalelos(Step migrarPessoaStep, Step migrarDadosBancariosStep) {
        return new FlowBuilder<Flow>("stepParalelos")
                .start(migrarPessoaFlow(migrarPessoaStep))
                .split(new SimpleAsyncTaskExecutor())
                .add(migrarDadosBancariosFlow(migrarDadosBancariosStep))
                .build();
    }

    private Flow migrarPessoaFlow(Step migrarPessoaStep) {
        return new FlowBuilder<Flow>("migrarPessoaFlow")
                .start(migrarPessoaStep)
                .build();
    }

    private Flow migrarDadosBancariosFlow(Step migrarDadosBancariosStep) {
        return new FlowBuilder<Flow>("migrarDadosBancariosFlow")
                .start(migrarDadosBancariosStep)
                .build();
    }
}
