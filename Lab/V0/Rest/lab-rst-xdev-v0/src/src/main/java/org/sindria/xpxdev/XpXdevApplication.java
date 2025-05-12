package org.sindria.xpxdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class XpXdevApplication {

	public static void main(String[] args) {
		SpringApplication.run(XpXdevApplication.class, args);
	}

}
