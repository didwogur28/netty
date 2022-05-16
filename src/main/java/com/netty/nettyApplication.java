package com.netty;

import com.netty.net.EchoMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class })
@ConfigurationProperties(prefix = "info.app")
public class nettyApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(nettyApplication.class, args);
        EchoMain echoMain = context.getBean(EchoMain.class);

        try{
            echoMain.start(args);
        }catch (Exception e){

        }
    }

    @Bean(name = "echoMain")
    public EchoMain echoMain() {
        EchoMain echoMain = new EchoMain();
        return echoMain;
    }
}
