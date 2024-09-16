package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class ProyectoCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new ProyectoCdkStack(app, "ProyectoCdkStack", StackProps.builder()
                .env(Environment.builder()
                        .account("484885772849")
                        .region("us-east-1")
                        .build())

                .build());

        app.synth();
    }
}

