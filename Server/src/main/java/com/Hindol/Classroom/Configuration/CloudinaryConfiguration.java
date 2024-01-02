package com.Hindol.Classroom.Configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfiguration {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dvpulu3cc");
        config.put("api_key", "516337169296598");
        config.put("api_secret", "t_faj3i3WIEmu3lG8eWsdJlPpik");
        config.put("secure", "true");

        return new Cloudinary(config);
    }
}
