package me.marioscalasm.hsa.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class Salute {
    private final String salute;
}

@RequestMapping("/hello")
@RestController
public class HelloController {
    @GetMapping
    public Salute getSalute(final @RequestParam("name") String name) {
        return new Salute( String.format("Hello, %s!", name));
    }
}
