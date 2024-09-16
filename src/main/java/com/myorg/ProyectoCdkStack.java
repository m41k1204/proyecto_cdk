package com.myorg;

import software.amazon.awscdk.services.iam.IRole;
import software.constructs.Construct;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;



import java.util.List;
import java.util.Map;

public class ProyectoCdkStack extends Stack {

    public ProyectoCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        CfnParameter instanceName = CfnParameter.Builder.create(this, "InstanceName")
                .type("String")
                .defaultValue("MV Websimple & Webplantilla")
                .description("Website Websimple & Webplantilla")
                .build();

        IVpc vpc = Vpc.fromLookup(this, "VPC", VpcLookupOptions.builder()
                .isDefault(true)
                .build());


        CfnParameter amiId = CfnParameter.Builder.create(this, "AMI")
                .type("String")
                .defaultValue("ami-0aa28dab1f2852040")
                .description("id de AMI")
                .build();


        SecurityGroup securityGroupAdder = SecurityGroup.Builder.create(this, "InstanceSecurityGroup")
                .vpc(vpc)
                .description("ssh y http")
                .allowAllOutbound(true)
                .build();

        securityGroupAdder.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH");
        securityGroupAdder.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP");

        IRole lab_role_arn = Role.fromRoleArn(this, "ExistingRole", "arn:aws:iam::484885772849:role/LabRole");

        Instance ec2Instance = Instance.Builder.create(this, "EC2Instance")
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.MICRO))
                .machineImage(MachineImage.genericLinux(Map.of("us-east-1", amiId.getValueAsString())))
                .securityGroup(securityGroupAdder)
                .keyName("vockey")
                .blockDevices(List.of(BlockDevice.builder()
                        .deviceName("/dev/sda1")
                        .volume(BlockDeviceVolume.ebs(20))
                        .build()))
                .role(lab_role_arn)
                .build();

        ec2Instance.addUserData(
                "#!/bin/bash",
                "cd /var/www/html/",
                "git clone https://github.com/utec-cc-2024-2-test/websimple.git",
                "git clone https://github.com/utec-cc-2024-2-test/webplantilla.git",
                "ls -l"
        );

        CfnOutput.Builder.create(this, "InstanceId")
                .description("ID de la instancia EC2")
                .value(ec2Instance.getInstanceId())
                .build();

        CfnOutput.Builder.create(this, "InstancePublicIP")
                .description("IP publica de la instancia")
                .value(ec2Instance.getInstancePublicIp())
                .build();

        CfnOutput.Builder.create(this, "websimpleURL")
                .description("URL de websimple")
                .value("http://" + ec2Instance.getInstancePublicIp() + "/websimple")
                .build();

        CfnOutput.Builder.create(this, "webplantillaURL")
                .description("URL de webplantilla")
                .value("http://" + ec2Instance.getInstancePublicIp() + "/webplantilla")
                .build();
    }
}
