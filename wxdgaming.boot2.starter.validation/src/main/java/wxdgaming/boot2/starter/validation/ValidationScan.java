package wxdgaming.boot2.starter.validation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-15 09:12
 **/
@ComponentScan(basePackageClasses = {ValidationUtil.class})
@Component
public class ValidationScan {
}
