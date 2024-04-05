package full.stack.chatter.controller;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.ServicesRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {
    @Resource
    private ServicesRequest servicesRequest;

    @PostMapping(value = "/create-normal-user")
    public void createNormalUser() {
        NormalUser normal_user = new NormalUser();
        normal_user.setUser("Cédric", "Martinet", "aa@pp.com", "1234", false);
        servicesRequest.addNormalUser(normal_user);
    }

    @GetMapping(value = "/list-normal-users")
    public List<NormalUser> getNormalUsers() {
        return servicesRequest.getNormalUsers();
    }

    @PostMapping(value = "/create-admin-user")
    public void createAdminUser() {
        AdminUser admin_user = new AdminUser();
        admin_user.setUser("Cédric", "Martinet", "bcdf@pp.com", "1234", false);
        System.out.println(admin_user.getEmail());
        servicesRequest.addAdminUser(admin_user);
    }

    @GetMapping(value = "/list-admin-users")
    public List<AdminUser> getAdminUsers() {
        return servicesRequest.getAdminUsers();
    }
}
