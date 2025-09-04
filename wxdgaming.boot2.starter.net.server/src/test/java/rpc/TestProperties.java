package rpc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import wxdgaming.boot2.core.BootstrapProperties;

@ConfigurationProperties(prefix = "boot")
public class TestProperties extends BootstrapProperties {

}
