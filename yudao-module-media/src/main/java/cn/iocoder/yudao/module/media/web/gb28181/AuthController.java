package cn.iocoder.yudao.module.media.web.gb28181;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@Hidden
public class AuthController {

    @GetMapping("/login")
    public String devices(String name, String passwd) {
        return "success";
    }
}
