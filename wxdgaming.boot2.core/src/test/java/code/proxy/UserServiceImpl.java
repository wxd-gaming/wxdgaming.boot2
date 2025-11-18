package code.proxy;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

    public void addUser(String name) {
        System.out.println("添加用户：" + name);
    }

}
